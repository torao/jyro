/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyrobot;

import java.io.*;
import java.lang.management.*;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Util: ユーティリティクラス
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * パッケージ内のユーティリティクラスです。
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/28 Java SE 6
 */
final class Util {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Util.class);

	// ======================================================================
	// Default Port
	// ======================================================================
	/**
	 * URL スキームに対するデフォルトポートです。
	 */
	private static final Map<String,Integer> DEFAULT_PORT = new HashMap<String,Integer>();

	// ======================================================================
	// Hyper-Text Reference XPath
	// ======================================================================
	/**
	 * HTML 上で別のコンテンツへのリンクを抽出するための XPath です。
	 */
	public static final Set<String> HREF_XPATH;

	// ======================================================================
	// Hyper-Text Reference XPath
	// ======================================================================
	/**
	 * HTML 上で別のコンテンツへのリンクを抽出するための XPath です。
	 */
	private static final RuntimeMXBean RUNTIME = ManagementFactory.getRuntimeMXBean();

	// ======================================================================
	// デフォルトポート
	// ======================================================================
	/**
	 * URL スキームに対するデフォルトポートです。
	 */
	static {
		ResourceBundle res = ResourceBundle.getBundle("org.koiroha.jyrobot.jyrobot");

		// デフォルトポートの参照
		final String defPortPrefix = "defaultPort.";
		Enumeration<String> en = res.getKeys();
		while(en.hasMoreElements()){
			String key = en.nextElement();
			if(key.startsWith(defPortPrefix)){
				String value = res.getString(key);
				DEFAULT_PORT.put(key.substring(defPortPrefix.length()), Integer.parseInt(value));
			}
		}

		// XPath リストを参照
		String[] xpath = res.getString("href.xpath").split("\\s*,\\s");
		HREF_XPATH = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(xpath)));
	}

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * コンストラクタはクラス内に隠蔽されています。
	 */
	private Util() {
		return;
	}

	// ======================================================================
	// デフォルトポートの参照
	// ======================================================================
	/**
	 * URL スキームに対するデフォルトのポートを参照します。
	 *
	 * @param scheme URL スキーム
	 * @return デフォルトポート
	 */
	public static int getDefaultPort(String scheme){
		scheme = scheme.toLowerCase();
		Integer port = DEFAULT_PORT.get(scheme);
		if(port == null){
			return -1;
		}
		return port;
	}

	// ======================================================================
	// デフォルトポートの判定
	// ======================================================================
	/**
	 * 指定されたポート番号が URL スキームに対するデフォルトのポートかを判定
	 * します。
	 *
	 * @param scheme URL スキーム
	 * @param port ポート
	 */
	public static boolean isDefaultPort(String scheme, int port){
		scheme = scheme.toLowerCase();
		Integer p = DEFAULT_PORT.get(scheme);
		if(p == null){
			return false;
		}
		return p.intValue() == port;
	}

	// ======================================================================
	// オブジェクトのクローズ
	// ======================================================================
	/**
	 * 入出力ストリームやデータベースリソースを例外なしでクローズするための
	 * ユーティリティ機能です。
	 *
	 * @param obj クローズするオブジェクト
	 */
	public static void close(Object... objs){
		for(Object obj: objs){
			if(obj == null){
				continue;
			}

			// 入出力ストリームの場合
			if(obj instanceof Closeable){
				try {
					((Closeable)obj).close();
				} catch(IOException ex){
					logger.warn("fail to close object: " + obj, ex);
				}
				continue;
			}

			if(obj instanceof Connection){
				try {
					((Connection)obj).close();
				} catch(SQLException ex){
					logger.warn("fail to close connection: " + obj, ex);
				}
				continue;
			}

			if(obj instanceof Statement){
				try {
					((Statement)obj).close();
				} catch(SQLException ex){
					logger.warn("fail to close statement: " + obj, ex);
				}
				continue;
			}

			if(obj instanceof ResultSet){
				try {
					((ResultSet)obj).close();
				} catch(SQLException ex){
					logger.warn("fail to close result set: " + obj, ex);
				}
				continue;
			}

			throw new IllegalStateException("unsupported closeable object: " + obj);
		}
		return;
	}

	// ======================================================================
	// 起動時間の参照
	// ======================================================================
	/**
	 * 起動時間をミリ秒単位で参照します。
	 *
	 * @return 起動時間
	 */
	public static long getUptime(){
		return RUNTIME.getUptime();
	}
}
