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

import java.net.URI;

import org.koiroha.jyrobot.Session.Request;


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyrobot: Web クローラーボット
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/28 Java SE 6
 */
public class Jyrobot {

	// ======================================================================
	// スケジューラ
	// ======================================================================
	/**
	 * ジョブスケジューラーです。
	 */
	private final Scheduler scheduler;

	// ======================================================================
	// クローラー
	// ======================================================================
	/**
	 * クローラーです。
	 */
	private final Crawler crawler;

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * JPA エンティティマネージャファクトリの名前を指定して構築を行います。
	 *
	 * @param name エンティティマネージャファクトリ名
	 */
	public Jyrobot(String name) {
		this.scheduler = new Scheduler(name);
		this.crawler = new Crawler(scheduler);
		return;
	}

	// ======================================================================
	// Refer Polling Interval
	// ======================================================================
	/**
	 * 実行キューのポーリング間隔を参照します。デフォルトは 1 秒に設定されて
	 * います。
	 *
	 * @return queue polling interval in milliseconds
	 */
	public long getQueuePollingInterval() {
		return scheduler.getQueuePollingInterval();
	}

	// ======================================================================
	// Set Polling Interval
	// ======================================================================
	/**
	 * 実行キューのポーリング間隔を設定します。
	 *
	 * @param queuePollingInterval queue polling interval in milliseconds
	 */
	public void setQueuePollingInterval(long queuePollingInterval) {
		scheduler.setQueuePollingInterval(queuePollingInterval);
		return;
	}

	// ======================================================================
	// Refer Site Access Interval
	// ======================================================================
	/**
	 * 同一サイトに対するアクセス間隔を参照します。
	 *
	 * @return visit interval in milliseconds
	 */
	public long getSiteAccessInterval() {
		return scheduler.getSiteAccessInterval();
	}

	// ======================================================================
	// Set Site Access Interval
	// ======================================================================
	/**
	 * 同一サイトに対するアクセス間隔を設定します。
	 *
	 * @param siteAccessInterval visit interval in milliseconds
	 */
	public void setSiteAccessInterval(long siteAccessInterval) {
		scheduler.setSiteAccessInterval(siteAccessInterval);
		return;
	}

	// ======================================================================
	// Refer User-Agent
	// ======================================================================
	/**
	 * クローラーが使用するユーザエージェントを参照します。
	 *
	 * @return value of User-Agent header
	 */
	public String getUserAgent() {
		return crawler.getUserAgent();
	}

	// ======================================================================
	// Set User-Agent
	// ======================================================================
	/**
	 * クローラーが使用するユーザエージェントを設定します。
	 *
	 * @param userAgent value of User-Agent header
	 */
	public void setUserAgent(String userAgent) {
		crawler.setUserAgent(userAgent);
		return;
	}

	// ======================================================================
	// Refer Max Content Length
	// ======================================================================
	/**
	 * クローリングで取得する内容の最大取得サイズを参照します。
	 *
	 * @return the maxContentLength
	 */
	public long getMaxContentLength() {
		return crawler.getMaxContentLength();
	}

	// ======================================================================
	// Set Max Content Length
	// ======================================================================
	/**
	 * クローリングで取得する内容の最大取得サイズを設定します。
	 *
	 * @param maxContentLength the maxContentLength to set
	 */
	public void setMaxContentLength(long maxContentLength) {
		crawler.setMaxContentLength(maxContentLength);
		return;
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
		return crawler.getRequestInterval();
	}

	// ======================================================================
	// Set Request Interval
	// ======================================================================
	/**
	 * 同一サイトに対するリクエスト間隔を瀬底します。
	 *
	 * @param requestInterval request interval
	 */
	public void setRequestInterval(long requestInterval) {
		crawler.setRequestInterval(requestInterval);
		return;
	}

	/**
	 * @return the adapter
	 */
	public JyrobotAdapter getAdapter() {
		return crawler.getAdapter();
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(JyrobotAdapter adapter) {
		crawler.setAdapter(adapter);
		return;
	}

	// ======================================================================
	// Start Crawling
	// ======================================================================
	/**
	 * クローリングを開始します。
	 *
	 * @param requestInterval request interval
	 */
	public void put(String uri){
		scheduler.put(new Request(URI.create(uri)));
		return;
	}

	// ======================================================================
	// Start Crawling
	// ======================================================================
	/**
	 * クローリングを開始します。
	 *
	 * @param requestInterval request interval
	 */
	public void reset(){
		scheduler.resetAllSessions();
		return;
	}

	// ======================================================================
	// Start Crawling
	// ======================================================================
	/**
	 * クローリングを開始します。
	 *
	 * @param requestInterval request interval
	 */
	public void start(){
		for(int i=0; i<5; i++){
			Thread t = new Thread(crawler, "Crawler-" + i);
			t.start();
		}
		return;
	}

	public static void main(String[] args) throws Exception{
		JyrobotAdapter adapter = new JyrobotAdapter() {
			@Override
			public void success(Request request, Content content) {
			}
			@Override
			public void failure(Request request, Throwable ex) {
			}
			@Override
			public boolean accept(URI uri) {
				return true;
			}
		};
		Jyrobot jyrobot = new Jyrobot("jyrobot");
		jyrobot.setAdapter(adapter);
		jyrobot.put("http://www.yahoo.co.jp");
		jyrobot.put("http://www.google.co.jp");
		jyrobot.put("http://www.goo.co.jp");
		jyrobot.reset();
		jyrobot.start();
		return;
	}

}
