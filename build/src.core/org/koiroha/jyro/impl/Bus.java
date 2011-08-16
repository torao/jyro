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

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Bus: Node Bus
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The abstract class to connect and send, receive job between node.
 *
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public abstract class Bus {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(Bus.class);

	// ======================================================================
	// Attribute
	// ======================================================================
	/**
	 * Attribute values of this factory.
	 */
	private static final Map<String,String> attributes = new HashMap<String,String>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Constructor can call only from subclass.
	 */
	protected Bus(){
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
	public abstract void send(Job job) throws FunctionNotFoundException, JyroException;

	// ======================================================================
	// Receive Job
	// ======================================================================
	/**
	 * Receive job.
	 *
	 * @param func function names to receive job
	 * @throws JyroException if fail to post job
	 */
	public abstract Job receive(String func) throws JyroException, InterruptedException;

	// ======================================================================
	// Callback Result
	// ======================================================================
	/**
	 * Callback execution result from worker.
	 *
	 * @param result result of job execution
	 */
	public abstract void callback(Job.Result result) throws JyroException;

	// ======================================================================
	//
	// ======================================================================
	/**
	 */
	public void close(){
		return;
	}

	// ======================================================================
	// Create Job Queue
	// ======================================================================
	/**
	 * Create job queue for specified worker interface.
	 *
	 * @param worker worker interface type
	 * @return BlockingQueue instance
	 * @throws JyroException if fail to refer queue
	 */
	public String getAttribute(String name) {
		if(logger.isDebugEnabled()){
			logger.debug("getAttribute(" + name + "): " + attributes.get(name));
		}
		return attributes.get(name);
	}

}
