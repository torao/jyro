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

import org.koiroha.jyro.impl.Job;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// WorkerFilter: Filter for Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/03 Java SE 6
*/
public interface WorkerFilter {

	// ======================================================================
	// Execute Worker
	// ======================================================================
	/**
	 * Execute worker process.
	 *
	 * @param job arguments for worker
	 * @return result
	*/
	public Object filter(Job job, Next hop) throws JyroException;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Next: Next Filter
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Next filter instance to execute.
	*/
	public interface Next {

		/**
		 * Execute next runnable object.
		 */
		public Object execute(Job job) throws JyroException;
	}

}
