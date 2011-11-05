/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package sample;

import java.net.*;
import java.util.*;

import javax.xml.xpath.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.bot.*;
import org.w3c.dom.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// SampleBotClient:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version
 * @author torao
 * @since 2011/11/02 jyro 1.0
 */
public class SampleBotClient implements BotClient {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(SampleBotClient.class);

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param config
	 */
	@Override
	public void configure(Config config) {
		logger.info("configure(" + config + ")");
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @return
	 */
	@Override
	public boolean sessionStart(Session session) {
		logger.info("sessionStart(" + session + ")");
		return true;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 */
	@Override
	public void sessionEnd(Session session) {
		logger.info("sessionEnd(" + session + ")");
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @param url
	 * @return
	 */
	@Override
	public boolean prepareRequest(Request request) {
		logger.info("prepareRequest(" + request + ")");
		return true;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @param request
	 * @param response
	 */
	@Override
	public void requestSuccess(Request request, Response response) {
		logger.info("endRequest(" + request + "," + response + ")");
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @param request
	 * @param ex
	 */
	@Override
	public void requestFailed(Request request, Throwable ex) {
		logger.info("requestFailed(" + request + "," + ex + ")");
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param url
	 * @return
	 */
	@Override
	public boolean accept(URL url) {
		logger.info("accept(" + url + ")");
		return true;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param session
	 * @param request
	 * @param response
	 * @return
	 * @throws CrawlerException
	 */
	@Override
	public Iterable<URL> parse(Session session, Request request, Response response) throws CrawlerException {
		logger.info("parse(" + session + "," + request + "," + response + ")");
		URL base = request.getUrl();
		List<URL> urls = new ArrayList<URL>();
		try {
			Document doc = response.getDocument();

			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nl = (NodeList)xpath.evaluate(
				"//a/@href", doc, XPathConstants.NODESET);
			for(int i=0; i<nl.getLength(); i++){
				String href = nl.item(i).getTextContent();
				if(! href.startsWith("javascript:")){
					try {
						urls.add(new URL(base, href));
					} catch(MalformedURLException ex){
						ex.printStackTrace();
					}
				}
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return urls;
	}

}
