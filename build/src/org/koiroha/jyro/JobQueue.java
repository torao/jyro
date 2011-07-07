/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the Apache
 * License Ver. 2.0, and comes with NO WARRANTY.
 *
 * takami torao <koiroha@gmail.com> http://www.bjorfuan.com/
 */
package org.koiroha.jyro;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JobQueue: Job Queue
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The interface for job queue.
 *
 * @author takami torao
 */
public interface JobQueue {

	// ======================================================================
	// Post Job
	// ======================================================================
	/**
	 * Post specified job to this queue.
	 *
	 * @param job job to post
	 * @throws JyroException if fail to post job
	 */
	public abstract void post(Job job) throws JyroException;

	// ======================================================================
	// Add Listener
	// ======================================================================
	/**
	 * Add specified listener to this queue.
	 *
	 * @param l listener
	 */
	public abstract void addJobQueueListener(JobListener l);

	// ======================================================================
	// Remove Listener
	// ======================================================================
	/**
	 * Remove specified listener from this queue.
	 *
	 * @param l listener
	 */
	public abstract void removeJobQueueListener(JobListener l);

}