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

import java.io.*;
import java.net.URL;
import java.util.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Session: セッションインターフェース
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * クローリングのセッションを表すインターフェースです。
 *
 * @version
 * @author torao
 * @since 2011/09/14 jyro 1.0
 */
public abstract class Session implements Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Jyrobot
	// ======================================================================
	/**
	 * このセッションのアプリケーションです。
	 */
	private final Jyrobot jyrobot;

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
	// Request Count
	// ======================================================================
	/**
	 * このセッション上で処理されたリクエスト数です。
	 */
	private volatile int totalRequests = 0;

	// ======================================================================
	// Total Retrieval Size
	// ======================================================================
	/**
	 * このセッション上でダウンロードされたデータ量です。
	 */
	private volatile long totalRetrieval = 0;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * このセッションの ID を指定して構築を行います。
	 *
	 * @param jyrobot application instance
	 * @param id ID of this session
	 */
	protected Session(Jyrobot jyrobot, long id) {
		this.id = id;
		this.jyrobot = jyrobot;
		return;
	}

	// ======================================================================
	// Refer Application
	// ======================================================================
	/**
	 * アプリケーションを参照します。
	 *
	 * @return jyrobot
	 */
	public Jyrobot getJyrobot() {
		return jyrobot;
	}

	// ======================================================================
	// Refer ID
	// ======================================================================
	/**
	 * このセッションの ID を参照します。
	 *
	 * @return id ID of this session
	 */
	public long getId() {
		return id;
	}

	// ======================================================================
	// Refer Total Requests
	// ======================================================================
	/**
	 * このセッション上で処理を行ったリクエスト数を参照します。
	 *
	 * @return requests count over this session
	 */
	public int getTotalRequests(){
		return totalRequests;
	}

	// ======================================================================
	// Refer Total Retrieval Size
	// ======================================================================
	/**
	 * このセッション上でダウンロードされたデータ量を参照します。
	 *
	 * @return total retrieval byte-size over this session
	 */
	public long getTotalRetrieval() {
		return totalRetrieval;
	}

	// ======================================================================
	// Poll Request
	// ======================================================================
	/**
	 * このセッションから次に処理を行うリクエストを参照します。
	 *
	 * @return request object
	 * @throws CrawlerException if fail to retrieve next request
	 */
	public Request poll() throws CrawlerException {

		// 次に処理を行う URL を参照
		URL url = pollURL();
		if(url == null){
			return null;
		}

		// リクエストを構築して返す
		Request request = new Request(this, url);
		totalRequests ++;
		return request;
	}

	// ======================================================================
	// Close Session
	// ======================================================================
	/**
	 * このセッションをクローズします。
	 *
	 * @throws CrawlerException セッションのクローズに失敗した場合
	 */
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
		List<Cookie> c = new ArrayList<Session.Cookie>();
		Date now = new Date();
		for(Cookie cookie: cookies){
			if(cookie.shouldSend(url, now)){
				c.add(cookie);
			}
		}
		return c;
	}

	// ======================================================================
	// Increase Total Retrieval Size
	// ======================================================================
	/**
	 * このセッション上で取得したデータ量を加算します。
	 *
	 * @param size increase amount in bytes
	 */
	void increaseTotalRetrieval(long size){
		totalRetrieval += size;
		return;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Cookie: HTTP Cookie
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * HTTP Cookie を表すクラスです。
	 */
	public static class Cookie implements Serializable {
		/** Serial version of this class. */
		private static final long serialVersionUID = 1L;

		/** The name of this cookie. */
		private final String name;
		/** The value of this cookie. */
		private final String value;
		/** domain of this cookie. */
		private String domain = null;
		/** path of this cookie. */
		private String path = null;
		/** Expires timestamp */
		private Date expires = null;
		/** http-only flag. */
		private boolean httpOnly = false;
		/** secure flag. */
		private boolean secure = false;

		/**
		 * Construct cookie instance with specified properties.
		 * @param name name of this cookie
		 * @param value value of this cookie
		 */
		public Cookie(String name, String value){
			this.name = name;
			this.value = value;
			return;
		}

		/**
		 * Retrieve name of this cookie.
		 * @return cookie name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Retrieve value of this cookie.
		 * @return cookie value
		 */
		public String getValue() {
			return value;
		}

		// ======================================================================
		// Refer domain
		// ======================================================================
		/**
		 * を参照します。
		 *
		 * @return domain
		 */
		public String getDomain() {
			return domain;
		}
		// ======================================================================
		// Set domain
		// ======================================================================
		/**
		 * を設定します。
		 *
		 * @param domain the domain to set
		 */
		public void setDomain(String domain) {
			this.domain = domain;
			return;
		}
		// ======================================================================
		// Refer path
		// ======================================================================
		/**
		 * を参照します。
		 *
		 * @return path
		 */
		public String getPath() {
			return path;
		}
		// ======================================================================
		// Set path
		// ======================================================================
		/**
		 * を設定します。
		 *
		 * @param path the path to set
		 */
		public void setPath(String path) {
			this.path = path;
			return;
		}
		// ======================================================================
		// Set expires
		// ======================================================================
		/**
		 * を設定します。
		 *
		 * @param expires the expires to set
		 */
		public void setExpires(Date expires) {
			this.expires = expires;
			return;
		}
		// ======================================================================
		// Refer expires
		// ======================================================================
		/**
		 * を参照します。
		 *
		 * @return expires
		 */
		public Date getExpires() {
			return expires;
		}

		/**
		 * Evaluate whether this cookie is expired or not in now.
		 * @return true if this cookie is expired
		 */
		public boolean isExpired() {
			return isExpired(new Date());
		}

		/**
		 * Evaluate whether this cookie is expired or not in specified date.
		 * @param date date to evaluate expiration
		 * @return true if this cookie is expired
		 */
		public boolean isExpired(Date date) {
			return expires != null && date.after(expires);
		}

		// ======================================================================
		// Refer httpOnly
		// ======================================================================
		/**
		 * を参照します。
		 *
		 * @return httpOnly
		 */
		public boolean isHttpOnly() {
			return httpOnly;
		}
		// ======================================================================
		// Set httpOnly
		// ======================================================================
		/**
		 * を設定します。
		 *
		 * @param httpOnly the httpOnly to set
		 */
		public void setHttpOnly(boolean httpOnly) {
			this.httpOnly = httpOnly;
			return;
		}
		// ======================================================================
		// Refer secure
		// ======================================================================
		/**
		 * を参照します。
		 *
		 * @return secure
		 */
		public boolean isSecure() {
			return secure;
		}
		// ======================================================================
		// Set secure
		// ======================================================================
		/**
		 * を設定します。
		 *
		 * @param secure the secure to set
		 */
		public void setSecure(boolean secure) {
			this.secure = secure;
			return;
		}

		// ==================================================================
		// Evaluate Cookie
		// ==================================================================
		/**
		 * 指定された URL と日時に対するリクエストにこの Cookie を送るべきかを判定します。
		 *
		 * @param url url to evaluate
		 * @param date date
		 * @return true if this cookie should send
		 */
		public boolean shouldSend(URL url, Date date){

			// ドメインの比較
			String host = url.getHost().toLowerCase();
			String domain = getDomain().toLowerCase();
			if(domain.startsWith(".")){
				if(! host.endsWith(domain)){
					return false;
				}
			} else {
				if(! host.equals(domain)){
					return false;
				}
			}

			// パスの比較
			String path = url.getPath();

			if(this.isSecure() && !url.getProtocol().equalsIgnoreCase("https")){
				return false;
			}
			if(this.isExpired(date)){
				return false;
			}
			return true;
		}

	}

}
