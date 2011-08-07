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
package org.koiroha.jyro.workers.crawler;

import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.xml.parser.HTMLDocumentBuilderFactory;
import org.w3c.dom.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Parser: Parser Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The worker class to crawl web sites.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/08/06 Java SE 6
 */
public class Parser extends Worker {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Parser.class);

	// ======================================================================
	// HTTP Client
	// ======================================================================
	/**
	 * HTTP client instance shared by all workers.
	 */
	private final HttpClient client;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 *
	 */
	public Parser() {
		this.client = new DefaultHttpClient();
		return;
	}

	// ======================================================================
	// Receive specified job
	// ======================================================================
	/**
	 *
	 * @param job
	 * @return
	 * @throws WorkerException
	*/
	@Override
	public Object receive(Job job) throws WorkerException {
		String url = job.getAttribute("url");
		List<String> urls = retrieveLink(url);
		WorkerContext context = getContext();
		return null;
	}

	// ======================================================================
	// Receive specified job
	// ======================================================================
	/**
	 *
	 * @param job
	 * @return
	 * @throws WorkerException
	*/
	public List<String> retrieveLink(String url) throws WorkerException {
		logger.debug("running crawler to: " + url);

		List<String> urls = new ArrayList<String>();
		try {
			HttpGet request = new HttpGet(url);
			request.setHeader("User-Agent", "Mozilla/5.0 (compatible; Jyrobot/" + Jyro.VERSION + "; +http://www.koiroha.org/bot.html)");
			HttpResponse response = client.execute(request);

			Header header = response.getFirstHeader("Content-Type");
			String contentType = header.getValue();
			logger.debug("Content-Type: " + contentType);
			if(contentType.equals("text/html")){
				DocumentBuilderFactory factory = new HTMLDocumentBuilderFactory();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(response.getEntity().getContent());

				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList nl = (NodeList)xpath.evaluate("//a/@href", doc, XPathConstants.NODESET);
				for(int i=0; i<nl.getLength(); i++){
					urls.add(((Attr)nl.item(i)).getValue());
				}
			}
		} catch(Exception ex){
			throw new WorkerException(ex);
		}
		return urls;
	}

	public static void main(String[] args) throws Exception {
		return;
	}
}
