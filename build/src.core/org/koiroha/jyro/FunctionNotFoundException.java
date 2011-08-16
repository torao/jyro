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
// FunctionNotFoundException:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/16 Java SE 6
 */
public class FunctionNotFoundException extends JyroException {

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
	public FunctionNotFoundException() {
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
	public FunctionNotFoundException(String msg) {
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
	public FunctionNotFoundException(Throwable ex) {
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
	public FunctionNotFoundException(String msg, Throwable ex) {
		super(msg, ex);
		return;
	}

}
