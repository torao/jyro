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
import java.net.*;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.*;

import javax.xml.xpath.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.koiroha.jyro.Jyro;
import org.koiroha.jyro.util.IO;
import org.w3c.dom.*;



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
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final byte[] EMPTY_BYTES = new byte[0];

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
	private long requestInterval = 1 * 1000;

	// ======================================================================
	// User-Agent
	// ======================================================================
	/**
	 * The value of User-Agent header.
	 */
	private String userAgent = "Mozilla/5.0 (compatible; Jyrobot/" + Jyro.VERSION + "; +http://www.koiroha.org/jyro.html)";

	// ======================================================================
	// Jyrobot Adapter
	// ======================================================================
	/**
	 * The stub object of Jyrobot.
	 */
	private JyrobotAdapter adapter = null;

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

	// ======================================================================
	// Refer User-Agent
	// ======================================================================
	/**
	 * このクローラーのユーザエージェントを参照します。
	 *
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	// ======================================================================
	// Set User-Agent
	// ======================================================================
	/**
	 * このクローラーが使用するユーザエージェントを設定します。
	 *
	 * @param userAgent the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return;
	}

	// ======================================================================
	// Refer Max Content Length
	// ======================================================================
	/**
	 * クローリングで取得する内容の最大サイズを参照します。
	 *
	 * @return the maxContentLength
	 */
	public long getMaxContentLength() {
		return maxContentLength;
	}

	// ======================================================================
	// Set Max Content Length
	// ======================================================================
	/**
	 * クローリングで取得する内容の最大サイズを設定します。
	 *
	 * @param maxContentLength the maxContentLength to set
	 */
	public void setMaxContentLength(long maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	// ======================================================================
	// Refer Request Interval
	// ======================================================================
	/**
	 * 同一サイトに対するリクエスト間隔を参照します。
	 *
	 * @return request interval
	 */
	public long getRequestInterval() {
		return requestInterval;
	}

	// ======================================================================
	// Set Request Interval
	// ======================================================================
	/**
	 * 同一サイトに対するリクエスト間隔を設定します。
	 *
	 * @param requestInterval request interval
	 */
	public void setRequestInterval(long requestInterval) {
		this.requestInterval = requestInterval;
		return;
	}

	/**
	 * @return the adapter
	 */
	public JyrobotAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(JyrobotAdapter adapter) {
		this.adapter = adapter;
		return;
	}

	// ======================================================================
	// Execute Crawling
	// ======================================================================
	/**
	 * クロール処理を開始します。
	*/
	@Override
	public void run(){
		assert(adapter != null);
		NumberFormat nf = NumberFormat.getNumberInstance();

		try {
			while(! Thread.interrupted()){
				Session session = scheduler.next();
				long start = Util.getUptime();
				try {
					crawl(session);
				} finally {
					Util.close(session);
				}
				logger.info("finish crawling session for " + session.getPrefixURI());
				logger.info("total " +
					nf.format(Util.getUptime() - start) + "ms; " +
					nf.format(session.getTotalUrl()) + " urls; " +
					nf.format(session.getTotalRetrieval()) + " bytes retrieved");
			}
		} catch(InterruptedException ex){
		}
		return;
	}

	// ======================================================================
	// Execute Crawling
	// ======================================================================
	/**
	 * 指定されたセッションに対するクローリング処理を実行します。
	 *
	 * @param session session for crawling
	 * @throws InterruptedException if interrupted in sleep interval
	*/
	private void crawl(Session session) throws InterruptedException{
		assert(session != null);

		// TODO read robots.txt...

		long start = Util.getUptime();
		Session.Request request = session.take();
		while(request != null){
			try {

				// リクエストを実行
				URI uri = request.getUri();
				Content content = null;
				try {
					content = retrieve(uri);
				} finally {
					session.save(request, content);
				}

				// 内容取得成功の通知
				adapter.success(request, content);

				// 取得した  URL をスケジューラーに投入
				for(URI u: extractURL(content)){
					scheduler.put(new Session.Request(u));
				}

				// 次のリクエストまでスリープ
				long interval = getRequestInterval() - (Util.getUptime() - start);
				if(interval > 0){
					Thread.sleep(interval);
				}
			} catch(IOException ex){
				if(adapter.failure(request, ex)){
					break;
				}
			}

			// 次のリクエストを参照
			start = Util.getUptime();
			request = session.take();
		}
		return;
	}

	// ======================================================================
	// Retrieve Content
	// ======================================================================
	/**
	 * 指定された URI の内容を取得します。
	 *
	 * @param uri URI of content
	 * @return content
	 * @throws IOException if fail to retrieve content from specified uri
	*/
	private Content retrieve(URI uri) throws IOException{
		logger.debug("retrieve(" + uri + ")");

		// リクエストを準備
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri);
		request.setHeader("User-Agent", getUserAgent());
		// TODO additional header not implemented

		HttpResponse response = null;
		byte[] binary = EMPTY_BYTES;
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
				long remaining = getMaxContentLength();
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

		// リクエストの構築
		Content.Request req = new Content.Request(
				request.getRequestLine().getMethod(),
				request.getURI(),
				request.getRequestLine().getProtocolVersion().toString());
		for(Header header: request.getAllHeaders()){
			req.addHeader(header.getName(), header.getValue());
		}
		req.setContent(EMPTY_BYTES);	// TODO how to retrieve query string when post?

		// レスポンスの構築
		Content.Response res = new Content.Response(
				response.getStatusLine().getProtocolVersion().toString(),
				response.getStatusLine().getStatusCode(),
				response.getStatusLine().getReasonPhrase());
		for(Header header: response.getAllHeaders()){
			res.addHeader(header.getName(), header.getValue());
		}
		res.setContent(binary);

		//
		return new Content(uri, req, res);
	}

	// ======================================================================
	// Extract URI
	// ======================================================================
	/**
	 * 指定された内容に含まれている URI を抽出します。
	 *
	 * @param content content to extract urls
	 * @return list of url
	*/
	private Set<URI> extractURL(Content content) {
		if("text/html".equals(content.response.getContentType())){
			return extractURLFromHTML(content);
		}
		return Collections.emptySet();
	}

	// ======================================================================
	// Extract URI
	// ======================================================================
	/**
	 * 指定された HTML に含まれている URI を抽出します。
	 *
	 * @param content content to extract urls
	 * @return list of url
	*/
	private Set<URI> extractURLFromHTML(Content content) {
		Set<URI> urls = new HashSet<URI>();
		// TODO <base> not considered

		// HTML ドキュメントの参照
		Document doc = content.getDocument();
		Charset charset = content.getDocumentCharset();
		URI base = content.getURI();
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			logger.debug(base);
			for(String expr: Util.HREF_XPATH){
				NodeList nl = (NodeList)xpath.evaluate(expr, doc, XPathConstants.NODESET);
				for(int i=0; i<nl.getLength(); i++){
					String href = ((Attr)nl.item(i)).getValue();
					URI uri = getURL(base, href, charset);
					if(uri != null && adapter.accept(content, uri) && !urls.contains(uri)){
						logger.debug("  --> " + uri + " (" + href + ")");
						urls.add(uri);
					}
				}
			}
		} catch(XPathException ex){
			throw new IllegalStateException(ex);
		}

		return urls;
	}

	// ======================================================================
	// Parse URI
	// ======================================================================
	/**
	 * 文字列形式の URI からインスタンスを構築します。
	 *
	 * @param base base uri
	 * @param href attribute value of href, src or else
	 * @param charset page character set
	 * @return absolute url
	*/
	private static URI getURL(URI base, String href, Charset charset) {
		href = href.trim();

		// TODO remove hash

		// URL に含まれる日本語などの文字をエンコード
		StringBuilder buffer = new StringBuilder();
		for(int i=0; i<href.length(); i++){
			char ch = href.charAt(i);
			if(ch <= 0xFF && !Character.isISOControl(ch)){
				buffer.append(ch);
			} else {
				String str = String.valueOf(ch);
				byte[] bin = str.getBytes(charset);
				for(int j=0; j<bin.length; j++){
					buffer.append('%');
					buffer.append(Integer.toString((bin[j] >> 4) & 0x0F, 16));
					buffer.append(Integer.toString((bin[j] >> 0) & 0x0F, 16));
				}
			}
		}
		href = buffer.toString();

		// URI がサポートしている形式であれば返す
		try {
			URI sub = new URI(href);
			URI uri = base.resolve(sub);
			if(Util.getDefaultPort(uri.getScheme()) >= 0){
				return uri;
			}
		} catch(URISyntaxException ex){
			logger.debug("unsupported uri detected: " + href);
		}
		return null;
	}

}
