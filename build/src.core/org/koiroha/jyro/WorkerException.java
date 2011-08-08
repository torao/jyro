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
package org.koiroha.jyro;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// WorkerException:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
public class WorkerException extends JyroException {

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
	 * Create exception with no message.
	 */
	public WorkerException() {
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Create exception with specified message.
	 *
	 * @param msg exception message
	 */
	public WorkerException(String msg) {
		super(msg);
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Create exception with parent cause exception.
	 *
	 * @param ex cause exception
	 */
	public WorkerException(Throwable ex) {
		super(ex);
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Create exception with specified message and cause exception.
	 *
	 * @param msg exception message
	 * @param ex cause exception
	 */
	public WorkerException(String msg, Throwable ex) {
		super(msg, ex);
		return;
	}

}
