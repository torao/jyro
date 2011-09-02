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

import java.util.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Config: 設定クラス
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 設定内容を保持するためのクラスです。
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/28 Java SE 6
 */
final class Config {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private final Properties conf = new Properties();

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Config.class);

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * 構築直後はリソースにバンドルされているデフォルトのプロパティで初期化さ
	 * れています。
	 */
	public Config() {
		ResourceBundle res = ResourceBundle.getBundle("org.koiroha.jyrobot.jyrobot");
		Enumeration<String> en = res.getKeys();
		while(en.hasMoreElements()){
			String key = en.nextElement();
			String value = res.getString(key);
			conf.put(key, value);
		}
		return;
	}

	// ======================================================================
	// プロパティ値の参照
	// ======================================================================
	/**
	 * 指定された名前に対するプロパティ値を参照します。名前に該当するプロパ
	 * ティが定義されていない場合は指定されたデフォルト値を返します。
	 *
	 * @param name プロパティ名
	 * @param def デフォルト値
	 * @return プロパティ値
	 */
	public String getString(String name, String def){
		return conf.getProperty(name, def);
	}

	// ======================================================================
	// プロパティ値の参照
	// ======================================================================
	/**
	 * 指定された名前に対するプロパティ値を参照します。名前に該当するプロパ
	 * ティが定義されていない場合は指定されたデフォルト値を返します。
	 *
	 * @param name プロパティ名
	 * @param def デフォルト値
	 * @return プロパティ値
	 */
	public int getInt(String name, int def){
		String value = getString(name, String.valueOf(def));
		try {
			return Integer.parseInt(value.trim());
		} catch(NumberFormatException ex){
			logger.warn(name + " is not a numeric value: " + value);
		}
		return def;
	}

}
