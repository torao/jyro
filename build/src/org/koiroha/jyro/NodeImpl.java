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
public class NodeImpl implements Node {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(NodeImpl.class);

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
	// Constructor
	// ======================================================================
	/**
	 * @param proc process to execute on this node
	 * @param taskName task name of this node
	 * @param loader class loader of this node
	 */
	public NodeImpl(String taskName, ClassLoader loader, Worker proc) {
		this.taskName = taskName;
		this.loader = loader;
		this.worker = proc;
		this.threads = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, queue);
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
	@Override
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
