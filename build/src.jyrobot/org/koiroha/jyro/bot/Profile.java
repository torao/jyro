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

import java.util.*;

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
public class Profile {

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
	 * 親プロフィールを設定して構築を行います。
	 *
	 * @param parent 親のプロフィール
	 */
	public Profile(Profile parent) {
		if(parent != null){
			synchronized(parent){
				this.config.putAll(parent.config);
				this.header.addAll(parent.header);
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
	 * レスポンスとして取得する内容の最大長を参照します。
	 *
	 * @return max content length to retrieve from response in bytes
	 */
	public int getRetrievalLimitLength() {
		return getConfig("retrievalLimitLength", 0);
	}

	// ======================================================================
	// Set Max Content Length
	// ======================================================================
	/**
	 * レスポンスとして取得する内容の最大長を設定します。
	 *
	 * @param retrievalLimitLength max content length to retrieve from response in bytes
	 */
	public void setRetrievalLimitLength(int retrievalLimitLength) {
		setConfig("retrievalLimitLength", retrievalLimitLength);
		return;
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
