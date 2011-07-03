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
import java.util.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyro: Node Container
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Parallel processing container class.
 *
 * @author takami torao
 */
public class Jyro {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Jyro.class);

	// ======================================================================
	// Application Name
	// ======================================================================
	/**
	 * Human readable application name.
	 */
	public static final String NAME;

	// ======================================================================
	// Application ID
	// ======================================================================
	/**
	 * Application ID to be able to use file or directory name, part of uri
	 * and so on.
	 */
	public static final String ID;

	// ======================================================================
	// Version
	// ======================================================================
	/**
	 * The three numbers separated with period that specifies version of Jyro
	 * such as "1.0.9".
	 */
	public static final String VERSION;

	// ======================================================================
	// Build Number
	// ======================================================================
	/**
	 * Read build number from application bundle resource and return.
	 */
	public static final String BUILD;

	// ======================================================================
	// Variable Name
	// ======================================================================
	/**
	 * Common variable name for Jyro home directory.
	 */
	public static final String JYRO_HOME = "jyro.home";

	// ======================================================================
	// Static Initializer
	// ======================================================================
	/**
	 * Read and set version constants.
	 */
	static {
		ResourceBundle res = ResourceBundle.getBundle("org.koiroha.jyro.version");
		NAME = res.getString("name");
		ID = res.getString("id");
		VERSION = res.getString("version");
		BUILD = res.getString("build");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Const:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 */
	public interface Const {

		// ==================================================================
		// XML Namespace
		// ==================================================================
		/**
		 * XML Namespace of Jyro configuration xml.
		 */
		public static final String XMLNS10 = "http://www.koiroha.org/xmlns/jyro/configuration_1.0";

		// ==================================================================
		// Default Library Directory
		// ==================================================================
		/**
		 * Library directory name to load as default. ${jyro.home}/{@value}
		 */
		public static final String DIR_LIB = "lib";

		// ==================================================================
		// Temporary Directory
		// ==================================================================
		/**
		 * Temporary directory to place some work files. ${jyro.home}/{@value}
		 */
		public static final String DIR_TMP = "tmp";

		// ==================================================================
		// Configuration File Name
		// ==================================================================
		/**
		 * Configuration file name of jyro instance. ${jyro.home}/{@value}
		 */
		public static final String FILE_CONF = "jyro.xml";

		// ==================================================================
		// Lock Filename
		// ==================================================================
		/**
		 * Lock filename that will be placed in temporary directory.
		 */
		public static final String FILE_LOCK = ".lock";
	}

	// ======================================================================
	// Nodes
	// ======================================================================
	/**
	 * Nodes in this context.
	 */
	private final Map<String,List<Node>> nodes;

	// ======================================================================
	// Directory
	// ======================================================================
	/**
	 * Home directory of this jyro instance.
	 */
	private final File dir;

	// ======================================================================
	// Directory
	// ======================================================================
	/**
	 * Home directory of this instance.
	 */
	private final RandomAccessFile lock = null;

	// ======================================================================
	// Class Loader
	// ======================================================================
	/**
	 * Class loader for instance-scope classes.
	 */
	private final ClassLoader loader;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param dir home directory of this instance
	 * @param parent parent class loader
	 * @param prop init property replace with placeholder such as ${foo.bar}
	 */
	public Jyro(File dir, ClassLoader parent, Properties prop) throws JyroException{
		logger.debug("initializing Jyro on directory: " + dir);
		this.dir = dir;

		Configurator config = new Configurator(dir);
		this.loader = config.getJyroClassLoader(parent);
		this.nodes = config.createNodes(loader, prop);
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
	// Startup Services
	// ======================================================================
	/**
	 * Start all services in this instance.
	 *
	 * @throws JyroException if fail to startup jyro
	 */
	public void startup() throws JyroException {
		logger.debug("startup()");
/* if use lock
		// create temporary directory if not exits
		File tmp = getTemporaryDirectory();
		if(! tmp.isDirectory()){
			if(tmp.mkdirs()){
				logger.debug("create new temporary directory: " + tmp);
			} else {
				logger.warn("fail to create temporary directory: " + tmp);
			}
		}

		// acquire lock of home directory
		File lockFile = new File(tmp, Configurator.FILE_LOCK);
		try {
			lock = new RandomAccessFile(lockFile, "rw");
			FileLock fl = lock.getChannel().tryLock();
			if(fl == null){
				throw new JyroException("unable to acquire lock of home: " + lockFile);
			}
			logger.debug("${jyro.home} lock success: " + Configurator.DIR_TMP + "/" + Configurator.FILE_LOCK);
		} catch(IOException ex){
			IO.close(lock);
			throw new JyroException("fail to lock: " + lockFile, ex);
		}
*/
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
/*
		IO.close(lock);
		lock = null;
*/
		return;
	}

	// ======================================================================
	// Retrieve Temporary Directory
	// ======================================================================
	/**
	 * Retrieve temporary directory
	 *
	 * @return temporary directory of this instance.
	 */
	private File getTemporaryDirectory(){
		return new File(dir, Const.DIR_TMP);
	}

}
