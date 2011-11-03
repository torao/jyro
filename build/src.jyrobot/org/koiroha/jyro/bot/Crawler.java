/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.bot;

import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.util.Util;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Crawler: クローラー
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/08/06 Java SE 6
 */
public class Crawler implements Runnable {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Crawler.class);

	// ======================================================================
	// Empty Byte Array
	// ======================================================================
	/**
	 * 長さ 0 の配列です。
	 */
//	private static final byte[] EMPTY_BYTES = new byte[0];

	// ======================================================================
	// Jyrobot
	// ======================================================================
	/**
	 * このクローラーを管理しているボットです。
	 */
	private final Jyrobot jyrobot;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 *
	 * @param jyrobot bot of this crawler
	 */
	public Crawler(Jyrobot jyrobot) {
		this.jyrobot = jyrobot;
		return;
	}

	// ======================================================================
	// Refer Max Content Length
	// ======================================================================
	/**
	 * クローリングで取得する内容の最大サイズ (バイト) を参照します。
	 *
	 * @return limit for content length in bytes
	 */
	public long getMaxContentLength() {
		return jyrobot.getConfig().getLong("crawler", "max_content_length");
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
		return jyrobot.getConfig().getLong("crawler", "request_interval");
	}

	// ======================================================================
	// Execute Crawling
	// ======================================================================
	/**
	 * クロール処理を開始します。
	*/
	@Override
	public void run(){
		logger.debug("starting crawler");

		try {
			while(! Thread.interrupted()){

				// スケジューラから次のセッションを取得
				Session session = jyrobot.getSessionQueue().poll();
				logger.info("start crawling session for " + session);

				// クローリング開始を通知
				for(BotClient c: jyrobot.getBotClients()){
					c.startSession(session);
				}

				// クローリング処理を実行
				long start = Util.getUptime();
				try {
					crawl(session);
				} catch(CrawlerException ex){
					logger.fatal("fail to crawl!", ex);
				} finally {
					try {
						session.close();
					} catch(CrawlerException ex){
						logger.fatal("fail to close", ex);
					}
				}

				// クロール結果をログ出力
				NumberFormat nf = NumberFormat.getNumberInstance();
				logger.info("finish crawling session for " + session + "; total " +
					nf.format(Util.getUptime() - start) + "ms; " +
					nf.format(session.getTotalRequests()) + " requests; " +
					nf.format(session.getTotalRetrieval()) + " bytes retrieved");

				// クローリング終了を通知
				for(BotClient c: jyrobot.getBotClients()){
					c.endSession(session);
				}
			}
		} catch(InterruptedException ex){
			logger.info("crawling interrupted");
		}
		logger.debug("end crawler");
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
	 * @throws CrawlerException if fail to crawl over specified session
	*/
	private void crawl(Session session) throws CrawlerException, InterruptedException{
		assert(session != null);

		// TODO read robots.txt...

		long start = Util.getUptime();
		Request request = session.poll();
		while(request != null){

			// リクエストの実行
			crawl(session, request);

			// 次のリクエストまでスリープ
			long interval = getRequestInterval() - (Util.getUptime() - start);
			if(interval > 0){
				Thread.sleep(interval);
			}

			// 次のリクエストを参照
			start = Util.getUptime();
			request = session.poll();
		}
		return;
	}

	// ======================================================================
	// Execute Crawling
	// ======================================================================
	/**
	 * 指定されたリクエストに対する処理を実行します。
	 *
	 * @param session session for crawling
	 * @param request request
	*/
	private void crawl(Session session, Request request) {
		assert(request != null);
		logger.debug("crawl(" + request + ")");

		// リクエスト開始を通知してコールバックが必要なクライアントを取得
		List<BotClient> clients = new ArrayList<BotClient>();
		for(BotClient c: jyrobot.getBotClients()){
			if(c.startRequest(session, request.getUrl())){
				clients.add(c);
			}
		}

		// 通知対象のクライアントが存在しなければ終了
		if(clients.size() == 0){
			return;
		}

		try {

			// リクエストを実行し結果を通知
			Response response = request.get();
			for(BotClient c: clients){
				c.endRequest(session, request, response);
			}

			// レスポンスから URL を取得
			Set<URL> urls = new HashSet<URL>();
			for(BotClient c: clients){
				for(URL url: c.parse(session, request, response)){
					urls.add(url);
				}
			}

			// 取得した  URL をスケジューラーに投入
			for(URL url: urls){
				jyrobot.getSessionQueue().offer(url);
			}
		} catch(CrawlerException ex){
			for(BotClient c: clients){
				c.requestFailed(session, request, ex);
			}
		}
		return;
	}
//
//	// ======================================================================
//	// Retrieve Content
//	// ======================================================================
//	/**
//	 * 指定された URI の内容を取得します。
//	 *
//	 * @param uri URI of content
//	 * @return content
//	 * @throws IOException if fail to retrieve content from specified uri
//	*/
//	private Content retrieve(URI uri) throws IOException{
//		logger.debug("retrieve(" + uri + ")");
//
//		// リクエストを準備
//		HttpClient client = new DefaultHttpClient();
//		HttpGet request = new HttpGet(uri);
//		request.setHeader("User-Agent", getUserAgent());
//		// TODO additional header not implemented
//
//		HttpResponse response = null;
//		byte[] binary = EMPTY_BYTES;
//		InputStream in = null;
//		try {
//
//			// read response content
//			response = client.execute(request);
//			HttpEntity entity = response.getEntity();
//			if(entity != null){
//
//				// read content
//				in = entity.getContent();
//				ByteArrayOutputStream out = new ByteArrayOutputStream();
//				byte[] buffer = new byte[1024];
//				long remaining = getMaxContentLength();
//				while(true){
//					int len = (int)Math.min(buffer.length, remaining);
//					len = in.read(buffer, 0, len);
//					if(len < 0){
//						break;
//					}
//					out.write(buffer, 0, len);
//					remaining -= len;
//				}
//				binary = out.toByteArray();
//			}
//		} finally {
//			IO.close(in);
//		}
//
//		// リクエストの構築
//		Content.Request req = new Content.Request(
//				request.getRequestLine().getMethod(),
//				request.getURI(),
//				request.getRequestLine().getProtocolVersion().toString());
//		for(Header header: request.getAllHeaders()){
//			req.addHeader(header.getName(), header.getValue());
//		}
//		req.setContent(EMPTY_BYTES);	// TODO how to retrieve query string when post?
//
//		// レスポンスの構築
//		Content.Response res = new Content.Response(
//				response.getStatusLine().getProtocolVersion().toString(),
//				response.getStatusLine().getStatusCode(),
//				response.getStatusLine().getReasonPhrase());
//		for(Header header: response.getAllHeaders()){
//			res.addHeader(header.getName(), header.getValue());
//		}
//		res.setContent(binary);
//
//		//
//		return new Content(uri, req, res);
//	}
//
//	// ======================================================================
//	// Extract URI
//	// ======================================================================
//	/**
//	 * 指定された内容に含まれている URI を抽出します。
//	 *
//	 * @param content content to extract urls
//	 * @return list of url
//	*/
//	private Set<URI> extractURL(Content content) {
//		if("text/html".equals(content.response.getContentType())){
//			return extractURLFromHTML(content);
//		}
//		return Collections.emptySet();
//	}
//
//	// ======================================================================
//	// Extract URI
//	// ======================================================================
//	/**
//	 * 指定された HTML に含まれている URI を抽出します。
//	 *
//	 * @param content content to extract urls
//	 * @return list of url
//	*/
//	private Set<URI> extractURLFromHTML(Content content) {
//		Set<URI> urls = new HashSet<URI>();
//		// TODO <base> not considered
//
//		// HTML ドキュメントの参照
//		Document doc = content.getDocument();
//		Charset charset = content.getDocumentCharset();
//		URI base = content.getURI();
//		XPath xpath = XPathFactory.newInstance().newXPath();
//		try {
//			logger.debug(base);
//			for(String expr: Util.HREF_XPATH){
//				NodeList nl = (NodeList)xpath.evaluate(expr, doc, XPathConstants.NODESET);
//				for(int i=0; i<nl.getLength(); i++){
//					String href = ((Attr)nl.item(i)).getValue();
//					URI uri = getURL(base, href, charset);
//					if(uri != null && adapter.accept(content, uri) && !urls.contains(uri)){
//						logger.debug("  --> " + uri + " (" + href + ")");
//						urls.add(uri);
//					}
//				}
//			}
//		} catch(XPathException ex){
//			throw new IllegalStateException(ex);
//		}
//
//		return urls;
//	}
//
//	// ======================================================================
//	// Parse URI
//	// ======================================================================
//	/**
//	 * 文字列形式の URI からインスタンスを構築します。
//	 *
//	 * @param base base uri
//	 * @param href attribute value of href, src or else
//	 * @param charset page character set
//	 * @return absolute url
//	*/
//	private static URI getURL(URI base, String href, Charset charset) {
//		href = href.trim();
//
//		// TODO remove hash
//
//		// URL に含まれる日本語などの文字をエンコード
//		StringBuilder buffer = new StringBuilder();
//		for(int i=0; i<href.length(); i++){
//			char ch = href.charAt(i);
//			if(ch <= 0xFF && !Character.isISOControl(ch)){
//				buffer.append(ch);
//			} else {
//				String str = String.valueOf(ch);
//				byte[] bin = str.getBytes(charset);
//				for(int j=0; j<bin.length; j++){
//					buffer.append('%');
//					buffer.append(Integer.toString((bin[j] >> 4) & 0x0F, 16));
//					buffer.append(Integer.toString((bin[j] >> 0) & 0x0F, 16));
//				}
//			}
//		}
//		href = buffer.toString();
//
//		// URI がサポートしている形式であれば返す
//		try {
//			URI sub = new URI(href);
//			URI uri = base.resolve(sub);
//			if(Util.getDefaultPort(uri.getScheme()) >= 0){
//				return uri;
//			}
//		} catch(URISyntaxException ex){
//			logger.debug("unsupported uri detected: " + href);
//		}
//		return null;
//	}

}
