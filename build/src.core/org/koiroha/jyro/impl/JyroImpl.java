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
import java.util.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.JyroException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroImpl: Jyro Container
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Jyro container to host multi instance.
 *
 * @author takami torao
 */
public class JyroImpl {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JyroImpl.class);

	// ======================================================================
	// Generic Use Timer
	// ======================================================================
	/**
	 * The timer to detect modification and reload for cores.
	 */
	public static final Timer TIMER = Config.TIMER;

	// ======================================================================
	// Name
	// ======================================================================
	/**
	 * Human readable name of this instance.
	 */
	private final String name;

	// ======================================================================
	// Core Instance Map
	// ======================================================================
	/**
	 * The map of all JyroCore instance.
	 */
	private final Map<String,Cluster> clusters = new HashMap<String,Cluster>();

	// ======================================================================
	// Directory
	// ======================================================================
	/**
	 * Home directory of this jyro instance.
	 */
	private final File dir;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param name name of this instance
	 * @param dir home directory of this instance
	 * @param parent parent class loader
	 * @param prop init property replace with placeholder such as ${foo.bar}
	 * @throws JyroException if cannot configure instance
	 */
	public JyroImpl(String name, File dir, ClassLoader parent, Properties prop) throws JyroException{
		logger.debug("initializing Jyro on directory: " + dir);
		this.name = name;

		// check directory existance
		this.dir = dir;
		if(! dir.isDirectory()){
			logger.warn("specified parameter is not directory: " + dir);
			return;
		}

		// build jyro cores
		String[] names = dir.list();
		for(int i=0; names!=null && i<names.length; i++){
			File file = new File(dir, names[i]);
			if(file.isDirectory()){
				if(names[i].startsWith(".")){
					logger.debug(". directory ignored: " + names[i]);
					continue;
				}
				Cluster core = new Cluster(names[i], file, parent, prop);
				clusters.put(names[i], core);
			}
		}

		// logging stuations
		if(clusters.size() == 0){
			logger.warn("no jyro core load from: " + dir);
		} else {
			logger.debug("load " + clusters.size() + " cores: " + dir);
		}
		return;
	}

	// ======================================================================
	// Refer Name
	// ======================================================================
	/**
	 * Refer human-readable name of this instance.
	 *
	 * @return name
	 */
	public String getName() {
		return name;
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
	// Retrieve Jyro Cluster
	// ======================================================================
	/**
	 * Retrieve Jyro cluster as iterable.
	 *
	 * @return jyro cluster
	 */
	public Iterable<Cluster> getClusters(){
		return new ArrayList<Cluster>(clusters.values());
	}

	// ======================================================================
	// Retrieve Jyro Cluster
	// ======================================================================
	/**
	 * Retrieve Jyro cluster by specified name. Null will return is not found.
	 *
	 * @param name core name
	 * @return jyro core
	 */
	public Cluster getCluster(String name){
		return clusters.get(name);
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

		// boot with alphanumeric sequence
		List<String> names = new ArrayList<String>(clusters.keySet());
		Collections.sort(names);

		// startup all cores
		for(String name: names){
			Cluster core = clusters.get(name);
			core.startup();
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

		// shutdown with reverse alphanumeric sequence
		List<String> names = new ArrayList<String>(clusters.keySet());
		Collections.sort(names, Collections.reverseOrder());

		// shutdown all cores.
		for(String name: names){
			Cluster core = clusters.get(name);
			core.shutdown();
		}

		logger.debug("shutdown complete");
		return;
	}

}
