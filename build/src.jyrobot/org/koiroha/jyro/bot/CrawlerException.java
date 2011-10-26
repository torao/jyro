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
// CrawlerException:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version
 * @author torao
 * @since 2011/10/21 jyro 1.0
 */
public class CrawlerException extends Exception {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 *
	 */
	public CrawlerException() {
		// TODO Auto-generated constructor stub
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 *
	 * @param message
	 */
	public CrawlerException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 *
	 * @param cause
	 */
	public CrawlerException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 *
	 * @param message
	 * @param cause
	 */
	public CrawlerException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
