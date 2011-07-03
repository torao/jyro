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

import java.util.concurrent.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Node:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/01 Java SE 6
 */
public class Node {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Node.class);

	// ======================================================================
	// Task Name
	// ======================================================================
	/**
	 * Task name of this node.
	 */
	private final String taskName;

	// ======================================================================
	// Class Loader
	// ======================================================================
	/**
	 * Class Loader of this node.
	 */
	private final ClassLoader loader;

	// ======================================================================
	// Worker
	// ======================================================================
	/**
	 * Worker to execute parallel.
	 */
	private final Worker worker;

	// ======================================================================
	// Job Queue
	// ======================================================================
	/**
	 * Job queue for workers.
	 */
	private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

	// ======================================================================
	// Thread Pool
	// ======================================================================
	/**
	 * Thread pool to run workers.
	 */
	private final ThreadPoolExecutor threads;

	// ======================================================================
	// Thread Group
	// ======================================================================
	/**
	 * Thread group of this worker threads.
	 */
	private final ThreadGroup threadGroup;

	// ======================================================================
	// Stack Size
	// ======================================================================
	/**
	 * Stack size as bytes of new worker thread.
	 */
	private int stackSize = -1;

	// ======================================================================
	// Daemon
	// ======================================================================
	/**
	 * Whether worker thread run as daemon.
	 */
	private boolean daemon = false;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param proc process to execute on this node
	 * @param taskName task name of this node
	 * @param loader class loader of this node
	 */
	public Node(String taskName, ClassLoader loader, Worker proc) {
		assert(taskName != null);
		assert(loader != null);
		assert(proc != null);
		this.taskName = taskName;
		this.loader = loader;
		this.worker = proc;
		this.threads = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, queue);

		this.threadGroup = new ThreadGroup(taskName);
		this.threads.setThreadFactory(new ThreadFactory() {
			private volatile int seq = 0;
			@Override
			public Thread newThread(Runnable r) {
				int num = seq;
				seq = Math.abs(seq+1);
				String name = Node.this.taskName + "-" + num;
				Thread thread = null;
				if(stackSize >= 0){
					thread = new Thread(threadGroup, r, name, stackSize);
				} else {
					thread = new Thread(threadGroup, r, name);
				}
				thread.setDaemon(daemon);
				return thread;
			}
		});
		return;
	}

	// ======================================================================
	// Retrieve Name
	// ======================================================================
	/**
	 * Retrieve name of this node. The return value may be ununique in Jyro
	 * scope if same workers running on context.
	 *
	 * @return name of this node
	*/
	public String getTaskName(){
		return taskName;
	}

	// ======================================================================
	// Retrieve Enqueued Job
	// ======================================================================
	/**
	 * Retrieve enqueued and waiting jobs count.
	 *
	 * @return size of waiting jobs
	 */
	public int getWaitingJobs(){
		return queue.size();
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
		return threads.getCorePoolSize();
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
		threads.setCorePoolSize(min);
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
		return threads.getMaximumPoolSize();
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
		threads.setMaximumPoolSize(max);
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
	// Start Node
	// ======================================================================
	/**
	 * Start workers on this node.
	*/
	public void start(){
		logger.debug("start node " + getTaskName());
		threads.prestartAllCoreThreads();
		return;
	}

	// ======================================================================
	// Stop Node
	// ======================================================================
	/**
	 * Stop workers on this node.
	*/
	public void stop(){
		logger.debug("stop node " + getTaskName());
		threads.shutdown();
		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	*/
	public void post() {
		threads.execute(new Runnable(){
			@Override
			public void run(){
				worker.exec();
			}
		});
		return;
	}

}
