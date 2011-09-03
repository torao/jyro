/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyrobot;

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
import org.koiroha.jyro.util.IO;
import org.koiroha.xml.Xml;
import org.koiroha.xml.parser.HTMLDocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Crawler: クローラー
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/08/06 Java SE 6
 */
class Crawler implements Runnable {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Crawler.class);

	// ======================================================================
	// Scheduler
	// ======================================================================
	/**
	 * このクローラーが使用するスケジューラです。
	 */
	private final Scheduler scheduler;

	// ======================================================================
	// Max Content Length
	// ======================================================================
	/**
	 * Max content length as byte to read.
	 */
	private long maxContentLength = 1 * 1024 * 1024;

	// ======================================================================
	// Wait Interval
	// ======================================================================
	/**
	 * 同一ホストに対するリクエストごとの待機時間です。
	 */
	private long waitInterval = 1 * 1000;

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
	 * コンストラクタは何も行いません。
	 *
	 * @param scheduler スケジューラー
	 */
	public Crawler(Scheduler scheduler) {
		this.scheduler = scheduler;
		return;
	}

	/**
	 * このクローラーのユーザエージェントを参照します。
	 *
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * このクローラーが使用するユーザエージェントを設定します。
	 *
	 * @param userAgent the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return;
	}

	/**
	 * クローリングで取得する内容の最大サイズを参照します。
	 *
	 * @return the maxContentLength
	 */
	public long getMaxContentLength() {
		return maxContentLength;
	}

	/**
	 * クローリングで取得する内容の最大サイズを設定します。
	 *
	 * @param maxContentLength the maxContentLength to set
	 */
	public void setMaxContentLength(long maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	// ======================================================================
	// Execute Crawling
	// ======================================================================
	/**
	 * クロール処理を開始します。
	*/
	@Override
	public void run(){
		try {
			while(! Thread.interrupted()){
				Session session = scheduler.next();
				try {
					Session.Request request = session.take();
					while(request != null){
						crawl(request);
						Thread.sleep(waitInterval);
						request = session.take();
					}
				} finally {
					Util.close(session);
				}
			}
		} catch(InterruptedException ex){
		}
		return;
	}

	// ======================================================================
	// Crawl
	// ======================================================================
	/**
	 * 指定されたリクエストを実行します。
	 *
	 * @param job
	 * @return
	 * @throws WorkerException
	*/
	private void crawl(Session.Request request) {
		URI uri = request.getUri();
		Content content = retrieve(uri);
		return;
	}

	// ======================================================================
	// URL Content Retrieve Stage
	// ======================================================================
	/**
	 * Retrieve content of specified URL.
	 *
	 * @param content content to retrieve
	 * @throws WorkerException if fail to retrieve
	*/
	private Content retrieve(URI uri) throws IOException{

		// リクエストを準備
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri);
		request.setHeader("User-Agent", userAgent);
		// TODO additional header not implemented

		HttpResponse response = null;
		byte[] binary = null;
		InputStream in = null;
		try {

			// read response content
			response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if(entity != null){

				// read content
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
				binary = out.toByteArray();
			}
		} finally {
			IO.close(in);
		}

		// build request
		Content.Request req = new Content.Request(
				request.getRequestLine().getMethod(),
				request.getURI(),
				request.getRequestLine().getProtocolVersion().toString());
		for(Header header: request.getAllHeaders()){
			req.addHeader(header.getName(), header.getValue());
		}
		req.setConten(new byte[0]);	// TODO how to retrieve query string when post?

		// build response
		Content.Response res = new Content.Response(
				response.getStatusLine().getProtocolVersion().toString(),
				response.getStatusLine().getStatusCode(),
				response.getStatusLine().getReasonPhrase());
		for(Header header: response.getAllHeaders()){
			res.addHeader(header.getName(), header.getValue());
		}
		res.setConten(binary);

		//
		return new Content(uri, req, res);
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
	private List<URI> analyzeContent(URI uri, String type, byte[] content) {
		List<URI> urls = Collections.emptyList();
		if(type.toLowerCase().startsWith("text/html")){
			urls = parseHtmlContent(uri, type, content);
		}
		return urls;
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
	private List<URI> parseHtmlContent(URI uri, String type, byte[] content) {
		List<URI> urls = new ArrayList<URI>();

		try {
			// HTML ドキュメントの解析
			Charset def = Xml.getCharset(type);
			HTMLDocumentBuilderFactory factory = new HTMLDocumentBuilderFactory();
			factory.setNamespaceAware(false);
			factory.setXIncludeAware(false);
			InputSource is = factory.guessInputSource(new ByteArrayInputStream(content), def.name(), content.length);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);

			// リンク部分の抽出
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nl = (NodeList)xpath.evaluate("//a/@href", doc, XPathConstants.NODESET);
			for(int i=0; i<nl.getLength(); i++){
				String href = ((Attr)nl.item(i)).getValue();
				urls.add(uri.resolve(href));
			}
		} catch(Exception ex){
			throw new IllegalStateException(ex);
		}
		return urls;
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
			request.setHeader("User-Agent", userAgent);
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

}
