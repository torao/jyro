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
// JobQueueFactory: Job Queue Factory
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Factory interface to create job queue for each nodes and to refer queues
 * to post job.
 *
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public interface JobQueueFactory {

	// ======================================================================
	// Create Job Queue
	// ======================================================================
	/**
	 * Create job queue for specified node id
	 *
	 * @param nodeId node ID of queue
	 * @return the implementation of job queue
	 * @throws JyroException if fail to create job queue
	 */
	public JobQueueImpl create(String nodeId) throws JyroException;

	// ======================================================================
	// Refer Job Queue
	// ======================================================================
	/**
	 * Refer job queue to post job.
	 *
	 * @param nodeId node ID of queue
	 * @return the implementation of job queue
	 * @throws JyroException if fail to post job
	 */
	public JobQueue lookup(String nodeId) throws JyroException;

}
