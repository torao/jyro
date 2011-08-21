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

import static org.koiroha.jyro.impl.Cluster.*;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.JyroException;
import org.koiroha.jyro.util.Dependency;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Config: Configuration for Cluster
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Configuration class to build {@link Cluster} instance.
 *
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
final class Config {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Config.class);

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
	// Bus
	// ======================================================================
	/**
	 * Bus to transport jobs.
	 */
	private final Bus bus;

	// ======================================================================
	// Nodes
	// ======================================================================
	/**
	 * Node map.
	 */
	private final Map<String,Node> nodes = new HashMap<String,Node>();

	// ======================================================================
	// ClassLoader
	// ======================================================================
	/**
	 * Individual class loader for core.
	 */
	private final ClassLoader loader;

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
	public Config(File dir, ClassLoader parent, Properties init) throws JyroException {
		this.dir = dir;

		// determine default class loader if specified value is null
		if(parent == null){
			parent = java.lang.Thread.currentThread().getContextClassLoader();
			if(parent == null){
				parent = ClassLoader.getSystemClassLoader();
			}
		}

		// create class loader for cluster from lib/ directory
		File libext = new File(dir, DIR_LIB);
		this.loader = getLibextLoader(libext, parent);

		// empty property use if initprop is null
		if(init == null){
			init = new Properties();
		}

		// read jyro configuration xml
		Document doc = load(new File(dir, FILE_CONF));
		Builder builder = new Builder(doc, dir, this.loader, init);

		// refer bus on this cluster
		this.bus = builder.getBus();

		// refer nodes on this cluster
		for(Node node: builder.getNodes(bus)){
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
	 * @return nodes
	 */
	public Iterable<Node> getNodes() {
		return new ArrayList<Node>(nodes.values());
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @param id ID of queue
	 * @return home directory
	 */
	public Node getNode(String id) {
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
		for(Node n: nodes.values()){
			n.init();
		}

		// start all nodes
		for(Node n: nodes.values()){
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
		for(Node n: nodes.values()){
			n.stop();
		}

		// stop all nodes
		for(Node n: nodes.values()){
			n.destroy();
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
	public void send(Job job) throws JyroException {
		bus.send(job);
		return;
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
	private ClassLoader getLibextLoader(File libext, ClassLoader parent) {
		File[] libs = Builder.getLibraries(libext);

		// return default class loader if library not found
		if(libs.length == 0){
			logger.debug("extra library not found: " + dir);
		} else {
			logger.debug("use library: " + Arrays.toString(libs));
		}

		// add classpath/extdirs library to dependency
		dependency.add(Arrays.asList(libs));

		return Builder.getLibextLoader(libs, parent);
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

}
