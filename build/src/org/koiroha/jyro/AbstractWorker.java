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

import org.apache.log4j.Logger;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// AbstractWorker:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/23 Java SE 6
 */
public abstract class AbstractWorker implements Worker{

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(AbstractWorker.class);

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
	 *
	 */
	protected AbstractWorker() {
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

}
