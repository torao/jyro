/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro;

import static org.koiroha.jyro.Jyro.Const.*;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.util.*;
import org.koiroha.jyro.util.Text;
import org.koiroha.xml.DefaultNamespaceContext;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroFactory: Jyro Factory
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The factory class to build Jyro instance from configuration xml.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
final class Configurator {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Configurator.class);

	// ======================================================================
	// Home Directory
	// ======================================================================
	/**
	 * Home directory to configure Jyro instance.
	 */
	private final File dir;

	// ======================================================================
	// Property
	// ======================================================================
	/**
	 * Placeholder map in configuration such as ${foo.bar}.
	 */
	private final Map<String,String> param = new HashMap<String,String>();

	// ======================================================================
	// XPath
	// ======================================================================
	/**
	 * Utility instnce of xpath.
	 */
	private final XPath xpath;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 *
	 * @param dir home directory of jyro
	 */
	public Configurator(File dir){
		this.dir = dir;

		// utility xpath instance that used in this class
		DefaultNamespaceContext nc = new DefaultNamespaceContext();
		nc.setNamespaceURI("j", XMLNS10);
		XPathFactory xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();
		xpath.setNamespaceContext(nc);
		return;
	}

	// ======================================================================
	// Retrieve Class Loader
	// ======================================================================
	/**
	 * Retrieve jyro instance scope classloader. This method returns all
	 * *.jar and *.zip files in ${jyro.home}/lib.
	 *
	 * @param parent parent class loader
	 * @return jyro instance scope class loader
	 * @throws JyroException
	 */
	public ClassLoader getJyroClassLoader(ClassLoader parent) throws JyroException {

		// determine default class loader if specified value is null
		if(parent == null){
			parent = Thread.currentThread().getContextClassLoader();
			if(parent == null){
				parent = ClassLoader.getSystemClassLoader();
			}
		}

		// get class loader for extra libraries
		String classpath = "";
		String libext = new File(dir, DIR_LIB).toString();
		return getLibextLoader(classpath, libext, parent);
	}

	// ======================================================================
	// Create All Nodes
	// ======================================================================
	/**
	 * Create all nodes in this configuration.
	 *
	 * @param parent parent class loader of each nodes
	 * @param init initial property replaced by ${...}
	 * @return taskname-node map
	 * @throws JyroException in case fail to configure
	 */
	public Map<String,List<Node>> createNodes(ClassLoader parent, Properties init) throws JyroException {
		param.clear();	// init parameter

		if(init == null){
			init = new Properties();
		}

		// read jyro configuration xml
		File file = new File(dir, FILE_CONF);
		logger.debug("loading configuration: " + file);
		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setXIncludeAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(file);
		} catch(ParserConfigurationException ex){
			throw new IllegalStateException("bad xml parser environment", ex);
		} catch(IOException ex){
			throw new JyroException("fail to read jyro configuration: " + file, ex);
		} catch(SAXException ex){
			throw new JyroException("fail to read jyro configuration: " + file, ex);
		}

		// retrieve all system properties
		for(Map.Entry<Object,Object> e: System.getProperties().entrySet()){
			param.put(e.getKey().toString(), e.getValue().toString());
		}

		// retrieve all specified properties
		for(Map.Entry<Object,Object> e: init.entrySet()){
			param.put(e.getKey().toString(), e.getValue().toString());
		}

		// retrieve all property settings in configuration
		Element root = doc.getDocumentElement();
		for(Element elem: elemset("j:property", root)){
			String name = elem.getAttribute("name");
			String value = f(elem.getAttribute("value"));
			param.put(name, value);
		}

		// build all nodes and taskname mapping
		Map<String,List<Node>> map = new HashMap<String,List<Node>>();
		for(Element elem: elemset("j:node", root)){
			Node node = buildNode(elem, parent);
			List<Node> list = map.get(node.getTaskName());
			if(list == null){
				list = new ArrayList<Node>();
				map.put(node.getTaskName(), list);
			}
			list.add(node);
		}
		return map;
	}

	// ======================================================================
	// Build Node
	// ======================================================================
	/**
	 * Build node instance from specified element.
	 *
	 * @param elem node element
	 * @param parent default class loader of this node
	 * @return Jyro instance
	 * @throws JyroException fail to build node
	 */
	private Node buildNode(Element elem, ClassLoader parent) throws JyroException {

		// retrieve task name
		String task = f(elem.getAttribute("task"));

		// retrieve node-scope class loader
		String classpath = f(elem.getAttribute("classpath"));
		String extdirs = f(elem.getAttribute("extdirs"));
		ClassLoader loader = getLibextLoader(classpath, extdirs, parent);

		// retrieve worker element
		Worker worker = null;
		Element wk = elem("j:worker", elem);
		if(wk != null){
			worker = createWorker(f(wk.getAttribute("class")), loader);
		} else {
			wk = elem("j:script", elem);
			if(wk != null){
				worker = createScript(loader, wk);
			} else {
				throw new JyroException("no worker found in node definition: " + task);
			}
		}

		// create node implementation
		Node node = new Node(task, loader, worker);

		// set minimum thread-pool size
		if(wk.hasAttribute("min")){

		}
		return node;
	}

	// ======================================================================
	// Create Worker
	// ======================================================================
	/**
	 * Create worker for specified Java class.
	 *
	 * @param clazz class name of worker
	 * @param loader class loader to create worker
	 * @return worker instance
	 * @throws JyroException if fail to create worker instance
	 */
	private Worker createWorker(String clazz, ClassLoader loader) throws JyroException {
		try {
			return (Worker)Class.forName(clazz, true, loader).newInstance();
		} catch(ClassCastException ex){
			throw new JyroException(clazz + " is not subclass of " + Worker.class.getName(), ex);
		} catch(ClassNotFoundException ex){
			throw new JyroException("specified class " + clazz + " not found in this context", ex);
		} catch(IllegalAccessException ex){
			throw new JyroException("cannot access to default constructor of " + clazz, ex);
		} catch(InstantiationException ex){
			throw new JyroException("fail to instantiate for " + clazz, ex);
		}
	}

	// ======================================================================
	// Create Worker
	// ======================================================================
	/**
	 * Create worker for specified Java class.
	 *
	 * @param clazz class name of worker
	 * @param loader class loader to create worker
	 * @return worker instance
	 * @throws JyroException if fail to create worker instance
	 */
	private Worker createScript(ClassLoader loader, Element elem) throws JyroException {
		String type = f(elem.getAttribute("type"));
		String includes = f(elem.getAttribute("includes"));
		String charset = f(elem.getAttribute("charset"));

		// parse include files
		List<File> files = new ArrayList<File>();
		StringTokenizer tk = new StringTokenizer(includes, File.pathSeparator);
		while(tk.hasMoreTokens()){
			String path = tk.nextToken();
			try {
				for(File f: IO.fileSet(dir, path)){
					files.add(f);
				}
			} catch(IOException ex){
				logger.warn("invalid include path: " + path + "; " + ex);
			}
		}

		File[] fs = files.toArray(new File[files.size()]);
		String[] cs = new String[fs.length];
		Arrays.fill(cs, charset);
		return new ScriptWorker(loader, type, fs, cs);
	}

	// ======================================================================
	// Format String
	// ======================================================================
	/**
	 * Format specified string value with variable.
	 *
	 * @param value string to format
	 * @return formatted string
	 */
	private String f(String value){
		return Text.format(value, param);
	}

	// ======================================================================
	// Format String
	// ======================================================================
	/**
	 * Format specified string value with variable.
	 *
	 * @param value string to format
	 * @return formatted string
	 */
	private int toInt(String name, String value) throws JyroException{
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException ex){
			throw new JyroException(name + " must be integer: " + value);
		}
	}

	// ======================================================================
	// Retrieve Element
	// ======================================================================
	/**
	 * Retrieve element for specified xpath expression.
	 *
	 * @param expr xpath expression
	 * @param elem base node
	 * @return element
	 */
	private Element elem(String expr, Object elem){
		Iterator<Element> it = elemset(expr, elem).iterator();
		if(it.hasNext()){
			return it.next();
		}
		return null;
	}

	// ======================================================================
	// Retrieve Nodeset
	// ======================================================================
	/**
	 * Retrieve nodeset for specified element.
	 *
	 * @param expr xpath expression
	 * @param elem base node
	 * @return iterable of elements
	 */
	private Iterable<Element> elemset(String expr, Object elem){
		List<Element> list = new ArrayList<Element>();
		try {
			NodeList nl = (NodeList)xpath.evaluate(expr, elem, XPathConstants.NODESET);
			for(int i=0; i<nl.getLength(); i++){
				list.add((Element)nl.item(i));
			}
		} catch(XPathException ex){
			throw new IllegalStateException("invalid xpath expression (this maybe bug): " + expr, ex);
		}
		return list;
	}

	// ======================================================================
	// Create Libext ClassLoader
	// ======================================================================
	/**
	 * Create ClassLoader for specified libext directory.
	 *
	 * @param classpath classpath that separated pathseparator.
	 * @param libext libext directories
	 * @param parent parent class loader
	 * @return class loader
	 */
	private static ClassLoader getLibextLoader(String classpath, String libext, ClassLoader parent) {
		// TODO Curretly, supports local filesystem only not as URL
		List<File> libs = new ArrayList<File>();

		// retrieve library or directory from classpath
		StringTokenizer tk = new StringTokenizer(classpath, File.pathSeparator);
		while(tk.hasMoreTokens()){
			File file = new File(tk.nextToken());
			if(! file.exists()){
				logger.warn("path not found: " + file.getAbsolutePath());
			} else {
				libs.add(file);
				logger.debug("use library: " + file.getAbsolutePath());
			}
		}

		// retrieve extra library files
		tk = new StringTokenizer(libext, File.pathSeparator);
		while(tk.hasMoreTokens()){

			// retrieve library files in directory
			File dir = new File(tk.nextToken());
			File[] files = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					name = name.toLowerCase();
					return name.endsWith(".jar") || name.endsWith(".zip");
				}
			});

			// return default class loader if library not found
			if(files == null || files.length == 0){
				logger.debug("extra library not found: " + dir);
				continue;
			}

			// add files
			for(File f: files){
				libs.add(f);
				logger.debug("use library: " + f.getAbsolutePath());
			}
		}

		// create url array to library files
		List<URL> urls = new ArrayList<URL>();
		for(int i=0; i<libs.size(); i++){
			try {
				urls.add(libs.get(i).toURI().toURL());
			} catch(MalformedURLException ex){
				logger.warn(ex.toString());
			}
		}

		// create class loader
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
	}

}
