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
import java.nio.channels.FileLock;
import java.util.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.util.IO;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyro: Node Container
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
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

	// ======================================================================
	// Nodes
	// ======================================================================
	/**
	 * Nodes in this context.
	 */
	private final Map<String,List<NodeImpl>> nodes = new HashMap<String,List<NodeImpl>>();

	// ======================================================================
	// Directory
	// ======================================================================
	/**
	 * Home directory of this instance.
	 */
	private final File dir;

	// ======================================================================
	// Directory
	// ======================================================================
	/**
	 * Home directory of this instance.
	 */
	private RandomAccessFile lock = null;

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
	 * @param loader default class loader
	 */
	public Jyro(File dir, ClassLoader loader) {
		this.dir = dir;
		this.loader = loader;
		return;
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
		File lockFile = new File(tmp, JyroFactory.FILE_LOCK);
		try {
			lock = new RandomAccessFile(lockFile, "rw");
			FileLock fl = lock.getChannel().tryLock();
			if(fl == null){
				throw new JyroException("unable to acquire lock of home: " + lockFile);
			}
			logger.debug("${jyro.home} lock success: " + JyroFactory.DIR_TMP + "/" + JyroFactory.FILE_LOCK);
		} catch(IOException ex){
			IO.close(lock);
			throw new JyroException("fail to lock: " + lockFile, ex);
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
		IO.close(lock);
		lock = null;
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
		return new File(dir, JyroFactory.DIR_TMP);
	}

}
