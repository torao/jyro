/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.webapp;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// ConsoleServlet: Console Servlet
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The servlet for web console of Jyro.
 *
 * @author takami torao
 */
public class ConsoleServlet extends HttpServlet {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(ConsoleServlet.class);

	// ======================================================================
	// Jyro
	// ======================================================================
	/**
	 * Jyro instance.
	 */
	private Jyro jyro = null;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 *
	 */
	public ConsoleServlet() {
		return;
	}

	// ======================================================================
	// Initialize Servlet
	// ======================================================================
	/**
	 * Initialize this servlet.
	 *
	 * @throws ServletException if fail to initialize
	 */
	@Override
	public void init() throws ServletException {
		super.init();

		// retrieve jyro.home
		String dirName = getInitParameter("jyro.home");
		if(dirName == null){
			dirName = getServletContext().getInitParameter("jyro.home");
			if(dirName == null){
				dirName = System.getProperty("jyro.home");
				if(dirName == null){
					throw new ServletException("jyro.home not specified in servlet or context paramter, system property");
				}
			}
		}

		// resolve relative path
		File dir = new File(dirName);
		if(! dir.isAbsolute()){
			dirName = getServletContext().getRealPath(dirName);
			dir = new File(dirName);
		}
		logger.info("jyro.home=" + dirName);

		try {
			// build jyro instance
			this.jyro = new Jyro(dir, null, null);

			// startup jyro
			this.jyro.startup();
		} catch(JyroException ex){
			throw new ServletException(ex);
		}
		return;
	}

	// ======================================================================
	// Destroy Servlet
	// ======================================================================
	/**
	 *
	 * <p>
	*/
	@Override
	public void destroy() {

		// shutdown jyro
		try {
			if(this.jyro != null){
				this.jyro.shutdown();
			}
		} catch(JyroException ex){
			logger.fatal("fail to shutdown jyro", ex);
		}

		super.destroy();
		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return;
	}

}
