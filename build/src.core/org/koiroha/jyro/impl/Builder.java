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

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.util.*;
import org.koiroha.jyro.util.Text;
import org.w3c.dom.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Builder:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/20 Java SE 6
 */
class Builder extends XmlBeanAdapter {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Builder.class);

	// ======================================================================
	// Base Directory
	// ======================================================================
	/**
	 * Base directory to resolve relative path.
	 */
	private final File base;

	// ======================================================================
	// Class Loader
	// ======================================================================
	/**
	 * Default class Loader of this builder.
	 */
	private final ClassLoader loader;

	// ======================================================================
	// Property
	// ======================================================================
	/**
	 * Placeholder map in configuration such as ${foo.bar}.
	 */
	private final Properties param;

	// ======================================================================
	// Dependency
	// ======================================================================
	/**
	 * File dependency to reload automatically.
	 */
	private final Dependency dependency = new Dependency();

	// ==================================================================
	// Constructor
	// ==================================================================
	/**
	 * @param doc xml document of jyro.xml
	 */
	public Builder(Document doc, File base, ClassLoader loader, Properties param) throws JyroException {
		super(doc);

		setNamespaceURI("j", Cluster.XMLNS10);

		this.param = getProperties(param);
		this.loader = loader;
		this.base = base;

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
	private Properties getProperties(Properties init){
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
	// Retrieve Bus
	// ==================================================================
	/**
	 * Retrieve bus to transmit jobs.
	 *
	 * @return bus
	 * @throws JyroException
	 */
	public Bus getBus() throws JyroException {

		// refer queue element
		Element elem = elem("j:jyro/j:bus");
		if(elem == null){
			return new LocalBus();
		}

		// create queue factory instance
		String factoryName = elem.getAttribute("factory");
		Bus factory = null;
		if(factoryName.equals("jvm")){
			factory = new LocalBus();
		} else if(factoryName.equals("jms")){
			factory = new JMSBus();
			// TODO initialize JMS settings
		} else {
			factory = (Bus)Beans.newInstance(loader, factoryName);
		}

		// set factory properties
		for(Element prop: elemset("j:jyro/j:bus/j:property")){
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
	public Iterable<Node> getNodes(Bus bus) throws JyroException {
		List<Node> list = new ArrayList<Node>();
		for(Element elem: elemset("j:jyro/j:node")){
			Node node = buildNode(elem, bus);
			list.add(node);
		}
		return list;
	}

	// ======================================================================
	// Refer Library Files
	// ======================================================================
	/**
	 * Refer library files (*.jar, *.zip) in specified directory.
	 *
	 * @param libext libext directory
	 * @return library files
	 */
	public static File[] getLibraries(File libext){
		File[] files = libext.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				return name.endsWith(".jar") || name.endsWith(".zip");
			}
		});
		if(files == null){
			return new File[0];
		}
		return files;
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
	 * @param dependency or null
	 * @return class loader
	 */
	public static ClassLoader getLibextLoader(File[] files, ClassLoader parent) {
		URL[] urls = new URL[files.length];
		for(int i=0; i<files.length; i++){
			try {
				urls[i] = files[i].toURI().toURL();
			} catch(MalformedURLException ex){
				logger.warn(ex.toString());
			}
		}
		return getLibextLoader(urls, parent);
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
	 * @param dependency or null
	 * @return class loader
	 */
	public static ClassLoader getLibextLoader(URL[] urls, ClassLoader parent) {

		// create class loader
		return new URLClassLoader(urls, parent){
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
	private Node buildNode(Element elem, Bus bus) throws JyroException {

		// retrieve task name
		String id = f(elem.getAttribute("id"));

		// retrieve node-scope class loader
		ClassLoader loader = buildNodeClassLoader(elem);

		// retrieve thread pool
		ThreadPool threads = buildThreadPool(elem, id, loader);

		// retrieve filters
		List<WorkerFilter> filters = new ArrayList<WorkerFilter>();
		for(Element f: elemset(elem, "j:filter")){
			WorkerFilter filter = buildFilter(f,loader);
			filter.init();
			filters.add(filter);
		}

		// retrieve worker element
		Worker worker = buildWorker(elem, id);
		worker.setContext(new Context(bus));
		worker.init();

		// create node implementation
		Node node = new Node(id, loader, bus, threads, filters, worker);
		return node;
	}

	// ==================================================================
	// Get Node ClassLoader
	// ==================================================================
	/**
	 * Retrieve node class loader for specified element.
	 *
	 * @param elem node element
	 * @return class loader
	 */
	private ClassLoader buildNodeClassLoader(Element elem) {
		List<File> libs = new ArrayList<File>();

		// extdirs attribute
		String extdirs = f(elem.getAttribute("extdirs"));
		for(String extdir: extdirs.split(File.pathSeparator)){
			libs.addAll(Arrays.asList(getLibraries(new File(base, extdir))));
		}
		String classpath = f(elem.getAttribute("classpath"));
		for(String cp: classpath.split(File.pathSeparator)){
			libs.add(new File(base, cp));
		}
		return getLibextLoader(libs.toArray(new File[libs.size()]), loader);
	}

	// ==================================================================
	// Build Worker
	// ==================================================================
	/**
	 * Build worker instance from specified element.
	 *
	 * @param elem node element
	 * @param id node id
	 * @return worker instance
	 * @throws JyroException fail to build worker
	 */
	private Worker buildWorker(Element elem, String id) throws JyroException {

		// build worker implemented as Java class
		Element worker = elem(elem, "j:worker");
		if(worker != null){
			return create(f(worker.getAttribute("class")), loader, Worker.class);
		}

		// build worker of script
		worker = elem(elem, "j:script");
		if(worker != null){
			return createScript(loader, worker);
		}

		throw new JyroException("no worker found in node definition: " + id);
	}

	// ==================================================================
	// Build Filter
	// ==================================================================
	/**
	 * Build filter instance from specified element.
	 *
	 * @param elem filter element
	 * @return filter instance
	 * @throws JyroException fail to build filter
	 */
	private WorkerFilter buildFilter(Element elem, ClassLoader loader) throws JyroException {
		String className = elem.getAttribute("class");
		WorkerFilter filter = (WorkerFilter)Beans.newInstance(loader, className);
		for(Element prop: elemset(elem, "j:property")){
			Beans.setProperty(filter, prop.getAttribute("name"), prop.getAttribute("value"));
		}
		return filter;
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
	private ThreadPool buildThreadPool(Element elem, String id, ClassLoader loader) throws JyroException {
		ThreadPool threadPool = new ThreadPool(id, loader);

		Element thread = elem(elem, "j:thread");
		if(thread == null){
			return threadPool;
		}

		// set minimum thread-pool size
		if(thread.hasAttribute("min")){
			threadPool.setMinimumWorkers(n(thread, "min"));
		}

		// set maximum thread-pool size
		if(thread.hasAttribute("max")){
			threadPool.setMaximumWorkers(n(thread, "max"));
		}

		// set priority for worker threads
		if(thread.hasAttribute("priority")){
			threadPool.setPriority(n(thread, "priority"));
		}

		// set daemon flag for worker threads
		if(thread.hasAttribute("daemon")){
			threadPool.setDaemon(bool(thread, "@daemon"));
		}

		return threadPool;
	}

	// ==================================================================
	// Create Object
	// ==================================================================
	/**
	 * Create some for specified Java class.
	 *
	 * @param className class name of worker
	 * @param loader class loader to create worker
	 * @return worker instance
	 * @throws JyroException if fail to create worker instance
	 */
	private <T> T create(String className, ClassLoader loader, Class<T> type) throws JyroException {
		try {
			Class<?> clazz = Class.forName(className, true, loader);
			if(clazz.isAssignableFrom(type)){
				throw new JyroException(className + " is not subclass of " + type.getName());
			}
			Object obj = clazz.newInstance();
			return type.cast(obj);
		} catch(ClassNotFoundException ex){
			throw new JyroException("specified class " + className + " not found in this context: " + loader, ex);
		} catch(IllegalAccessException ex){
			throw new JyroException("cannot access to default constructor of " + className, ex);
		} catch(InstantiationException ex){
			throw new JyroException("fail to instantiate for " + className, ex);
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
				for(File f: IO.fileSet(base, path)){
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Context: Worker Context
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Worker context class.
	 */
	private static class Context implements WorkerContext {

		// ==================================================================
		// Bus
		// ==================================================================
		/**
		 * Bus of this context.
		*/
		private final Bus bus;

		// ==================================================================
		//
		// ==================================================================
		/**
		 * Retrieve worker interface to call.
		 *
		 * @param worker worker interface
		 * @return callable worker instance
		 * @throws JyroException if fail to refer worker interface
		*/
		public Context(Bus bus) {
			this.bus = bus;
			return;
		}

		// ==================================================================
		// Retrieve Worker Interface
		// ==================================================================
		/**
		 * Retrieve worker interface to call.
		 *
		 * @param worker worker interface
		 * @return callable worker instance
		 * @throws JyroException if fail to refer worker interface
		*/
		@Override
		public void call(String func, Object... args) throws JyroException{
			Job job = new Job(func, args, null);
			bus.send(job);
			return;
		}

	}

}
