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

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroCore: Node Container
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Parallel processing container class.
 *
 * @author takami torao
 */
public class JyroCore {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JyroCore.class);

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
	 * Library directory name to load as default. ${jyro.home}/${jyro.name}/{@value}
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
	// Configuration File Name
	// ======================================================================
	/**
	 * Configuration file name of jyro instance. ${jyro.home}/${jyro.name}/conf/{@value}
	 */
	public static final String FILE_CONF = "jyro.xml";

	// ======================================================================
	// Lock Filename
	// ======================================================================
	/**
	 * Lock filename that will be placed in temporary directory.
	 */
	public static final String FILE_LOCK = ".lock";

	// ======================================================================
	// Core Name
	// ======================================================================
	/**
	 * The name of this core.
	 */
	private final String name;

	// ======================================================================
	// Configuration
	// ======================================================================
	/**
	 * Configuration of this core.
	 */
	private final Config config;

	// ======================================================================
	// Queues
	// ======================================================================
	/**
	 * Queue in this context.
	 */
	private final Map<String,JobQueue> queues = new HashMap<String,JobQueue>();

	// ======================================================================
	// Nodes
	// ======================================================================
	/**
	 * Nodes in this context.
	 */
	private final Map<String,List<Node>> nodes;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param dir home directory of this instance
	 * @param parent parent class loader
	 * @param prop init property replace with placeholder such as ${foo.bar}
	 * @throws JyroException if cannot configure instance
	 */
	public JyroCore(String name, File dir, ClassLoader parent, Properties prop) throws JyroException{
		logger.debug("initializing JyroCore: " + name);

		// set core-depend context parameters
		prop = new Properties(prop);
		prop.setProperty("jyro.core", name);

		// set instance properties
		this.name = name;
		this.config = new Config(dir, parent, prop);
		this.nodes = config.createNodes(prop);
		return;
	}

	// ======================================================================
	// Retrieve Core Name
	// ======================================================================
	/**
	 * Retrieve the name of this core.
	 *
	 * @return core name
	 */
	public String getName() {
		return name;
	}

	// ======================================================================
	// Retrieve Queue
	// ======================================================================
	/**
	 * Retrieve specified queue of this core.
	 *
	 * @param queue name
	 * @return job queue
	 */
	public JobQueue getQueue(String name){
		return queues.get(name);
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
		return config.getDirectory();
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
		for(List<Node> l: nodes.values()){
			for(Node n: l){
				n.start();
			}
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
		for(List<Node> l: nodes.values()){
			for(Node n: l){
				n.stop();
			}
		}

		return;
	}

}
