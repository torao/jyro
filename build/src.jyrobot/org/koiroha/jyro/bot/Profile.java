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

import org.koiroha.jyro.bot.Session.Cookie;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Profile: User-Agent プロフィール
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * User-Agent の初期設定を表すプロフィールです。
 *
 * @version
 * @author torao
 * @since 2011/09/16 jyro 1.0
 */
public class Profile implements Serializable{

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Configuration
	// ======================================================================
	/**
	 * このプロフィールの設定内容です。
	 */
	private final Map<String,Object> config = new HashMap<String,Object>();

	// ======================================================================
	// Header
	// ======================================================================
	/**
	 * リクエストに使用するヘッダです。
	 */
	private final List<Message.Header> header = new ArrayList<Message.Header>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * 親を持たないプロフィールを構築します。
	 */
	public Profile() {
		this(null);
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * デフォルトのプロフィールを指定して構築を行います。
	 * このコンストラクタは {@code def} で指定したインスタンスの設定値をコピーするだけです。
	 * {@code def} に対する設定変更はこのインスタンスに影響しません。
	 *
	 * @param def default profile
	 */
	public Profile(Profile def) {
		if(def != null){
			synchronized(def){
				this.config.putAll(def.config);
				this.header.addAll(def.header);
			}
		}
		return;
	}

	// ======================================================================
	// Refer Connection Timeout
	// ======================================================================
	/**
	 * デフォルトの接続タイムアウトを参照します。
	 *
	 * @return TCP/IP connection timeout in milliseconds
	 */
	public long getConnectionTimeout(){
		return getConfig("connectionTimeout", 30 * 1000);
	}

	// ======================================================================
	// Set Connection Timeout
	// ======================================================================
	/**
	 * デフォルトの接続タイムアウトを設定します。
	 *
	 * @param connectionTimeout connection timeout in milliseconds
	 */
	public void setConnectionTimeout(long connectionTimeout){
		setConfig("connectionTimeout", connectionTimeout);
		return;
	}

	// ======================================================================
	// Refer Read Timeout
	// ======================================================================
	/**
	 * デフォルトの読み込みタイムアウトを参照します。
	 *
	 * @return read timeout in milliseconds
	 */
	public long getReadTimeout(){
		return getConfig("readTimeout", 60 * 1000);
	}

	// ======================================================================
	// Set Read Timeout
	// ======================================================================
	/**
	 * デフォルトの読み込みタイムアウトを設定します。
	 *
	 * @param readTimeout read timeout in milliseconds
	 */
	public void setReadTimeout(long readTimeout){
		setConfig("readTimeout", readTimeout);
		return;
	}

	// ======================================================================
	// Refer Max Redirects
	// ======================================================================
	/**
	 * リクエスト実行時にリダイレクトレスポンスを受けた場合、内部で自動的にリダイレクトを行う
	 * 最大回数を参照します。この回数以上のリダイレクトが行われた場合、get または post
	 * リクエストで 301, 302 レスポンスを返します。
	 * デフォルトでは 0 (自動リダイレクトなし) が設定されています。
	 *
	 * @return limit count for redirect
	 */
	public int getMaxRedirects() {
		return getConfig("maxRedirects", 0);
	}

	// ======================================================================
	// Set Max Redirects
	// ======================================================================
	/**
	 * リダイレクトレスポンスを受けた場合の自動リダイレクト回数を設定します。
	 *
	 * @param maxRedirects limit count for redirect
	 */
	public void setMaxRedirects(int maxRedirects) {
		setConfig("maxRedirects", maxRedirects);
		return;
	}

	// ======================================================================
	// Refer Max Content Length
	// ======================================================================
	/**
	 * 指定された Content-Type のレスポンスから取得する内容の最大長を参照します。
	 *
	 * @param contentType MIME-Type or null if not defined
	 * @return max content length to retrieve from response in bytes
	 */
	public int getRetrievalLimitLength(String contentType) {
		String name = combine("retrievalLimitLength", contentType);
		return getConfig(name, 0);
	}

	// ======================================================================
	// Set Max Content Length
	// ======================================================================
	/**
	 * 指定された Content-Type のレスポンスから取得する内容の最大長を設定します。
	 *
	 * @param contentType MIME-Type or null if not defined
	 * @param retrievalLimitLength max content length to retrieve from response in bytes
	 */
	public void setRetrievalLimitLength(String contentType, int retrievalLimitLength) {
		String name = combine("retrievalLimitLength", contentType);
		setConfig(name, retrievalLimitLength);
		return;
	}

	// ======================================================================
	// Create New Session
	// ======================================================================
	/**
	 * このプロフィールの設定から新しいセッションを構築します。
	 *
	 * @return new session
	 */
	public Session createSession(){
		return null;
	}

	// ======================================================================
	// Load Persistent Cookie
	// ======================================================================
	/**
	 * 指定された URL に対する永続 Cookie を参照します。
	 *
	 * @param url access URL
	 * @return session cookies for specified url
	 * @throws IOException fail to access backend strage
	 */
	public Iterable<Cookie> loadPersistentCookies(URL url) throws IOException{
		return Collections.emptyList();
	}

	// ======================================================================
	// Store Persistent Cookie
	// ======================================================================
	/**
	 * 指定された URL に対する永続 Cookie を保存します。
	 *
	 * @param url access URL
	 * @return session cookies for specified url
	 * @throws IOException fail to access backend strage
	 */
	public void storePersistentCookies(URL url, Iterable<Cookie> cookies) throws IOException{
		return;
	}

	// ======================================================================
	// Combine Configuration Name
	// ======================================================================
	/**
	 * オプション付きの設定名を参照します。
	 *
	 * @param base base name of configuration
	 * @param option option value or null if default
	 * @return combined string
	 */
	private static String combine(String base, String option) {
		if(option == null){
			return base;
		}
		return base + "." + option.toLowerCase();
	}

	// ======================================================================
	// Set Configuration
	// ======================================================================
	/**
	 * 指定された設定を追加します。
	 *
	 * @param name 設定名
	 * @param value 設定値
	 */
	private void setConfig(String name, Object value){
		synchronized(this){
			config.put(name, value);
		}
		return;
	}

	// ======================================================================
	// Refer Configuration
	// ======================================================================
	/**
	 * 指定された名前の設定値を参照します。
	 *
	 * @param name preference name
	 * @param def default value
	 * @return preference value
	 * @param <T> type of preference value
	 */
	private <T> T getConfig(String name, T def){
		synchronized(this){
			Object value = config.get(name);
			if(value == null){
				return def;
			}
			@SuppressWarnings("unchecked")
			T t = (T)value;
			return t;
		}
	}

}
