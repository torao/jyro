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
// Jyro: Jyro Container
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Jyro container to host multi instance.
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

		assert(VERSION.matches("\\d+\\.\\d+\\.\\d+"));
	}

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
	private final Map<String,JyroCore> cores = new HashMap<String,JyroCore>();

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
	public Jyro(String name, File dir, ClassLoader parent, Properties prop) throws JyroException{
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
				JyroCore core = new JyroCore(names[i], file, parent, prop);
				cores.put(names[i], core);
			}
		}

		// logging stuations
		if(cores.size() == 0){
			logger.warn("no jyro core load from: " + dir);
		} else {
			logger.debug("load " + cores.size() + " cores: " + dir);
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
	// Retrieve Jyro Cores
	// ======================================================================
	/**
	 * Retrieve Jyro cores as iterable.
	 *
	 * @return jyro cores
	 */
	public Iterable<JyroCore> getCores(){
		return new ArrayList<JyroCore>(cores.values());
	}

	// ======================================================================
	// Retrieve Jyro Core
	// ======================================================================
	/**
	 * Retrieve Jyro core by specified name. Null will return is not found.
	 *
	 * @param name core name
	 * @return jyro core
	 */
	public JyroCore getCore(String name){
		return cores.get(name);
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
		List<String> names = new ArrayList<String>(cores.keySet());
		Collections.sort(names);

		// startup all cores
		for(String name: names){
			JyroCore core = cores.get(name);
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
		List<String> names = new ArrayList<String>(cores.keySet());
		Collections.sort(names, Collections.reverseOrder());

		// shutdown all cores.
		for(String name: names){
			JyroCore core = cores.get(name);
			core.shutdown();
		}

		return;
	}

}
