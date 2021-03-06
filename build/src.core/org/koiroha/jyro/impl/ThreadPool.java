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

import java.util.concurrent.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// ThreadPool:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/21 Java SE 6
 */
public final class ThreadPool {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(ThreadPool.class);

	// ======================================================================
	// Node ID
	// ======================================================================
	/**
	 * Node ID
	 */
	private final String id;

	// ======================================================================
	// Class Loader
	// ======================================================================
	/**
	 * Class loader.
	 */
	private final ClassLoader loader;

	// ======================================================================
	// Job Queue
	// ======================================================================
	/**
	 * Job queue for workers.
	 */
	private final BlockingQueue<Runnable> queue;

	// ======================================================================
	// Thread Pool
	// ======================================================================
	/**
	 * Thread pool to run workers.
	 */
	private ThreadPoolExecutor threads = null;

	// ======================================================================
	// Thread Group
	// ======================================================================
	/**
	 * Thread group of this worker threads.
	 */
	private final ThreadGroup threadGroup;

	// ======================================================================
	// Minimum Workers
	// ======================================================================
	/**
	 * Minimum workers on this node.
	 */
	private int minimumWorkers = 5;

	// ======================================================================
	// Maximum Workers
	// ======================================================================
	/**
	 * Maximum workers on this node.
	 */
	private int maximumWorkers = 10;

	// ======================================================================
	// Stack Size
	// ======================================================================
	/**
	 * Stack size as bytes of new worker thread.
	 */
	private int stackSize = -1;

	// ======================================================================
	// Thread Priority
	// ======================================================================
	/**
	 * Thread priority of new worker threads.
	 */
	private int priority = Thread.NORM_PRIORITY;

	// ======================================================================
	// Daemon
	// ======================================================================
	/**
	 * Whether worker thread run as daemon.
	 */
	private boolean daemon = false;

	// ======================================================================
	// Load Average
	// ======================================================================
	/**
	 * Load average calculator for worker process queue.
	 */
	private final LoadAverage loadAverage;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param id task name of this node
	 * @param loader class loader of this node
	 * @param queue job queue
	 * @param proc process to execute on this node
	 */
	public ThreadPool(String id, ClassLoader loader) {
		this.threadGroup = new ThreadGroup(id);
		this.queue = new ArrayBlockingQueue<Runnable>(1);
		this.id = id;
		this.loader = loader;

		// create load average calculator
		this.loadAverage = new LoadAverage(this.queue);
		this.loadAverage.start();
		return;
	}

	// ======================================================================
	// Active Worker Count
	// ======================================================================
	/**
	 * Retrieve the number of active worker threads to execute.
	 *
	 * @return active workers
	*/
	public int getActiveWorkers(){
		return threads.getActiveCount();
	}

	// ======================================================================
	// Min Worker Execution
	// ======================================================================
	/**
	 * Retrieve the number of minimum worker threads to execute.
	 *
	 * @return max workers
	*/
	public int getMinimumWorkers(){
		return minimumWorkers;
	}

	// ======================================================================
	// Min Worker Execution
	// ======================================================================
	/**
	 * Set the number of minimum worker threads to execute.
	 * for implementation.
	 *
	 * @param min number of minimum workers
	*/
	public void setMinimumWorkers(int min){
		this.minimumWorkers = min;
		if(threads != null){
			threads.setCorePoolSize(min);
		}
		return;
	}

	// ======================================================================
	// Max Worker Execution
	// ======================================================================
	/**
	 * Retrieve the number of maximum worker threads to execute.
	 *
	 * @return max workers
	*/
	public int getMaximumWorkers(){
		return maximumWorkers;
	}

	// ======================================================================
	// Max Worker Execution
	// ======================================================================
	/**
	 * Set the number of maximum worker threads to execute.
	 * for implementation.
	 *
	 * @param max number of maximum workers
	*/
	public void setMaximumWorkers(int max){
		this.maximumWorkers = max;
		if(threads != null){
			threads.setMaximumPoolSize(max);
		}
		return;
	}

