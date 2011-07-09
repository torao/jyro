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
// JobListener: Job Listener
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Listener class to receive job from queue.
 *
 * @author takami torao
 */
public interface JobListener {

	// ======================================================================
	// Receive Job
	// ======================================================================
	/**
	 * Notify when specified job received on queue.
	 *
	 * @param job received job
	*/
	public void received(Job job);

}
