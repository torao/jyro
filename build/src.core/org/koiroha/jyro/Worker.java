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
// Worker: Worker
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
	 */
	public void init(){
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
	// Set Context
	// ======================================================================
	/**
	 * Set specified context to this worker instance. This method is called
	 * framework and can call only once for instance.
	 *
	 * @param context worker context
	 * @throws IllegalStateException if context already set
	 */
	public final void setContext(WorkerContext context) throws IllegalStateException{
		assert(context != null);
		if(this.context != null){
			logger.error("context specified");
			throw new IllegalStateException("context already present");
		}
		this.context = context;
		return;
	}

}
