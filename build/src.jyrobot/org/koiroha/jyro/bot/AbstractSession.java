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
import java.util.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Session: セッションインターフェース
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 1クローリング単位を表すセッションのインターフェースです。
 *
 * @version
 * @author torao
 * @since 2011/09/14 jyro 1.0
 */
public abstract class AbstractSession implements Session {

	// ======================================================================
	// Jyrobot
	// ======================================================================
	/**
	 * このセッションのアプリケーションです。
	 */
	protected final Jyrobot jyrobot;

	// ======================================================================
	// Session Statistics
	// ======================================================================
	/**
	 * このセッションの実行統計です。
	 */
	private final Stat.Session stat = new Stat.Session();

	// ======================================================================
	// Session ID
	// ======================================================================
	/**
	 * このセッションの ID です。
	 */
	private final long id;

	// ======================================================================
	// Session Cookies
	// ======================================================================
	/**
	 * このセッションが保持している Cookie です。
	 */
	private final Collection<Cookie> cookies = new ArrayList<Cookie>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * このセッションの ID を指定して構築を行います。
	 *
	 * @param jyrobot application instance
	 * @param id ID of this session
	 */
	protected AbstractSession(Jyrobot jyrobot, long id) {
		this.id = id;
		this.jyrobot = jyrobot;
		return;
	}

	// ======================================================================
	// Refer User Agent
	// ======================================================================
	/**
	 * このセッションを実行しているユーザエージェントを参照します。
	 *
	 * @return user agent of this session
	 */
	@Override
	public UserAgent getUserAgent(){
		return jyrobot.getUserAgent();
	}

	// ======================================================================
	// Refer Statistics
	// ======================================================================
	/**
	 * このセッションの統計情報を参照します。
	 *
	 * @return statistics of this session
	 */
	@Override
	public Stat.Session getStat(){
		return stat;
	}

	// ======================================================================
	// Refer ID
	// ======================================================================
	/**
	 * このセッションの ID を参照します。
	 *
	 * @return session ID
	 */
	@Override
	public long getId() {
		return id;
	}

	// ======================================================================
	// Poll Request
	// ======================================================================
	/**
	 * このセッションから次のリクエストを参照します。
	 *
	 * @return next request, or null if no more requests on this session
	 * @throws CrawlerException if fail to retrieve request
	 */
	@Override
	public Request poll() throws CrawlerException {

		// 次に処理を行う URL を参照
		URL url = pollURL();
		if(url == null){
			return null;
		}

		// リクエストを構築して返す
		Request request = new Request(this, url);
		getStat().increaseRequests(1);
		return request;
	}

	// ======================================================================
	// Response Callback
	// ======================================================================
	/**
	 * このセッション上で実行したリクエストの実行結果をコールバックします。
	 *
	 * @param response result of request over this session
	 */
	@Override
	public void callback(Response response){
		return;
	}

	// ======================================================================
	// Close Session
	// ======================================================================
	/**
	 * セッションをクローズします。
	 * @throws CrawlerException if fail to close session
	 */
	@Override
	public void close() throws CrawlerException{
		return;
	}

	// ======================================================================
	// Poll Next URL
	// ======================================================================
	/**
	 * このセッション上で次にリクエストすべき URL を参照します。これ以上リクエスト対象の URL が存在しない
	 * 場合は null を返します。
	 *
	 * @return URL to request over this session
	 * @throws CrawlerException fail if retrieve request url
	 */
	protected abstract URL pollURL() throws CrawlerException;

	// ======================================================================
	// Retrieve Cookies
	// ======================================================================
	/**
	 * 指定された URL へリクエストを送るための Cookie を参照します。
	 *
	 * @param url URL for request
	 * @return request object
	 * @throws IOException if specified URL is invalid
	 */
	protected Iterable<Cookie> retrieve(URL url) throws IOException {
		List<Cookie> c = new ArrayList<AbstractSession.Cookie>();
		Date now = new Date();
		for(Cookie cookie: cookies){
			if(cookie.shouldSend(url, now)){
				c.add(cookie);
			}
		}
		return c;
	}

}