	// ======================================================================
	// Retrieve Thread Priority
	// ======================================================================
	/**
	 * Retrieve priority of worker thread on this node.
	 *
	 * @return thread priority
	 */
	public int getPriority(){
		return priority;
	}

	// ======================================================================
	// Set Thread Priority
	// ======================================================================
	/**
	 * Set priority of worker thread on this node.
	 *
	 * @param priority thread priority that defined in class {@link Thread}
	 */
	public void setPriority(int priority){
		if(priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY){
			throw new IllegalArgumentException("invalid thread priority: " + priority);
		}
		this.priority = priority;
		return;
	}

	// ======================================================================
	// Retrieve Daemon
	// ======================================================================
	/**
	 * Retrieve whether worker thread on this node is daemon or not.
	 *
	 * @return true if worker is daemon
	 */
	public boolean isDaemon() {
		return daemon;
	}

	// ======================================================================
	// Set Daemon
	// ======================================================================
	/**
	 * Set daemon flag of worker thread. This should be call before start.
	 *
	 * @param daemon true if worker will be daemon
	 */
	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
		return;
	}

	// ======================================================================
	// Retrieve Stack Size
	// ======================================================================
	/**
	 * Retrieve stack size for worker thread on this node.
	 *
	 * @return stack size (bytes)
	 */
	public int getStackSize() {
		return stackSize;
	}

	// ======================================================================
	// Set Stack Size
	// ======================================================================
	/**
	 * Set staci size of worker thread. This should be call before start.
	 * if negative value specified, runtime default stack size used.
	 *
	 * @param stackSize stack size (bytes)
	 */
	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
		return;
	}

	// ======================================================================
	// Retrieve Load Average
	// ======================================================================
	/**
	 * Retrieve load average of this node. The return values are 3 load
	 * average for 1, 5 and 15 minutes.
	 *
	 * @return load average (1, 5, 15min)
	 */
	public double[] getLoadAverage(){
		return loadAverage.getLoadAverage();
	}

	// ======================================================================
	// Start Node
	// ======================================================================
	/**
	 * Start workers on this node.
	*/
	public void start(){
		threads = new ThreadPoolExecutor(minimumWorkers, maximumWorkers, 10, TimeUnit.SECONDS, queue);
		threads.setThreadFactory(new NodeThreadFactory());
		return;
	}

	// ======================================================================
	// Stop Node
	// ======================================================================
	/**
	 * Stop workers on this node.
	*/
	public void stop(){
		threads.shutdown();
		threads = null;
		return;
	}

	// ======================================================================
	// Execute Worker
	// ======================================================================
	/**
	 * Execute worker process.
	 *
	 * @param job arguments for worker
	 * @return result
	*/
	public void execute(Runnable r){
		threads.execute(r);
		return;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// NodeThreadFactory
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * The thread factory for this node.
	*/
	private class NodeThreadFactory implements ThreadFactory {

		// ==================================================================
		// Thread Sequence Number
		// ==================================================================
		/**
		 * Sequence number for name of new thread.
		 */
		private int seq = 0;

		// ==================================================================
		// Create New Thread
		// ==================================================================
		/**
		 * Create new thread to execute specified runnable.
		 *
		 * @param r runnable
		 */
		@Override
		public Thread newThread(Runnable r) {

			// issue new sequence number
			int num = 0;
			synchronized(this){
				num = seq;
				seq = Math.abs(seq+1);
			}

			// create new thread and set attributes
			String name = id + "-" + num;
			Thread thread = null;
			if(stackSize >= 0){
				thread = new Thread(threadGroup, r, name, stackSize);
			} else {
				thread = new Thread(threadGroup, r, name);
			}
			thread.setPriority(priority);
			thread.setDaemon(daemon);
			thread.setContextClassLoader(loader);
			logger.trace("create new thread: " + name);
			return thread;
		}
	}

}
