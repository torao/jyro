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

import org.apache.log4j.*;
import org.koiroha.jyro.*;

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
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JobQueueImpl.class);

	// ======================================================================
	// Interval Time to Wait
	// ======================================================================
	/**
	 * The interval in millis to wait stop receiver thread.
	 */
	private static final long STOP_WAITING_INTERVAL = 3 * 1000;

	// ======================================================================
	// ID
	// ======================================================================
	/**
	 * ID of this job queue.
	*/
	private final String id;

	// ======================================================================
	// Receiver Thread
	// ======================================================================
	/**
	 * The thread to receive subclass specified queue implementation.
	*/
	private Receiver receiver = null;

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
	 * @param id ID of this queue
	*/
	protected JobQueueImpl(String id){
		this.id = id;
		return;
	}

	// ======================================================================
	// Retrieve ID
	// ======================================================================
	/**
	 * Retrieve ID of this job queue.
	 *
	 * @return queue id
	 */
	public String getId() {
		return id;
	}

	// ======================================================================
	// Start Queuing
	// ======================================================================
	/**
	 * Start queuing.
	 */
	public synchronized void start(){
		stop();
		receiver = new Receiver();
		receiver.start();
		return;
	}

	// ======================================================================
	// Start Queuing
	// ======================================================================
	/**
	 * Start queuing.
	 */
	public synchronized void stop(){
		if(receiver != null){

			// stop receiver thread
			org.koiroha.jyro.util.Thread.kill(receiver, STOP_WAITING_INTERVAL);
			receiver = null;

			// close queue
			try {
				close();
			} catch(JyroException ex){
				logger.error("fail to close queue: " + getId(), ex);
			}
		}
		return;
	}

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
	// Receive Job
	// ======================================================================
	/**
	 * Receive job from this queue.
	 *
	 * @return received job
	 * @throws InterruptedException if interrupted while waiting queue
	 * @throws JyroException fail to receive
	*/
	protected abstract Job receive() throws InterruptedException, JyroException ;

	// ======================================================================
	// Close Queue
	// ======================================================================
	/**
	 * Close this queue.
	 *
	 * @throws JyroException if fail to close queue
	*/
	protected void close() throws JyroException{
		logger.debug("job queue closed: " + getId());
		return;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Receiver: Queue Receiver
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Queue receiver thread.
	 */
	private class Receiver extends Thread {

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 */
		public Receiver(){
			setName("JobQueueReceiver[" + JobQueueImpl.this.getId() + ":" + getId() + "]");
			return;
		}

		// ==================================================================
		// Start Queuing
		// ==================================================================
		/**
		 * Start queuing.
		 */
		@Override
		public void run(){
			NDC.push(JobQueueImpl.this.getId());
			try {
				logger.debug("start job receiver thread");
				while(! Thread.currentThread().isInterrupted()){
					dispatch();
				}
			} catch(InterruptedException ex){
				// ignore
			} finally {
				logger.debug("end job receiver thread");
				NDC.pop();
			}
			return;
		}

		// ==================================================================
		// Dispatch Job
		// ==================================================================
		/**
		 * Receive job and dispatch.
		 *
		 * @throws InterruptedException
		 */
		private void dispatch() throws InterruptedException {
			try {

				// receive job from subclass implementation
				Job job = receive();

				// notify all listeners
				for(JobListener l: listeners){
					try {
						l.received(job);
					} catch(Exception ex){
						logger.fatal("fail to dispatch job " + job + " to listener " + l, ex);
					}
				}

			} catch(JyroException ex){
				logger.fatal("", ex);
				Thread.sleep(3 * 1000);
			}
			return;
		}

	}

}
