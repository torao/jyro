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
// JyroServlet: Jyro Servlet
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The servlet for web console of Jyro.
 *
 * @author takami torao
 */
public class JyroServlet extends HttpServlet {

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
	private static final Logger logger = Logger.getLogger(JyroServlet.class);

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
	public JyroServlet() {
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
		String dirName = getInitParameter(Jyro.JYRO_HOME);
		if(dirName == null){
			dirName = getServletContext().getInitParameter(Jyro.JYRO_HOME);
			if(dirName == null){
				dirName = System.getProperty(Jyro.JYRO_HOME);
				if(dirName == null){
					throw new ServletException(Jyro.JYRO_HOME + " not specified in servlet or context paramter, system property");
				}
			}
		}

		// resolve relative path
		File dir = new File(dirName);
		if(! dir.isAbsolute()){
			dirName = getServletContext().getRealPath(dirName);
			dir = new File(dirName);
		}
		logger.info(Jyro.JYRO_HOME + "=" + dirName);

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
	 * Destroy this servlet.
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
	// Serve GET Request
	// ======================================================================
	/**
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws ServletException
	 * @throws IOException if fail to output
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// redirect to top page if no PATH_INFO specified
		String pathInfo = request.getPathInfo();
		if(pathInfo == null || pathInfo.length() == 0 || pathInfo.equals("/")){
			logger.debug("redirecting top page: pathInfo=" + pathInfo);
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		// send status for jyro
		if(pathInfo.equals("/status")){
			status(request, response);
			return;
		}

		// send 404 Not Found if unrecognized pathinfo sent
		logger.debug("pathinfo not found: " + pathInfo);
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}

	// ======================================================================
	// Send Status
	// ======================================================================
	/**
	 * Send status of Jyro.
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws ServletException
	 * @throws IOException if fail to output
	 */
	private void status(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/javascript+json; charset=utf-8");
		response.setHeader("Cache-Control", "no-cache");

		PrintWriter out = response.getWriter();
		out.write('[');
		out.write(']');
		out.flush();
		return;
	}

}
