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

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// SessionQueue: セッションキュー
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * セッションを参照するためのキューです。
 *
 * @version
 * @author torao
 * @since 2011/09/14 jyro 1.0
 */
public interface SessionQueue {

	// ======================================================================
	// Configure Instance
	// ======================================================================
	/**
	 * 指定された設定でインスタンスの初期設定を行うために呼び出されます。
	 * config parameter is map that defined on /scheduler/session_queue in jyrobot.yml
	 *
	 * @param jyrobot application instance
	 * @param config configuration of this session queue
	 * @throws CrawlerException
	 */
	public void configure(Jyrobot jyrobot, Config config) throws CrawlerException;

	// ======================================================================
	// Retrieve Next Session
	// ======================================================================
	/**
	 * このスケジューラーから次に処理を行うセッションを参照します。
	 * これ以上処理を行うセッションが存在しない場合 (クローラーを終了すべき場合) は null を返します。
	 *
	 * @return the session that will execute next, or null if no more
	 * available session.
	 * @throws InterruptedException if interrupted in waiting
	 */
	public Session poll() throws InterruptedException;

	// ======================================================================
	// Put Crawling URL
	// ======================================================================
	/**
	 * 指定された URL をクローリングキューに投入します。
	 *
	 * @param url the url that will be crawled
	 * @throws CrawlerException if fail to store request url
	 */
	public void offer(URL url) throws CrawlerException;

	// ======================================================================
	// Reset Crawling URL
	// ======================================================================
	/**
	 * 指定された URL がキューに存在する場合、クローリングスケジュールをリセットし未アクセス状態にします。
	 * このメソッドの呼び出しにより指定された URL は早い時期にクローリングが行われるようになります。
	 * URL が存在しない場合や既に実行中の場合は何も行わず false を返します。
	 *
	 * @param url URL to reset crawling schedule
	 * @param zombie accessed before timestamp of session that recognized
	 * as zombie and force reset in milliseconds
	 * @return true if crawling schedule reset normally, false if specified
	 * URL is not enqueued or now on crawling
	 * @throws CrawlerException if fail to reset schedule
	 */
	public boolean reset(URL url, long zombie) throws CrawlerException;

	// ======================================================================
	// Reset All Sessions
	// ======================================================================
	/**
	 * すべてのセッションの実行中フラグと前回アクセス日時をリセットし、次回の
	 * 処理で即時実行されるようにします。
	 *
	 * @return the number of sessions
	 * @throws CrawlerException if fail to reset sessions
	 */
	public int resetAll() throws CrawlerException;

}
