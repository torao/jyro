/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.env.webapp;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.Jyro;
import org.koiroha.jyro.env.JyroPlatform;
import org.koiroha.jyro.impl.*;
import org.koiroha.jyro.util.*;
import org.w3c.dom.Document;

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
	// Jyro Platform
	// ======================================================================
	/**
	 *
	 */
	private JyroPlatform platform = null;

	// ======================================================================
	// Template Cache
	// ======================================================================
	/**
	 * XSL template cache.
	 */
	private final Map<String,Templates> xslCache
		= Collections.synchronizedMap(new HashMap<String, Templates>());

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

		// build and startup Jyro
		String contextPath = getServletContext().getContextPath();
		ClassLoader loader = java.lang.Thread.currentThread().getContextClassLoader();
		try {
			platform = new JyroPlatform(contextPath, dir, loader, null);
			platform.startup();
		} catch (Exception ex) {
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
			if(platform != null){
				platform.shutdown();
			}
			platform = null;
		} catch(Exception ex){
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
	 * @throws ServletException if fail to execute servlet
	 * @throws IOException if fail to output
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		// respond core name list
		String[] elem = parsePathInfo(request);
		if(elem.length == 0 || elem[0].length() == 0){

			// build core list
			List<String> names = new ArrayList<String>();
			JyroImpl jyro = platform.getJyro();
			for(Cluster core: jyro.getClusters()){
				names.add(core.getName());
			}

			// send core name list
			response.setContentType("text/javascript+json;charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			Writer out = response.getWriter();
			Text.json(out, names);
			out.flush();
			return;
		}

		// respond node name list
		if(elem.length == 1){

			// build core list
			List<String> names = new ArrayList<String>();
			JyroImpl jyro = platform.getJyro();
			Cluster core = jyro.getCluster(elem[0]);
			for(Node node: core.getNodes()){
				names.add(node.getId());
			}

			// send core name list
			response.setContentType("text/javascript+json;charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			Writer out = response.getWriter();
			Text.json(out, names);
			out.flush();
			return;
		}

		return;
	}

	// ======================================================================
	// Serve POST Request
	// ======================================================================
	/**
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws ServletException if fail to execute servlet
	 * @throws IOException if fail to output
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String[] elem = parsePathInfo(request);

		String cluster = elem[0];
		String node = elem[1];
		try {
			String j = request.getParameter("job");
			Job job = Job.parse(j);
			platform.post(cluster, job);
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		} catch(Exception ex){
			throw new ServletException(ex);
		}

		return;
	}

	// ======================================================================
	// XSL
	// ======================================================================
	/**
	 *
	 * @param res HTTP response
	 * @param doc document
	 * @param prefix prefix of xsl file
	 * @return true if send complete
	 * @throws ServletException
	 * @throws IOException if fail to output
	 */
	private boolean send(HttpServletResponse res, Document doc, String prefix) throws ServletException, IOException {
		String path = prefix + ".xsl";
		InputStream in = null;
		try {

			// refer input stream for xsl file
			in = getServletContext().getResourceAsStream(path);
			if(in == null){
				return false;
			}

			// retrieve xsl templates
			Templates templates = xslCache.get(prefix);
			if(templates == null){
				TransformerFactory f = TransformerFactory.newInstance();
				templates = f.newTemplates(new StreamSource(in));
//				xslCache.put(prefix, templates);
			}

			Properties output = templates.getOutputProperties();
			res.setContentType(output.getProperty("media-type", "text/xml"));
			res.setHeader("Cache-Control", "no-cache");

			// transform document
			Transformer transformer = templates.newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(res.getOutputStream()));
		} catch(TransformerException ex){
			throw new ServletException(path, ex);
		} finally {
			IO.close(in);
		}
		return true;
	}

	// ======================================================================
	// Serve GET Request
	// ======================================================================
	/**
	 *
	 * @param req HTTP request
	 * @param res HTTP response
	 * @param doc response document
	 * @param prefix xsl prefix
	 * @throws ServletException
	 * @throws IOException if fail to output
	 */
	private void send(HttpServletRequest req, HttpServletResponse res, Document doc, String prefix) throws ServletException, IOException {
		RequestDispatcher rd = req.getRequestDispatcher(prefix + ".jsp");
		if(rd == null){
			res.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
			return;
		}
		req.setAttribute("jyro", doc);
		rd.forward(req, res);
		return;
	}

	// ======================================================================
	// Parse PATH_INFO
	// ======================================================================
	/**
	 * Separate PATH_INFO with slash.
	 *
	 * @param request HTTP request to retrieve PATH_INFO
	 * @return separated components
	 * @throws UnsupportedEncodingException
	 */
	private static String[] parsePathInfo(HttpServletRequest request) throws UnsupportedEncodingException{

		// redirect to top page if no PATH_INFO specified
		logger.debug(request.getRequestURI());
		String pathInfo = request.getPathInfo();
		if(pathInfo == null){
			pathInfo = "/";
		}
		// TODO servlet container portability problem
		pathInfo = new String(pathInfo.getBytes("iso-8859-1"), "UTF-8");
		pathInfo = pathInfo.replaceFirst("^/+", "");
		pathInfo = pathInfo.replaceFirst("/+$", "");

		// respond core name list
		return pathInfo.split("/+");
	}

}
