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

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Worker: Node Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Worker process bound to node.
 *
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/02 Java SE 6
 */
public class Worker {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(Worker.class);

	// ======================================================================
	// Worker Context
	// ======================================================================
	/**
	 * The context of this worker implementation.
	 */
	private WorkerContext context = null;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Default constructor called dynamically.
	 */
	public Worker() {
		return;
	}

	// ======================================================================
	// initialize worker
	// ======================================================================
	/**
	 * Initialize this worker.
	 *
	 * @param context worker context
	 */
	public void init(WorkerContext context){
		logger.debug("init(" + context + ")");
		this.context = context;
		return;
	}

	// ======================================================================
	// Refer Context
	// ======================================================================
	/**
	 * Refer context of this worker.
	 *
	 * @return worker context
	 */
	public WorkerContext getContext() {
		return context;
	}

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
	public Object receive(Job job) throws WorkerException{
		return null;
	}

}
