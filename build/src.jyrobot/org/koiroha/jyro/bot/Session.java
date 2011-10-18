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
	// Profile of this session
	// ======================================================================
	/**
	 * このセッションのプロフィールです。
	 */
	private final Profile profile;

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
	 * このセッションが使用するプロフィールを指定して構築を行います。
	 *
	 * @param profile profile of this session
	 */
	public Session(Profile profile) {
		this.profile = new Profile(profile);
		return;
	}

	// ======================================================================
	// Create Request
	// ======================================================================
	/**
	 * このセッションを使用して指定された URL に対するリクエストを作成します。
	 *
	 * @param url URL for request
	 * @return request object
	 * @throws IOException if specified URL is invalid
	 */
	public Request newRequest(URL url) throws IOException{
		Request request = new Request(profile, url);

		return request;
	}

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
