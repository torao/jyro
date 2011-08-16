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

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Dispatcher:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Transmit job between bus and worker thread pool.
 * <p>
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
final class Dispatcher {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(Dispatcher.class);

	// ======================================================================
	// Node
	// ======================================================================
	/**
	 * Node to dispatch job.
	 */
	private final Node node;

	// ======================================================================
	// Threads
	// ======================================================================
	/**
	 * Receiver threads.
	 */
	private final Receiver[] receivers;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param bus bus
	 * @param worker worker to execute job
	 * @param threads thread pool
	 * @param funcs function names
	 */
	public Dispatcher(Node node) {
		this.node = node;

		// create receivers
		String[] func = node.getFunctions();
		this.receivers = new Receiver[func.length];
		for(int i=0; i<func.length; i++){
			this.receivers[i] = new Receiver(func[i]);
		}
		return;
	}

	// ======================================================================
	// Start Dispatch
	// ======================================================================
	/**
	 * Start job dispatch.
	 */
	public void start() {
		for(Receiver r: receivers){
			r.start();
		}
		return;
	}

	// ======================================================================
	// Start Dispatch
	// ======================================================================
	/**
	 * Start job dispatch.
	 */
	public void stop() {
		for(Receiver r: receivers){
			r.cancel();
		}
		return;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Receiver: Job Dispatcher
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Job dispatcher thread from bus to worker thread pool.
	*/
	private class Receiver extends Thread {

		// ==================================================================
		// Function Name
		// ==================================================================
		/**
		 * Function name to receive job from bus.
		 */
		private final String func;

		// ==================================================================
		// Stop Flag
		// ==================================================================
		/**
		 * The flag to specify stop queue pomping.
		 */
		private volatile boolean stop = false;

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * @param func function name
		 */
		public Receiver(String func){
			this.func = func;
			return;
		}

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * @param func function name
		 */
		public void cancel(){
			stop = true;
			interrupt();
			return;
		}

		// ==================================================================
		// Run Thread
		// ==================================================================
		/**
		 * Receive job for constructor-specified function and put caller to
		 * thread pool until thread interrupted.
		 */
		@Override
		public void run() {
			Bus bus = node.getBus();
			try {
				boolean error = false;	// error status
				while(!stop && !Thread.interrupted()){

					// retrieve job from bus
					Job job = null;
					try {
						job = bus.receive(func);
						error = false;		// reset error status
					} catch(JyroException ex){
						if(! error){
							logger.fatal("", ex);	// TODO
							error = true;
						}
						Thread.sleep(3000);
						continue;
					}
					assert(job != null);

					// pass job to thread pool
					node.post(new Caller(job));
				}
			} catch(InterruptedException ex){
				logger.debug("pomping thread finish");
			}
			return;
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Caller:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * The thread factory for this node.
	*/
	private class Caller implements Runnable {

		// ==================================================================
		// Job
		// ==================================================================
		/**
		 * Job to execute by worker.
		 */
		private final Job job;

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * @param job job to pass worker
		 */
		public Caller(Job job){
			this.job = job;
			return;
		}

		// ==================================================================
		// Run Thread
		// ==================================================================
		/**
		 * Call worker and callback result to bus.
		 */
		@Override
		public void run() {
			node.exec(job);
			return;
		}
	}

}
