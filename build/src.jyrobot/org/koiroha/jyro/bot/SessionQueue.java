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
 * セッションを保持するキューです。
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
	 * @param config configuration of this session queue
	 */
	public void configure(Config config);

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
	public int resetAllSessions() throws CrawlerException;

	// ======================================================================
	// Retrieve Next Session
	// ======================================================================
	/**
	 * このスケジューラーから次に処理を行うセッションを参照します。
	 *
	 * @return the session that will execute next
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

}
