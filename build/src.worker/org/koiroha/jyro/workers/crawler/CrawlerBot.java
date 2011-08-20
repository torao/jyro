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

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.xml.Xml;
import org.koiroha.xml.parser.HTMLDocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// CrawlerBotImpl: Crawler Robot Implementation
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The implementation class of crawler bot.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/08/06 Java SE 6
 */
public class CrawlerBot extends Worker {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(CrawlerBot.class);

	// ======================================================================
	// Max Content Length
	// ======================================================================
	/**
	 * Max content length as byte to read.
	 */
	private long maxContentLength = 1 * 1024 * 1024;

	// ======================================================================
	// User-Agent
	// ======================================================================
	/**
	 * The value of User-Agent header.
	 */
	private String userAgent = "Mozilla/5.0 (compatible; Jyrobot/" + Jyro.VERSION + "; +http://www.koiroha.org/jyro.html)";

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Nothing to do in constructor.
	 */
	public CrawlerBot() {
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Nothing to do in constructor.
	 */
	public <T> T getInterface(Class<T> type){
		return null;
	}

	// ======================================================================
	// URL Content Retrieve Stage
	// ======================================================================
	/**
	 * Retrieve content of specified object.
	 *
	 * @param content content to retrieve
	 * @throws WorkerException if fail to retrieve
	*/
	@Override
	public void retrieveContent(Content content) throws WorkerException{
		InputStream in = null;
		try {

			// execute request
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(content.uri);
			request.setHeader("User-Agent", userAgent);
			if(content.referer != null){
				request.setHeader("Referer", content.referer.toASCIIString());
			}

			// read response content
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if(entity != null){
				Header header = entity.getContentType();
				if(header != null){
					content.contentType = header.getValue();
				}
				in = entity.getContent();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				long remaining = maxContentLength;
				while(true){
					int len = (int)Math.min(buffer.length, remaining);
					len = in.read(buffer, 0, len);
					if(len < 0){
						break;
					}
					out.write(buffer, 0, len);
					remaining -= len;
				}
				content.content = out.toByteArray();
			}
		} catch(Exception ex){
			throw new WorkerException(ex);
		}

		getInterface(CrawlerBot.class).analyzeContent(content);
		return;
	}

	// ======================================================================
	// Content Analyzer Stage
	// ======================================================================
	/**
	 * Parse content.
	 *
	 * @param content content to analyze
	 * @throws WorkerException if fail to retrieve
	*/
	@Override
	@Stage
	public void analyzeContent(Content content) throws WorkerException {
		List<URI> urls = new ArrayList<URI>();
		try {
			if(content.contentType.toLowerCase().startsWith("text/html")){

				// parse html document
				Charset def = Xml.getCharset(content.contentType);
				HTMLDocumentBuilderFactory factory = new HTMLDocumentBuilderFactory();
				InputSource is = factory.guessInputSource(new ByteArrayInputStream(content.content), def.name(), content.content.length);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(is);

				// extract link
				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList nl = (NodeList)xpath.evaluate("//a/@href", doc, XPathConstants.NODESET);
				for(int i=0; i<nl.getLength(); i++){
					String href = ((Attr)nl.item(i)).getValue();
					urls.add(content.uri.resolve(href));
				}
			}
		} catch(Exception ex){
			throw new WorkerException(ex);
		}
		return;
	}

	// ======================================================================
	// Retrieve URLs
	// ======================================================================
	/**
	 * Retrieve URLs that will used to specify uri as next content. You may
	 * override this method and customize what this crawler focus for.
	 *
	 * Default behavior retrieve from all href attribute of &lt;a&gt; element.
	 *
	 * @param base base uri
	 * @param doc document
	 * @return collection of uri
	*/
	protected List<URI> extractLink(URI base, Document doc) {
		return extractLink(base, doc, "//a/@href");
	}

	// ======================================================================
	// Retrieve URLs
	// ======================================================================
	/**
	 * Utility method to retrieve URLs that contained in specified document.
	 *
	 * @param base base uri
	 * @param doc document
	 * @param expr xpath expression of uri
	 * @return collection of uri
	*/
	protected static List<URI> extractLink(URI base, Document doc, String... expr) {
		List<URI> uris = new ArrayList<URI>();
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			for(String e: expr){
				NodeList nl = (NodeList)xpath.evaluate(e, doc, XPathConstants.NODESET);
				for(int i=0; i<nl.getLength(); i++){
					String href = nl.item(i).getTextContent();
					uris.add(base.resolve(href));
				}
			}
		} catch(XPathException ex){
			throw new IllegalArgumentException(ex);
		}
		return uris;
	}

	// ======================================================================
	// Determine Port
	// ======================================================================
	/**
	 * Determine the port number of specified uri. Return negative value if
	 * URI scheme is not supported.
	 *
	 * @param uri URI
	 * @return port number
	*/
	private static int getPort(URI uri){
		int port = uri.getPort();
		if(port >= 0){
			return port;
		}
		String scheme = uri.getScheme();
		if(scheme.equals("http")){
			return 80;
		} else if(scheme.equals("https")){
			return 443;
		} else if(scheme.equals("ftp")){
			return 21;
		}
		return -1;
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
	public List<URI> retrieveLink(String url) throws WorkerException {
		logger.debug("running crawler to: " + url);

		List<URI> urls = new ArrayList<URI>();
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			request.setHeader("User-Agent", "Mozilla/5.0 (compatible; Jyrobot/" + Jyro.VERSION + "; +http://www.koiroha.org/jyro.html)");
			HttpResponse response = client.execute(request);

			Header header = response.getFirstHeader("Content-Type");
			String contentType = header.getValue();
			logger.debug("Content-Type: " + contentType);
			if(contentType.toLowerCase().startsWith("text/html")){
				DocumentBuilderFactory factory = new HTMLDocumentBuilderFactory();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(response.getEntity().getContent());

				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList nl = (NodeList)xpath.evaluate("//a/@href", doc, XPathConstants.NODESET);
				for(int i=0; i<nl.getLength(); i++){
					String href = ((Attr)nl.item(i)).getValue();
					urls.add(URI.create(url).resolve(href));
				}
			}
		} catch(Exception ex){
			throw new WorkerException(ex);
		}
		return urls;
	}

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
