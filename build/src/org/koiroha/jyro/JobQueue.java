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
// JobQueue: Job Queue
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/06 Java SE 6
 */
public interface JobQueue {

	// ======================================================================
	// Post Job
	// ======================================================================
	/**
	 * Post specified job to this queue.
	*/
	public void send(Job job) throws JyroException;

	// ======================================================================
	// Receive Job
	// ======================================================================
	/**
	 * Receive job from this queue.
	*/
	public Job receive() throws JyroException;

	// ======================================================================
	// Close Queue
	// ======================================================================
	/**
	 * Close queue messaging and release resources.
	 *
	 * @throws JyroException if fail to close queue
	*/
	public void close() throws JyroException;

}
