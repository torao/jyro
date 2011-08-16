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
// WorkerContext: Worker Context
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Runtime context interface for worker implementations.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/23 Java SE 6
 */
public interface WorkerContext {

	// ======================================================================
	// Call Procedure
	// ======================================================================
	/**
	 * Request to call specified function on cluster anywhere.
	 * In this method, there is no way to retrieve function result.
	 *
	 * @param func function name to call
	 * @param args arguments
	 * @throws JyroException fail to request call
	*/
	public void call(String func, Object... args) throws JyroException;

	// TODO add method that returns result waiting object.

}
