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

import java.io.IOException;
import java.net.URL;

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
public interface Session {

	// ======================================================================
	// Refer Session ID
	// ======================================================================
	/**
	 * このセッションの ID を参照します。
	 *
	 * @return ID of this session
	 */
	public long getId();

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
	public Request newRequest(URL url) throws IOException;

}
