/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.koiroha.jyro.JobQueue;
import org.koiroha.jyro.JobQueueFactory;
import org.koiroha.jyro.JobQueueImpl;
import org.koiroha.jyro.JyroException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JobQueueFactoryImpl: Default Implementation for JobQueueFactory
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The default implementation for JobQueueFactory.
 *
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public abstract class JobQueueFactoryImpl implements JobQueueFactory {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(JobQueueFactoryImpl.class);

	// ======================================================================
	// Job Queue Map
	// ======================================================================
	/**
	 * Job queue map.
	 */
	private static final Map<String,JobQueueImpl> queues
		= Collections.synchronizedMap(new HashMap<String,JobQueueImpl>());

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Nothing to do in super class.
	 */
	public JobQueueFactoryImpl() {
		return;
	}

	// ======================================================================
	// Create Queue
	// ======================================================================
	/**
	 * Create new job queue for specified nodeId.
	 *
	 * @param nodeId node id
	 * @return new job queue
	 * @throws JyroException
	 */
	@Override
	public JobQueueImpl create(String nodeId) throws JyroException {
		synchronized(queues){
			JobQueueImpl queue = queues.get(nodeId);
			if(queue == null){
				queue = createQueue(nodeId);
				queues.put(nodeId, queue);
			}
			return queue;
		}
	}

	// ======================================================================
	// Lookup Queue
	// ======================================================================
	/**
	 * Lookup queue for specified nodeId.
	 *
	 * @param nodeId node id
	 * @return job queue
	 * @throws JyroException
	 */
	@Override
	public JobQueue lookup(String nodeId) throws JyroException {
		JobQueue queue = queues.get(nodeId);
		if(queue == null){
			throw new JyroException("no such queue: " + nodeId);
		}
		return queue;
	}

	// ======================================================================
	// Create Job Queue
	// ======================================================================
	/**
	 * Create new job queue instance.
	 *
	 * @param nodeId
	 * @return job queue implementation
	 * @throws JyroException
	 */
	protected abstract JobQueueImpl createQueue(String nodeId) throws JyroException;

}
