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

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// SessionFactory: セッションファクトリ
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * セッションを作成するためのファクトリクラスです。
 *
 * @version
 * @author torao
 * @since 2011/09/28 jyro 1.0
 */
public interface SessionFactory {

	// ======================================================================
	// Create New Session
	// ======================================================================
	/**
	 * このファクトリを使用して新しいセッションを作成します。
	 *
	 * @param userAgent user agent
	 * @return new session
	 */
	public Session createSession(Profile profile);

}
