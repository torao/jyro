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
import org.koiroha.jyro.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// LocalBus:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public class LocalBus extends Bus {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(LocalBus.class);

	// ======================================================================
	// JavaVM Job Queue Map
	// ======================================================================
	/**
	 * JavaVM job queue map.
	 */
	private static final Map<String,BlockingQueue<Job>> queues
		= Collections.synchronizedMap(new HashMap<String,BlockingQueue<Job>>());

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 */
	public LocalBus() {
		return;
	}


	// ======================================================================
	// Send Job
	// ======================================================================
	/**
	 * Send specified job on this bus.
	 *
	 * @param job job to post any node
	 * @throws JyroException if fail to post job
	 */
	@Override
	public void send(Job job) throws FunctionNotFoundException, JyroException{
		if(logger.isDebugEnabled()){
			logger.debug("post(" + job + ")");
		}
		BlockingQueue<Job> queue = queues.get(job.getFunction());
		if(queue == null){
			throw new FunctionNotFoundException("worker not exists on local jvm: " + job.getFunction());
		}
		try {
			queue.put(job);
		} catch(InterruptedException ex){
			throw new JyroException("operation interrupted");
		}
		return;
	}

	// ======================================================================
	// Receive Job
	// ======================================================================
	/**
	 * Receive job.
	 *
	 * @param funcs function names to receive job
	 * @throws JyroException if fail to post job
	 */
	@Override
	public Job receive(String func) throws InterruptedException{
		BlockingQueue<Job> queue = null;
		synchronized(queues){
			queue = queues.get(func);
			if(queue == null){
				queue = new LinkedBlockingQueue<Job>(getCapacity());
				queues.put(func, queue);
			}
		}
		return queue.take();
	}

	// ======================================================================
	// Callback Result
	// ======================================================================
	/**
	 * Callback execution result from worker.
	 *
	 * @param result result of job execution
	 */
	@Override
	public void callback(Job.Result result){
		result.callback();
		return;
	}

	// ======================================================================
	// Receive Job
	// ======================================================================
	/**
	 * Receive job.
	 *
	 * @param job job to post any node
	 * @throws JyroException if fail to post job
	 */
	private int getCapacity(){
		int capacity = Integer.MAX_VALUE;
		String attr = getAttribute("maxCapacity");
		if(attr != null){
			try {
				capacity = Integer.parseInt(attr);
				// TODO if negative
			} catch(NumberFormatException ex){
				// TODO warning message
			}
		}
		return capacity;
	}

}
