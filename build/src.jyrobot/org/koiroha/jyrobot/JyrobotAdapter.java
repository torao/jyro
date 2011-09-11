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

import java.net.URI;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyrobotAdapter: Jyrobot アダプタ
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/09/03 Java SE 6
 */
public interface JyrobotAdapter {

	// ======================================================================
	// URL Filtering
	// ======================================================================
	/**
	 * 指定された URL を次回以降のクローリングに使用するかを判定します。
	*/
	public boolean accept(Content referer, URI uri);

	// ======================================================================
	// Notify Success
	// ======================================================================
	/**
	 * 指定されたリクエストに成功したときに呼び出されます。
	 *
	 * @param request 成功したリクエスト
	 * @param content 取得した内容
	 * @return 処理を続行する場合 true
	*/
	public boolean success(Session.Request request, Content content);

	// ======================================================================
	// Notify Error
	// ======================================================================
	/**
	 * 例外の発生により処理が行えなかった場合に呼び出されます。
	 *
	 * @param request 失敗したリクエスト
	 * @param ex 発生した例外
	 * @return 処理を続行する場合 true
	*/
	public boolean failure(Session.Request request, Throwable ex);

}
