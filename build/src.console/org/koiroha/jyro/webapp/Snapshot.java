/* **************************************************************************
 * Copyright (C) 2008 BJoRFUAN. All Right Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * BSD License, and comes with NO WARRANTY.
 *
 *                                                 torao <torao@bjorfuan.com>
 *                                                       http://www.moyo.biz/
 * $Id:$
*/
package org.koiroha.jyro.webapp;

import java.io.Serializable;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Snapshot:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/04 Java SE 6
 */
public final class Snapshot implements Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	//
	// ======================================================================
	/**
	 * Server name.
	 */
	private final String server;

	// ======================================================================
	//
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 * <p>
	 */
	public Snapshot() {
		return;
	}

}
