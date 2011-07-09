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

import java.util.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JobQueueImpl: Job Queue Implementation
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The class to implement job queue.
 *
 * @author takami torao
 */
public abstract class JobQueueImpl implements JobQueue {

	// ======================================================================
	// Listeners
	// ======================================================================
	/**
	 * Listeners of this queue.
	*/
	private final List<JobListener> listeners = new ArrayList<JobListener>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	*/
	protected JobQueueImpl(){
		return;
	}

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
	public void addJobQueueListener(JobListener l){
		listeners.add(l);
		return;
	}

	// ======================================================================
	// Remove Listener
	// ======================================================================
	/**
	 * Remove specified listener from this queue.
	 *
	 * @param l listener
	 */
	public void removeJobQueueListener(JobListener l){
		listeners.remove(l);
		return;
	}

	// ======================================================================
	// Notify Job Received
	// ======================================================================
	/**
	 * Notify all job listeners to receive specified job.
	 *
	 * @param job received job
	*/
	protected void fireJobQueueListener(Job job){
		for(JobListener l: listeners){
			l.received(job);
		}
		return;
	}

}
