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

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.koiroha.jyro.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Crawler: Crawler Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The worker class to crawl web sites.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/08/06 Java SE 6
 */
public class Crawler extends Worker {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Crawler.class);

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
	public Crawler() {
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
		logger.debug("running crawler to: " + url);

		try {
			HttpGet request = new HttpGet(url);
			request.setHeader("User-Agent", "Mozilla/1.0 (jyrobot)");
			HttpResponse response = client.execute(request);

			Header header = response.getFirstHeader("Content-Type");
			String contentType = header.getValue();
			logger.debug("Content-Type: " + contentType);
			if(contentType.equals("text/html")){

			}
			return null;
		} catch(Exception ex){
			throw new WorkerException(ex);
		}
	}

}
