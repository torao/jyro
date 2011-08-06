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
package org.koiroha.jyro.util;




// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// ParseException:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/17 Java SE 6
 */
public class ParseException extends Exception {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 * <p>
	 */
	public ParseException() {
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 * <p>
	 * @param msg exception message
	 */
	public ParseException(String msg) {
		super(msg);
		// TODO 自動生成されたコンストラクター・スタブ
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 * <p>
	 * @param ex cause exception
	 */
	public ParseException(Throwable ex) {
		super(ex);
		// TODO 自動生成されたコンストラクター・スタブ
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 * <p>
	 * @param msg exception message
	 * @param ex cause exception
	 */
	public ParseException(String msg, Throwable ex) {
		super(msg, ex);
		return;
	}

}
