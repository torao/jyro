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
	// Post Job
	// ======================================================================
	/**
	 * Post specified job to other node.
	 *
	 * @param nodeId node ID to post job
	 * @param job job to send
	 * @throws JyroException jyro
	*/
	public void send(String nodeId, Job job) throws JyroException;

}
