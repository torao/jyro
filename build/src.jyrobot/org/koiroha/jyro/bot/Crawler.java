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
				if(session == null){
					break;
				}
				logger.info("start crawling session for " + session);

				// クローリング開始を通知
				for(BotClient c: jyrobot.getBotClients()){
					c.sessionStart(session);
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
					nf.format(Util.getUptime() - start) + "ms; " + session.getStat());

				// クローリング終了を通知
				for(BotClient c: jyrobot.getBotClients()){
					c.sessionEnd(session);
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
			if(c.prepareRequest(request)){
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
				c.requestSuccess(request, response);
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
			logger.error("", ex);
			for(BotClient c: clients){
				c.requestFailed(request, ex);
			}
		}
		return;
	}

}
