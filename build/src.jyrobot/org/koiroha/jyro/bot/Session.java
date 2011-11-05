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

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Session: セッションインターフェース
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 1 クローリング単位を表すセッションのインターフェースです。
 *
 * @version
 * @author torao
 * @since 2011/11/05 jyro 1.0
 */
public interface Session {

	// ======================================================================
	// Refer User Agent
	// ======================================================================
	/**
	 * このセッションを実行しているユーザエージェントを参照します。
	 *
	 * @return user agent of this session
	 */
	public UserAgent getUserAgent();

	// ======================================================================
	// Refer Statistics
	// ======================================================================
	/**
	 * このセッションの統計情報を参照します。
	 *
	 * @return statistics of this session
	 */
	public Stat.Session getStat();

	// ======================================================================
	// Refer Session ID
	// ======================================================================
	/**
	 * このセッションの ID を参照します。
	 *
	 * @return id ID of this session
	 */
	public long getId();

	// ======================================================================
	// Poll Request
	// ======================================================================
	/**
	 * このセッションから次に処理を行うリクエストを参照します。
	 *
	 * @return request object
	 * @throws CrawlerException if fail to retrieve next request
	 */
	public Request poll() throws CrawlerException;

	// ======================================================================
	// Response Callback
	// ======================================================================
	/**
	 * このセッション上で実行したリクエストの実行結果をコールバックします。
	 *
	 * @param response result of request over this session
	 */
	public void callback(Response response);

	// ======================================================================
	// Close Session
	// ======================================================================
	/**
	 * このセッションをクローズしクローリングのために確保していたリソースを開放します。
	 *
	 * @throws CrawlerException if fail to close session
	 */
	public void close() throws CrawlerException;

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

		// ==================================================================
		// Refer domain
		// ==================================================================
		/**
		 * を参照します。
		 *
		 * @return domain
		 */
		public String getDomain() {
			return domain;
		}

		// ==================================================================
		// Set domain
		// ==================================================================
		/**
		 * を設定します。
		 *
		 * @param domain the domain to set
		 */
		public void setDomain(String domain) {
			this.domain = domain;
			return;
		}

		// ==================================================================
		// Refer path
		// ==================================================================
		/**
		 * を参照します。
		 *
		 * @return path
		 */
		public String getPath() {
			return path;
		}
		// ==================================================================
		// Set path
		// ==================================================================
		/**
		 * を設定します。
		 *
		 * @param path the path to set
		 */
		public void setPath(String path) {
			this.path = path;
			return;
		}
		// ==================================================================
		// Set expires
		// ==================================================================
		/**
		 * を設定します。
		 *
		 * @param expires the expires to set
		 */
		public void setExpires(Date expires) {
			this.expires = expires;
			return;
		}
		// ==================================================================
		// Refer expires
		// ==================================================================
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

		// ==================================================================
		// Refer httpOnly
		// ==================================================================
		/**
		 * を参照します。
		 *
		 * @return httpOnly
		 */
		public boolean isHttpOnly() {
			return httpOnly;
		}
		// ==================================================================
		// Set httpOnly
		// ==================================================================
		/**
		 * を設定します。
		 *
		 * @param httpOnly the httpOnly to set
		 */
		public void setHttpOnly(boolean httpOnly) {
			this.httpOnly = httpOnly;
			return;
		}
		// ==================================================================
		// Refer secure
		// ==================================================================
		/**
		 * を参照します。
		 *
		 * @return secure
		 */
		public boolean isSecure() {
			return secure;
		}
		// ==================================================================
		// Set secure
		// ==================================================================
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
			if(path == null || path.length() == 0){
				path = "/";
			}
			if(! this.path.equals(path) && ! path.startsWith(this.path)){
				// TODO 厳密な判定が必要
				return false;
			}

			// Secure 判定
			if(this.isSecure() && !url.getProtocol().equalsIgnoreCase("https")){
				return false;
			}

			// 有効期限切れ判定
			if(this.isExpired(date)){
				return false;
			}

			return true;
		}

	}

}