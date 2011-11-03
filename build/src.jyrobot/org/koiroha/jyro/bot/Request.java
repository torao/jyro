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
import java.net.*;
import java.util.Map;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Request: リクエスト
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * リクエストを実行するためのクラスです。
 *
 * @version
 * @author torao
 * @since 2011/09/14 jyro 1.0
 */
public class Request extends Message{

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Request.class);

	// ======================================================================
	// Session
	// ======================================================================
	/**
	 * このリクエストのセッションです。
	 */
	private final Session session;

	// ======================================================================
	// Request URL
	// ======================================================================
	/**
	 * リクエスト URI です。
	 */
	private final URL url;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * リクエスト URL を指定して構築を行います。
	 *
	 * @param session session of this request
	 * @param url request URL
	 */
	public Request(Session session, URL url) {
		this.url = url;
		this.session = session;
		return;
	}

	// ======================================================================
	// Refer session
	// ======================================================================
	/**
	 * このリクエストのセッションを参照します。
	 *
	 * @return session session of this request
	 */
	public Session getSession() {
		return session;
	}

	// ======================================================================
	// Refer URL
	// ======================================================================
	/**
	 * リクエスト URL を参照します。
	 *
	 * @return request URL
	 */
	public URL getUrl() {
		return url;
	}

	// ======================================================================
	// Action GET Request
	// ======================================================================
	/**
	 * このインスタンスの設定を使用して GET リクエストを実行します。
	 *
	 * @return レスポンス
	 * @throws CrawlerException 接続に失敗した場合
	 */
	public Response get() throws CrawlerException {
		try {
			return execute("GET");
		} catch(IOException ex){
			throw new CrawlerException(ex);
		}
	}

	// ======================================================================
	// Literalize Instance
	// ======================================================================
	/**
	 * このインスタンスを文字列化します。
	 *
	 * @return string for this instance
	 */
	@Override
	public String toString() {
		return url.toString();
	}

	// ======================================================================
	// Action Request
	// ======================================================================
	/**
	 * このインスタンスの設定を使用してリクエストを実行します。
	 *
	 * @param method request method
	 * @return response object
	 * @throws IOException if fail to connect
	 */
	private Response execute(String method) throws IOException {

		// リダイレクト回数の最大値までリダイレクト処理を実行
		int maxRedirects = session.getJyrobot().getUserAgent().getMaxRedirects();
		URL url = this.url;
		for(int redirect = 0; redirect <= maxRedirects; redirect ++){

			// リクエストの実行
			URLConnection con = doRequest(url, method);

			// リダイレクト処理の実行
			if(con instanceof HttpURLConnection){
				HttpURLConnection hcon = (HttpURLConnection)con;
				int code = hcon.getResponseCode();
				if(code >= 300 && code < 400){
					String location = hcon.getHeaderField("Location");
					if(location != null){
						logger.debug("redirect response detected: [" + code + "] " + location);
						url = new URL(url, location);
						method = "GET";
						continue;
					}
				}
			}

			// レスポンスを作成して返す
			return new Response(this, con);
		}

		// リダイレクトの最大回数に達した場合
		throw new IOException("max redirect reached: " + maxRedirects);
	}

	// ======================================================================
	// Execute Request
	// ======================================================================
	/**
	 * このインスタンスの設定を使用してリクエストを実行します。
	 *
	 * @param url request URL
	 * @param method request method
	 * @return URL connection
	 * @throws IOException if fail to connect
	 */
	private URLConnection doRequest(URL url, String method) throws IOException {
		logger.debug(method + " " + url);
		URLConnection con = url.openConnection();

		// リクエストヘッダの設定
		for(Header h: header.getAll()){
			if(h.getName().equalsIgnoreCase("User-Agent")){
				con.setRequestProperty(h.getName(), h.getValue());
			} else {
				con.addRequestProperty(h.getName(), h.getValue());
			}
		}

		// 制御情報の設定
		UserAgent ua = session.getJyrobot().getUserAgent();
		con.setConnectTimeout((int)ua.getConnectionTimeout());
		con.setReadTimeout((int)ua.getReadTimeout());
		con.setAllowUserInteraction(false);
		con.setDefaultUseCaches(false);
		con.setDoInput(true);
		con.setDoOutput(! method.equalsIgnoreCase("GET"));

		// デフォルトのリクエストヘッダを設定
		for(Map.Entry<String,String> e: ua.getDefaultRequestHeader().entrySet()){
			con.setRequestProperty(e.getKey(), e.getValue());
		}

		// HTTP 制御情報の設定
		if(con instanceof HttpURLConnection){
			HttpURLConnection hcon = (HttpURLConnection)con;
			hcon.setInstanceFollowRedirects(false);
			hcon.setRequestMethod(method);
		}

		con.connect();
		return con;
	}

}
