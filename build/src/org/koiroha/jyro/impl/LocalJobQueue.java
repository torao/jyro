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
package org.koiroha.jyro.impl;

import java.util.concurrent.*;

import org.koiroha.jyro.Job;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// LocalJobQueue: Local Job Queue
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Queue implementation for local Java VM.
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/06 Java SE 6
 */
public class LocalJobQueue extends JobQueueImpl {

	// ======================================================================
	// Queue
	// ======================================================================
	/**
	 * Queue map of current Java VM.
	*/
	private final BlockingQueue<Job> queue;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param id id of this queue.
	 */
	public LocalJobQueue(String id) {
		super(id);
		this.queue = new LinkedBlockingQueue<Job>();
		return;
	}

	// ======================================================================
	// Post Job
	// ======================================================================
	/**
	 * Post specified job to this queue.
	*/
	@Override
	public void post(Job job) {
		queue.offer(job);
		return;
	}

	// ======================================================================
	// Receive Job
	// ======================================================================
	/**
	 * Receive job from this queue.
	 *
	 * @throws InterruptedException thread interrupted while waiting job
	*/
	@Override
	public Job receive() throws InterruptedException {
		return queue.take();
	}

}
