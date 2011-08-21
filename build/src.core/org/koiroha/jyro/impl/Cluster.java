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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.koiroha.jyro.JyroException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Cluster: Node Container
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Parallel processing container.
 *
 * @author takami torao
 */
public class Cluster {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Cluster.class);

	// ======================================================================
	// XML Namespace
	// ======================================================================
	/**
	 * XML Namespace of cluster configuration xml.
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
	 * Configuration file name of cluster instance. ${jyro.home}/${jyro.name}/conf/{@value}
	 */
	public static final String FILE_CONF = "jyro.xml";

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Status: Cluster Status
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * The enumeration value that specify cluster status.
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
	// Cluster Name
	// ======================================================================
	/**
	 * The name of this cluster.
	 */
	private final String name;

	// ======================================================================
	// Cluster Status
	// ======================================================================
	/**
	 * The status of this cluster.
	 */
	private Status status = Status.STOPED;

	// ======================================================================
	// Configuration
	// ======================================================================
	/**
	 * Configuration of this cluster.
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
	public Cluster(String name, File dir, ClassLoader parent, Properties prop) throws JyroException{
		logger.debug("initializing cluster: " + name);

		// set core-depend context parameters
		prop = new Properties(prop);
		prop.setProperty("cluster.name", name);

		// set instance properties
		this.name = name;
		this.config = new Config(dir, parent, prop);
		return;
	}

	// ======================================================================
	// Retrieve Cluster Name
	// ======================================================================
	/**
	 * Retrieve the name of this cluster.
	 *
	 * @return cluster name
	 */
	public String getName() {
		return name;
	}

	// ======================================================================
	// Retrieve Cluster Status
	// ======================================================================
	/**
	 * Retrieve the status of this cluster.
	 *
	 * @return cluster status
	 */
	public Status getStatus() {
		return status;
	}

	// ======================================================================
	// Retrieve Nodes
	// ======================================================================
	/**
	 * Retrieve nodes on this cluster.
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
	// Retrieve Home Directory
	// ======================================================================
	/**
	 * Retrieve home directory of this cluster instance.
	 *
	 * @return home directory
	 */
	public File getDirectory() {
		return config.getDirectory();
	}

	// ======================================================================
	// Retrieve Uptime
	// ======================================================================
	/**
	 * Retrieve uptime in millis of this cluster instance.
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
	 * Retrieve that whether cluster-dependent files are modified or not.
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
	 * Start all nodes in this instance.
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

	// ======================================================================
	// Send Job
	// ======================================================================
	/**
	 * Send specified job on this cluster.
	 *
	 * @param job job to send
	 * @throws JyroException if fail to send job
	 */
	public void send(Job job) throws JyroException {
		config.send(job);
		return;
	}

}
