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
	// Retrieve Worker Interface
	// ======================================================================
	/**
	 * Retrieve worker interface to call.
	 *
	 * @param worker worker interface
	 * @return callable worker instance
	 * @throws WorkerException if fail to refer worker interface
	*/
	public void call(String func, Object... args) throws JyroException;

}
