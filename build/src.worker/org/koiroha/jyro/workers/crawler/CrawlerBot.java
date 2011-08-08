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

import java.io.Serializable;
import java.net.URI;

import org.koiroha.jyro.WorkerException;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// CrawlerBot: Crawler Robot Interface
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The interface of crawler robot.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/08/06 Java SE 6
 */
public interface CrawlerBot {

	// ======================================================================
	// URL Content Retrieve Stage
	// ======================================================================
	/**
	 * Retrieve content of specified object.
	 *
	 * @param content content to retrieve
	 * @throws WorkerException if fail to retrieve
	*/
	@Stage
	public void retrieveContent(Content content) throws WorkerException;

	// ======================================================================
	// Content Analyzer Stage
	// ======================================================================
	/**
	 * Parse content.
	 *
	 * @param content content to analyze
	 * @throws WorkerException if fail to retrieve
	*/
	@Stage
	public void analyzeContent(Content content) throws WorkerException;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Content:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 */
	public static class Content implements Serializable {

		/** serial version of this class. */
		private static final long serialVersionUID = 1L;

		/** The URI of this content. */
		public final URI uri;

		/** The referer URI of this content. */
		public final URI referer;

		/** Content type of this instance. */
		public String contentType = null;

		/** The binary of this content. */
		public byte[] content = null;

		/**
		 * @param uri URI of this content
		 * @param referer referer of this content
		 */
		public Content(URI uri, URI referer){
			this.uri = uri;
			this.referer = referer;
			return;
		}

	}

}
