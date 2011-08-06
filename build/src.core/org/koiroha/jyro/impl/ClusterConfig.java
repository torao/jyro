/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.impl;

import static org.koiroha.jyro.impl.ClusterImpl.*;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.util.*;
import org.koiroha.jyro.util.Text;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// ClusterConfig: Configuration for JyroCore
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Configuration class to build {@link ClusterImpl} instance.
 *
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
final class ClusterConfig implements WorkerContext {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(ClusterConfig.class);

	// ======================================================================
	// Generic Use Timer
	// ======================================================================
	/**
	 * The timer to detect modification and reload for cores.
	 */
	public static final Timer TIMER = new Timer("JyroTimer", true);

	// ======================================================================
	// Home Directory
	// ======================================================================
	/**
	 * Home directory to JyroCore instance.
	 */
	private final File dir;

	// ======================================================================
	// Property
	// ======================================================================
	/**
	 * Placeholder map in configuration such as ${foo.bar}.
	 */
	private final Properties param;

	// ======================================================================
	// Job Queue Factory
	// ======================================================================
	/**
	 * Job queue factory.
	 */
	private final JobQueueFactory factory;

	// ======================================================================
	// Nodes
	// ======================================================================
	/**
	 * Node map.
	 */
	private final Map<String,NodeImpl> nodes = new HashMap<String,NodeImpl>();

	// ======================================================================
	// ClassLoader
	// ======================================================================
	/**
	 * Individual class loader for core.
	 */
	private final ClassLoader loader;

	// ======================================================================
	// jyro.xml
	// ======================================================================
	/**
	 * The core configuration file jyro.xml.
	 */
	private final JyroConfig jyroConfig;

	// ======================================================================
	// Dependency
	// ======================================================================
	/**
	 * File dependency to reload automatically.
	 */
	private final Dependency dependency = new Dependency();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Retrieve jyro instance scope classloader. This method returns all
	 * *.jar and *.zip files in ${jyro.home}/${jyro.core}/lib.
	 *
	 * @param dir home directory of jyro
	 * @param parent parent class loader
	 * @param init init properties
	 * @throws JyroException if fail to load configuration
	 */
	public ClusterConfig(File dir, ClassLoader parent, Properties init) throws JyroException {
		this.dir = dir;

		// determine default class loader if specified value is null
		if(parent == null){
			parent = java.lang.Thread.currentThread().getContextClassLoader();
			if(parent == null){
				parent = ClassLoader.getSystemClassLoader();
			}
		}

		// get class loader for extra libraries
		String dummy = "";
		String libext = new File(dir, DIR_LIB).toString();
		this.loader = getLibextLoader(dummy, libext, parent);

		// read jyro configuration xml
		Document doc = load(new File(dir, FILE_CONF));
		this.jyroConfig = new JyroConfig(doc);

		// empty property use if initprop is null
		if(init == null){
			init = new Properties();
		}
		this.param = jyroConfig.getProperties(init);

		this.factory = jyroConfig.getQueueFactory();

		// build nodes on this core
		for(NodeImpl node: jyroConfig.getNodes()){
			nodes.put(node.getId(), node);
		}
		return;
	}

