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
import java.lang.management.ManagementFactory;
import java.util.Properties;

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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Status: Core Status
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * The enumeration value that specify core status.
	 */
	public enum Status {
		/** */
		BOOTING,
		/** */
		RUNNING,
		/** */
		SHUTTINGDOWN,
		/** */
		STOPED,
	}

	// ======================================================================
	// Core Name
	// ======================================================================
	/**
	 * The name of this core.
	 */
	private final String name;

	// ======================================================================
	// Core Status
	// ======================================================================
	/**
	 * The status of this core.
	 */
	private Status status = Status.STOPED;

	// ======================================================================
	// Configuration
	// ======================================================================
	/**
	 * Configuration of this core.
	 */
	private final Config config;

	// ======================================================================
	// Start Time
	// ======================================================================
	/**
	 * The JavaVM uptime in startup this instance.
	 */
	private long startTime = -1;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param name core name
	 * @param dir home directory of this instance
	 * @param parent parent class loader
	 * @param prop init property replace with placeholder such as ${foo.bar}
	 * @throws JyroException if cannot configure instance
	 */
	public JyroCore(String name, File dir, ClassLoader parent, Properties prop) throws JyroException{
		logger.debug("initializing JyroCore: " + name);

		// set core-depend context parameters
		prop = new Properties(prop);
		prop.setProperty("core.name", name);

		// set instance properties
		this.name = name;
		this.config = new Config(dir, parent, prop);
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
	// Retrieve Core Status
	// ======================================================================
	/**
	 * Retrieve the status of this core.
	 *
	 * @return core status
	 */
	public Status getStatus() {
		return status;
	}

	// ======================================================================
	// Retrieve Nodes
	// ======================================================================
	/**
	 * Retrieve nodes on this core.
	 *
	 * @return iterable nodes
	 */
	public Iterable<Node> getNodes(){
		return config.getNodes();
	}

	// ======================================================================
	// Retrieve Node
	// ======================================================================
	/**
	 * Retrieve node of specified id.
	 *
	 * @param id ID of node
	 * @return node
	 */
	public Node getNode(String id){
		return config.getNode(id);
	}

	// ======================================================================
	// Retrieve Queue
	// ======================================================================
	/**
	 * Retrieve specified queue of this core.
	 *
	 * @param id name
	 * @return job queue
	 */
	public JobQueue getQueue(String id){
		return config.getQueue(id);
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
	// Retrieve Home Directory
	// ======================================================================
	/**
	 * Retrieve home directory of this jyro instance.
	 *
	 * @return home directory
	 */
	public long getUptime(){
		if(startTime < 0){
			return -1;
		}
		return ManagementFactory.getRuntimeMXBean().getUptime() - startTime;
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
		return config.isModified();
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
		this.status = Status.BOOTING;
		this.config.startup();
		this.startTime = ManagementFactory.getRuntimeMXBean().getUptime();
		this.status = Status.RUNNING;
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
		this.config.shutdown();
		this.startTime = -1;
		this.status = Status.STOPED;
		return;
	}

}
