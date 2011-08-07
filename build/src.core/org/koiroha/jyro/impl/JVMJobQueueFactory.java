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

import java.util.*;
import java.util.concurrent.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.Job;
import org.koiroha.jyro.JobQueue;
import org.koiroha.jyro.JobQueueFactory;
import org.koiroha.jyro.JobQueueImpl;
import org.koiroha.jyro.JyroException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JVMJobQueueFactory:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public class JVMJobQueueFactory implements JobQueueFactory {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(JVMJobQueueFactory.class);

	// ======================================================================
	// JavaVM Job Queue Map
	// ======================================================================
	/**
	 * JavaVM job queue map.
	 */
	private static final Map<String,JVMJobQueue> queues
		= Collections.synchronizedMap(new HashMap<String,JVMJobQueue>());

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 */
	public JVMJobQueueFactory() {
		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @param nodeId
	 * @return
	 * @throws JyroException
	 */
	@Override
	public JobQueueImpl create(String nodeId) throws JyroException {
		synchronized(queues){
			JVMJobQueue queue = queues.get(nodeId);
			if(queue == null){
				queue = new JVMJobQueue(nodeId);
				queues.put(nodeId, queue);
			}
			return queue;
		}
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @param nodeId
	 * @return
	 * @throws JyroException
	 */
	@Override
	public JobQueue lookup(String nodeId) throws JyroException {
		JVMJobQueue queue = queues.get(nodeId);
		if(queue == null){
			throw new JyroException("no such queue: " + nodeId);
		}
		return queue;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// JVMJobQueue:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 * @author torao
	 * @since 2011/07/24 Java SE 6
	 */
	private static class JVMJobQueue extends JobQueueImpl {

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * @param id ID of this queue
		*/
		private final BlockingQueue<Job> queue = new LinkedBlockingQueue<Job>();

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * @param id ID of this queue
		*/
		protected JVMJobQueue(String id){
			super(id);
			return;
		}

		// ==================================================================
		//
		// ==================================================================
		/**
		 *
		 * @param job
		 * @throws JyroException
		 */
		@Override
		public void post(Job job) throws JyroException {
			if(logger.isDebugEnabled()){
				logger.debug("[" + getId() + "] post(" + job + ")");
			}
			queue.add(job);
			return;
		}

		// ==================================================================
		//
		// ==================================================================
		/**
		 *
		 * @return
		 * @throws InterruptedException
		 * @throws JyroException
		 */
		@Override
		protected Job receive() throws InterruptedException, JyroException {
			Job job = queue.take();
			if(logger.isDebugEnabled()){
				logger.debug("[" + getId() + "] receive() := " + job);
			}
			return job;
		}

	}

}
