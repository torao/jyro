/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroException:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
public class JyroException extends Exception {

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
	public JyroException() {
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
	public JyroException(String msg) {
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
	public JyroException(Throwable ex) {
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
	public JyroException(String msg, Throwable ex) {
		super(msg, ex);
		return;
	}

}
