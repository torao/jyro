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
// Worker: Node Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Worker process bound to node.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/02 Java SE 6
 */
public interface Worker {

	// ======================================================================
	// Execute Process
	// ======================================================================
	/**
	 * Execute this process with specified arguments. This method called in
	 * multi-thread environment.
	 *
	 * @param job job argument
	 * @return result
	 * @throws WorkerException if error in worker
	*/
	public Object receive(Job job) throws WorkerException;

}