	// ======================================================================
	// Retrieve Home Directory
	// ======================================================================
	/**
	 * Retrieve home directory of this jyro instance.
	 *
	 * @return home directory
	 */
	public File getDirectory() {
		return dir;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @param id ID of queue
	 * @return home directory
	 */
	public JobQueue getQueue(String id) throws JyroException{
		return factory.lookup(id);
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @return nodes
	 */
	public Iterable<NodeImpl> getNodes() {
		return new ArrayList<NodeImpl>(nodes.values());
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @param id ID of queue
	 * @return home directory
	 */
	public NodeImpl getNode(String id) {
		return nodes.get(id);
	}

	// ======================================================================
	// Retrieve ClassLoader
	// ======================================================================
	/**
	 * Retrieve class loader for core.
	 *
	 * @return class loader
	 */
	public ClassLoader getLoader() {
		return loader;
	}

	// ======================================================================
	// Retrieve Modified
	// ======================================================================
	/**
	 * Retrieve that whether core-dependent files are modified or not.
	 *
	 * @return true if one or more dependency files are modified
	 */
	public boolean isModified(){
		return dependency.isModified();
	}

	// ======================================================================
	// Startup Services
	// ======================================================================
	/**
	 * Start all services in this instance.
	 *
	 * @throws JyroException if fail to startup jyro
	 */
	public void startup() throws JyroException {
		logger.debug("startup()");

		// start all nodes
		for(NodeImpl n: nodes.values()){
			n.start();
		}
		return;
	}

	// ======================================================================
	// Shutdown Services
	// ======================================================================
	/**
	 * Shutdown all services in this instance.
	 *
	 * @throws JyroException if fail to shutdown jyro
	 */
	public void shutdown() throws JyroException {
		logger.debug("shutdown()");

		// stop all nodes
		for(NodeImpl n: nodes.values()){
			n.stop();
		}

		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @param nodeId
	 * @param job
	 * @throws JyroException
	 */
	@Override
	public void send(String nodeId, Job job) throws JyroException {
		JobQueue queue = getQueue(nodeId);
		if(queue == null){
			throw new JyroException("no such worker node: " + nodeId);
		}
		queue.post(job);
		return;
	}

	// ======================================================================
	// Load XML Document
	// ======================================================================
	/**
	 * Load specified XML document file.
	 *
	 * @param file xml document
	 * @return document
	 * @throws JyroException if fail to read xml document
	 */
	private Document load(File file) throws JyroException {
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

		// add xml file to dependency
		dependency.add(file);

		return doc;
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
	// Parse Number
	// ======================================================================
	/**
	 * Parse int value from specified attribute.
	 *
	 * @param elem element that has attribute
	 * @param attr attribute name
	 * @return attribute value as int
	 * @throws JyroException if attribute is not a valid number
	 */
	private static int n(Element elem, String attr) throws JyroException{
		String value = elem.getAttribute(attr);
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException ex){
			throw new JyroException("invalid number: @" + attr + "=\"" + value + "\"");
		}
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
	private ClassLoader getLibextLoader(String classpath, String libext, ClassLoader parent) {
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

		// add classpath/extdirs library to dependency
		dependency.add(libs);

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
		return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent){
			@Override
			public String toString(){
				StringBuilder buffer = new StringBuilder();
				buffer.append('[');
				for(URL url: getURLs()){
					buffer.append(url.toString());
				}
				if(getParent() != null){
					buffer.append(getParent());
				}
				buffer.append(']');
				return buffer.toString();
			}
		};
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// JyroConfig: jyro.xml
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * XML configuration for {@code jyro.xml}.
	 */
	private class JyroConfig extends XmlBeanAdapter {

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * @param doc xml document of jyro.xml
		 */
		public JyroConfig(Document doc) {
			super(doc);
			setNamespaceURI("j", ClusterImpl.XMLNS10);
			return;
		}

		// ==================================================================
		// Retrieve Properties
		// ==================================================================
		/**
		 * Retrieve core-specified properties.
		 *
		 * @param init initial properties
		 * @return core-specified properties
		 */
		public Properties getProperties(Properties init){
			Properties prop = new Properties(init);
			for(Element elem: elemset("j:jyro/j:property")){

				// retreive property name and value
				String name = elem.getAttribute("name");
				String value = Text.format(elem.getAttribute("value"), prop);
				Object old = prop.setProperty(name, value);

				// warn when property was overwrote
				if(old != null){
					logger.warn("duplicate property definition for: " + Text.literize(name) + ", overwrite value " + Text.literize(old.toString()) + " to " + Text.literize(value));
				} else {
					logger.debug("property: " + Text.literize(name) + "=" + Text.literize(value));
				}
			}
			return prop;
		}

		// ==================================================================
		// Retrieve Job Queue Factory
		// ==================================================================
		/**
		 * Retrieve job queue factory.
		 *
		 * @return job queue factory
		 * @throws JyroException
		 */
		public JobQueueFactory getQueueFactory() throws JyroException {
			Element elem = elem("j:jyro/j:queue");
			if(elem == null){
				return new JVMJobQueueFactory();
			}
			String factoryName = elem.getAttribute("factory");
			JobQueueFactory factory = null;
			if(factoryName.equals("jvm")){
				factory = new JVMJobQueueFactory();
				// TODO JMSJobQueueFactory
			} else {
				factory = (JobQueueFactory)Beans.newInstance(loader, factoryName);
			}
			for(Element prop: elemset("j:jyro/j:queue/j:property")){
				String name = prop.getAttribute("name");
				String value = f(prop.getAttribute("value"));
				Beans.setProperty(factory, name, value);
			}
			return factory;
		}

		// ==================================================================
		// Retrieve Queues
		// ==================================================================
		/**
		 * Retrieve queues defined in this configuration.
		 *
		 * @return list of queues
		 * @throws JyroException if fail to build node
		 */
		public Iterable<NodeImpl> getNodes() throws JyroException {
			List<NodeImpl> list = new ArrayList<NodeImpl>();
			for(Element elem: elemset("j:jyro/j:node")){
				NodeImpl node = buildNode(elem);
				list.add(node);
			}
			return list;
		}

		// ==================================================================
		// Build Node
		// ==================================================================
		/**
		 * Build node instance from specified element.
		 *
		 * @param elem node element
		 * @return Jyro instance
		 * @throws JyroException fail to build node
		 */
		private NodeImpl buildNode(Element elem) throws JyroException {

			// retrieve task name
			String id = f(elem.getAttribute("id"));

			// retrieve node-scope class loader
			String classpath = f(elem.getAttribute("classpath"));
			String extdirs = f(elem.getAttribute("extdirs"));
			ClassLoader loader = getLibextLoader(classpath, extdirs, getLoader());

			// retrieve worker element
			Worker worker = null;
			Element wk = elem(elem, "j:worker");
			if(wk != null){
				worker = createWorker(f(wk.getAttribute("class")), loader);
			} else {
				wk = elem(elem, "j:script");
				if(wk != null){
					worker = createScript(loader, wk);
				} else {
					throw new JyroException("no worker found in node definition: " + id);
				}
			}
			worker.init(ClusterConfig.this);

			// create node implementation
			NodeImpl node = new NodeImpl(id, loader, factory.create(id), worker);

			// set minimum thread-pool size
			if(wk.hasAttribute("min")){
				node.setMinimumWorkers(n(wk, "min"));
			}

			// set maximum thread-pool size
			if(wk.hasAttribute("max")){
				node.setMaximumWorkers(n(wk, "max"));
			}

			// set priority for worker threads
			if(wk.hasAttribute("priority")){
				node.setPriority(n(wk, "priority"));
			}

			// set daemon flag for worker threads
			if(wk.hasAttribute("daemon")){
				node.setDaemon(bool(wk, "@daemon"));
			}
			return node;
		}

		// ==================================================================
		// Create Worker
		// ==================================================================
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
				throw new JyroException("specified class " + clazz + " not found in this context: " + loader, ex);
			} catch(IllegalAccessException ex){
				throw new JyroException("cannot access to default constructor of " + clazz, ex);
			} catch(InstantiationException ex){
				throw new JyroException("fail to instantiate for " + clazz, ex);
			}
		}

		// ==================================================================
		// Create Script Worker
		// ==================================================================
		/**
		 * Create script worker for specified element.
		 *
		 * @param loader class loader to create worker
		 * @param elem script element
		 * @return worker instance
		 * @throws JyroException if fail to create worker instance
		 */
		private Worker createScript(ClassLoader loader, Element elem) throws JyroException {
			String type = f(elem.getAttribute("type"));
			String includes = f(elem.getAttribute("includes"));
			String charset = f(elem.getAttribute("charset"));
			if(charset.length() == 0){
				charset = null;
			}

			// parse include files
			List<File> files = new ArrayList<File>();
			StringTokenizer tk = new StringTokenizer(includes, File.pathSeparator);
			while(tk.hasMoreTokens()){
				String path = tk.nextToken();
				try {
					for(File f: IO.fileSet(dir, path)){
						files.add(f);
						dependency.add(f);
					}
				} catch(IOException ex){
					logger.warn("invalid include path: " + path + "; " + ex);
				}
			}

			// add included script files to dependency
			dependency.add(files);

			File[] fs = files.toArray(new File[files.size()]);
			String[] cs = new String[fs.length];
			Arrays.fill(cs, charset);
			return new ScriptWorker(loader, type, fs, cs, elem.getTextContent());
		}

	}

}
