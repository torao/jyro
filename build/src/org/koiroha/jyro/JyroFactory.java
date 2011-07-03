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

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.xpath.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.util.Text;
import org.koiroha.xml.DefaultNamespaceContext;
import org.w3c.dom.*;

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
public class JyroFactory {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JyroFactory.class);

	// ======================================================================
	// XML Namespace
	// ======================================================================
	/**
	 * XML Namespace of Jyro configuration xml.
	 */
	public static final String XMLNS10 = "http://www.koiroha.org/xmlns/jyro/configuration_1.0";

	// ======================================================================
	// Default Library Directory
	// ======================================================================
	/**
	 * Library directory name to load as default. ${jyro.home}/{@value}
	 */
	public static final String DIR_LIB = "lib";

	// ======================================================================
	// Temporary Directory
	// ======================================================================
	/**
	 * Temporary directory to place some work files. ${jyro.home}/{@value}
	 */
	public static final String DIR_TMP = "tmp";

	// ======================================================================
	// Lock Filename
	// ======================================================================
	/**
	 * Lock filename that will be placed in temporary directory.
	 */
	public static final String FILE_LOCK = ".lock";

	// ======================================================================
	// Root Node
	// ======================================================================
	/**
	 * Root element of xml configuration.
	 */
	private final Element root;

	// ======================================================================
	// XPath
	// ======================================================================
	/**
	 * XPath to parse xml.
	 */
	private final XPath xpath;

	// ======================================================================
	// Properties Map
	// ======================================================================
	/**
	 * Properties map defined in configuration.
	 */
	private final Map<String,String> properties = new HashMap<String,String>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * The constructor is hidden in this class.
	 *
	 * @param root root element of xml configuration
	 * @param prop default properties
	 */
	private JyroFactory(Element root, Properties prop) {
		DefaultNamespaceContext nc = new DefaultNamespaceContext();
		nc.setNamespaceURI("j", XMLNS10);
		this.root = root;
		XPathFactory factory = XPathFactory.newInstance();
		this.xpath = factory.newXPath();
		this.xpath.setNamespaceContext(nc);

		// retrieve all system properties
		for(Map.Entry<Object,Object> e: System.getProperties().entrySet()){
			properties.put(e.getKey().toString(), e.getValue().toString());
		}

		// retrieve all specified properties
		for(Map.Entry<Object,Object> e: prop.entrySet()){
			properties.put(e.getKey().toString(), e.getValue().toString());
		}
		return;
	}

	// ======================================================================
	// Parse Configuration
	// ======================================================================
	/**
	 * Parse xml configuration specified in constructor.
	 *
	 * @return Jyro instance
	 * @throws XPathException invalid xpath (bug?)
	 */
	private Jyro parse() throws XPathException {

		// parse all properties in configuration
		NodeList nl = (NodeList)xpath.evaluate("j:jyro/j:property", root, XPathConstants.NODESET);
		for(int i=0; i<nl.getLength(); i++){
			Element elem = (Element)nl.item(i);
			String name = elem.getAttribute("name");
			String value = f(elem.getAttribute("value"));
			properties.put(name, value);
		}

		// build all nodes
		nl = (NodeList)xpath.evaluate("j:jyro/j:node", root, XPathConstants.NODESET);
		for(int i=0; i<nl.getLength(); i++){
			Element elem = (Element)nl.item(i);
			Node node = buildNode(elem);
		}
		return;
	}

	// ======================================================================
	// Parse Configuration
	// ======================================================================
	/**
	 * Parse xml configuration specified in constructor.
	 *
	 * @param elem node element
	 * @param parent default class loader of this node
	 * @return Jyro instance
	 * @throws JyroException fail to build node
	 * @throws XPathException invalid xpath (bug?)
	 */
	private Node buildNode(Element elem, ClassLoader parent) throws JyroException, XPathException {

		// retrieve task name
		String task = f(elem.getAttribute("task"));

		// retrieve node-scope class loader
		String classpath = elem.getAttribute("classpath");
		String extdirs = elem.getAttribute("extdirs");
		ClassLoader loader = getLibextLoader(classpath, extdirs, parent);

		// build worker
		Worker worker = null;
		Element wk = (Element)xpath.evaluate("j:worker", elem, XPathConstants.NODE);
		if(wk.hasAttribute("class")){
			worker = createWorker(wk.getAttribute("class"), loader);
		}

		// create node implementation
		NodeImpl node = new NodeImpl(task, loader, worker);

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
	// Format String
	// ======================================================================
	/**
	 * Format specified string value with variable.
	 *
	 * @param value string to format
	 * @return formatted string
	 */
	private String f(String value){
		return Text.format(value, properties);
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
	// Create Instance
	// ======================================================================
	/**
	 * Create Jyro instance from specified jyro.home directory.
	 *
	 * @param dir jyro.home directory
	 * @param parent default class loader
	 * @return Jyro instance
	 * @throws JyroException if fail to configure
	 */
	public static Jyro createInstance(File dir, ClassLoader parent) throws JyroException {

		// set default class loader if not specified
		if(parent == null){
			parent = Thread.currentThread().getContextClassLoader();
			if(parent == null){
				parent = ClassLoader.getSystemClassLoader();
			}
		}

		// get class loader for extra libraries
		String classpath = "";
		String libext = new File(dir, DIR_LIB).toString();
		ClassLoader loader = getLibextLoader(classpath, libext, parent);

		try {
			JyroFactory factory = new JyroFactory(root, prop);
			return factory.parse();
		} catch(XPathException ex){
			logger.error("unexpected exception, this maybe bug; " + ex);
			throw new IllegalStateException(ex);
		}
		return null;

		// create Jyro instance
		return new Jyro(dir, loader);
	}

	// ======================================================================
	// Create Instance
	// ======================================================================
	/**
	 * Create Jyro instance from specified DOM element with properties.
	 *
	 * @param config xml element of configuration
	 * @param prop default properties
	 * @return Jyro instance
	 * @throws JyroException if fail to build instance
	 */
	public static Jyro createInstance(Element config, Properties prop) throws JyroException {
		try {

		} catch(XPathException ex){
			logger.error("unexpected exception, this maybe bug; " + ex);
			throw new IllegalStateException(ex);
		}
		return null;
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
