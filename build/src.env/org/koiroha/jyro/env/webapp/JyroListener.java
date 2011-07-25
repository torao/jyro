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
package org.koiroha.jyro.env.webapp;

import java.io.File;

import javax.servlet.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.Jyro;
import org.koiroha.jyro.env.JyroPlatform;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroListener:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public class JyroListener implements ServletContextListener {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JyroListener.class);

	// ======================================================================
	// Jyro Platform
	// ======================================================================
	/**
	 * The platform of Jyro.
	 */
	private JyroPlatform platform = null;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * The constructor called dynamically.
	 */
	public JyroListener() {
		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * <p>
	 * @param e
	 */
	@Override
	public void contextInitialized(ServletContextEvent e) {
		ServletContext context = e.getServletContext();

		// retrieve jyro.home
		String dirName = context.getInitParameter(Jyro.JYRO_HOME);
		if(dirName == null){
			dirName = System.getProperty(Jyro.JYRO_HOME);
			if(dirName == null){
				throw new IllegalStateException(Jyro.JYRO_HOME + " not specified in servlet or context paramter, system property");
			}
		}

		// resolve relative path
		File dir = new File(dirName);
		if(! dir.isAbsolute()){
			dirName = context.getRealPath(dirName);
			dir = new File(dirName);
		}
		logger.info(Jyro.JYRO_HOME + "=" + dirName);

		// build and startup Jyro
		String contextPath = context.getContextPath();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			platform = new JyroPlatform(contextPath, dir, loader, null);
			platform.startup();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * <p>
	 * @param e
	 */
	@Override
	public void contextDestroyed(ServletContextEvent e) {

		// shutdown jyro
		try {
			if(platform != null){
				platform.shutdown();
			}
			platform = null;
		} catch(Exception ex){
			logger.fatal("fail to shutdown jyro", ex);
		}

		return;
	}

}
