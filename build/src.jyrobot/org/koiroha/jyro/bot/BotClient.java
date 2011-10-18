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

import java.io.IOException;
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
	// Prepare Request
	// ======================================================================
	/**
	 * リクエストの準備のために呼び出されます。
	 * リクエストに対してヘッダの追加を行うことができます。
	 *
	 * @param request リクエスト
	 */
	public void prepare(Request request);

	// ======================================================================
	// Parse Content
	// ======================================================================
	/**
	 * レスポンス完了後に呼び出されます。
	 * 次回以降の
	 *
	 * @param request リクエスト
	 * @param response レスポンス
	 * @return URL iterator of extracted in response
	 * @throws IOException
	 */
	public Iterable<URL> parse(Request request, Response response) throws IOException;

	// ======================================================================
	// Notify Failure
	// ======================================================================
	/**
	 * リクエストで例外が発生したときに呼び出されます。
	 *
	 * @param request object that fail to request
	 * @param ex exception
	 */
	public void failure(Request request, Throwable ex);

}
