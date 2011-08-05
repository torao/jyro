/* **************************************************************************
 * Copyright (C) 2008 BJoRFUAN. All Right Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * BSD License, and comes with NO WARRANTY.
 *
 *                                                 torao <torao@bjorfuan.com>
 *                                                       http://www.moyo.biz/
 * $Id:$
*/
package org.koiroha.jyro.env.console;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.env.JyroPlatform;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Launcher: Jyro Launcher
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The Jyro launcher from console.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public final class Launcher {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Launcher.class);

	// ======================================================================
	// Node Name
	// ======================================================================
	/**
	 * Node name of jyro instance.
	 */
	private final String name;

	// ======================================================================
	// Home Directory
	// ======================================================================
	/**
	 * The home directory of jyro to launch.
	 */
	private final File home;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Constructor hidden in class.
	 *
	 * @param args commandline arguments
	 */
	private Launcher(String[] args) {

		// parse commandline parameters
		String name = ManagementFactory.getRuntimeMXBean().getName();
		String home = System.getProperty(Jyro.JYRO_HOME, ".");
		for (String arg : args) {

			home = arg;
		}
		this.home = new File(home);
		this.name = name;
		logger.info(Jyro.JYRO_HOME + "=" + this.home);
		return;
	}

	// ======================================================================
	// Startup Service
	// ======================================================================
	/**
	 * Startup service.
	 *
	 * @throws JyroException if fail to startup
	 */
	public void startup() throws JyroException {
		ClassLoader loader = java.lang.Thread.currentThread().getContextClassLoader();
		JyroPlatform platform = new JyroPlatform(name, home, loader, null);
		platform.startup();
		return;
	}

	// ======================================================================
	// Startup JYRO with Console
	// ======================================================================
	/**
	 * Console entry point of Jyro.
	 *
	 * @param args commandline arguments
	 * @throws JyroException
	 */
	public static void main(String[] args) throws JyroException {
		Launcher launcher = new Launcher(args);
		launcher.startup();
		return;
	}

}
