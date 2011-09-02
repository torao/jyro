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
import java.sql.*;

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
	// コンストラクタ
	// ======================================================================
	/**
	 * コンストラクタはクラス内に隠蔽されています。
	 */
	private Util() {
		return;
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

}
