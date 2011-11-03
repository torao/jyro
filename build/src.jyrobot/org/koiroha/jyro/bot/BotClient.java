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
// BotClient: ボットクライアント
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * クローラーボットの取得したコンテンツを処理するアプリケーションインターフェースです。
 *
 * @version
 * @author torao
 * @since 2011/10/18 jyro 1.0
 */
public interface BotClient {

	// ======================================================================
	// Initialize
	// ======================================================================
	/**
	 * クライアントの処理開始時にこのクライアント用の設定を指定して呼び出されます。
	 *
	 * @param config settings for this client instance
	 */
	public void configure(Config config);

	// ======================================================================
	// Notify Start Session
	// ======================================================================
	/**
	 * 指定されたセッションの処理を開始する時に呼び出されます。
	 *
	 * @param session the session to start
	 * @return false if skip crawling this session
	 */
	public boolean sessionStart(Session session);

	// ======================================================================
	// Notify End Session
	// ======================================================================
	/**
	 * 指定されたセッションの処理が終了した時に呼び出されます。
	 *
	 * @param session the session to start
	 */
	public void sessionEnd(Session session);

	// ======================================================================
	// Notify Start Request
	// ======================================================================
	/**
	 * 指定された URL に対するリクエストが行われる時に呼び出されます。
	 *
	 * @param request request
	 * @return false if client has no need to request/response
	 */
	public boolean prepareRequest(Request request);

	// ======================================================================
	// Notify End Request
	// ======================================================================
	/**
	 * 指定されたリクエスト/レスポンスが完了した時に呼び出されます。
	 *
	 * @param request request
	 * @param response response
	 */
	public void requestSuccess(Request request, Response response);

	// ======================================================================
	// Notify Fail to Request
	// ======================================================================
	/**
	 * 指定されたリクエストが例外により失敗した時に呼び出されます。
	 *
	 * @param request request
	 * @param ex occurred exception
	 */
	public void requestFailed(Request request, Throwable ex);

	// ======================================================================
	// Evaluate URL
	// ======================================================================
	/**
	 * 指定された URL が処理対象かどうかを判断します。
	 * このメソッドはリクエストを実行する前に呼び出されます。
	 *
	 * @param url URL to evaluate
	 * @return true if application use specified URL in after crawling
	 */
	public boolean accept(URL url);

	// ======================================================================
	// Parse Content
	// ======================================================================
	/**
	 * レスポンス完了後に呼び出されます。
	 * 次回以降の
	 *
	 * @param session session
	 * @param request request
	 * @param response response
	 * @return URL iterator of extracted in response
	 * @throws CrawlerException if fail to parse content
	 */
	public Iterable<URL> parse(Session session, Request request, Response response) throws CrawlerException;

}
