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
// UserAgent: ユーザエージェント
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * ユーザエージェントを表すクラスです。
 *
 * @version
 * @author torao
 * @since 2011/09/16 jyro 1.0
 */
public class UserAgent {

	// ======================================================================
	// User-Agent Configuration
	// ======================================================================
	/**
	 * このユーザエージェント設定です。
	 */
	private final Config config;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * このユーザエージェントの設定を指定して構築を行います。
	 *
	 * @param config profile of this user-agent
	 */
	public UserAgent(Config config){
		this.config = config;
		return;
	}

	// ======================================================================
	// Refer Connection Timeout
	// ======================================================================
	/**
	 * 接続タイムアウトを参照します。
	 *
	 * @return connection timeout in milliseconds
	 */
	public long getConnectionTimeout(){
		return config.getLong("connection_timeout");
	}

	// ======================================================================
	// Refer Read Timeout
	// ======================================================================
	/**
	 * 読み込みタイムアウトを参照します。
	 *
	 * @return read timeout in milliseconds
	 */
	public long getReadTimeout(){
		return config.getLong("read_timeout");
	}

	// ======================================================================
	// Refer Max Redirect
	// ======================================================================
	/**
	 * リダイレクト回数の最大値を参照します。
	 *
	 * @return redirect limit
	 */
	public int getMaxRedirects(){
		return config.getInt("max_redirects");
	}

	// ======================================================================
	// Refer Max Content Length
	// ======================================================================
	/**
	 * レスポンスの読み込み料を参照します。
	 *
	 * @return read limit in bytes
	 */
	public int getMaxContentLength(){
		return config.getInt("max_content_length");
	}

	// ======================================================================
	// Refer Default Header Settings
	// ======================================================================
	/**
	 * リクエストにデフォルトで付加するヘッダを参照します。
	 *
	 * @return map of default request header
	 */
	public Map<String,String> getDefaultRequestHeader(){
		Map<String,String> header = new HashMap<String,String>();
		if(config.getMap("header") != null){
			for(Map.Entry<String,Object> e: config.getMap("header").entrySet()){
				header.put(e.getKey(), e.getValue().toString());
			}
		}
		return header;
	}

}
