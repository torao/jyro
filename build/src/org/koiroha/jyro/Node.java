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

import java.lang.management.*;
import java.text.NumberFormat;
import java.util.concurrent.*;

import org.apache.log4j.*;

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
	private final String id;

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
	private ThreadPoolExecutor threads = null;

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
	// Load Average
	// ======================================================================
	/**
	 * Load average calculator for worker process queue.
	 */
	private final LoadAverage loadAverage;

	// ======================================================================
	// Job Count
	// ======================================================================
	/**
	 * The number of jobs that this node finish.
	 */
	private final RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();

	// ======================================================================
	// Job Count
	// ======================================================================
	/**
	 * The number of jobs that this node finish.
	 */
	private volatile long totalJobCount = 0;

	// ======================================================================
	// Total Time
	// ======================================================================
	/**
	 * Total time this node transact.
	 */
	private volatile long totalJobTime = 0;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param proc process to execute on this node
	 * @param id task name of this node
	 * @param loader class loader of this node
	 */
	public Node(String id, ClassLoader loader, Worker proc) {
		assert(id != null);
		assert(loader != null);
		assert(proc != null);
		this.id = id;
		this.loader = loader;
		this.worker = proc;

		// create load average calculator
		this.loadAverage = new LoadAverage(queue);
		this.loadAverage.start();

		this.threadGroup = new ThreadGroup(id);
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
	public String getId(){
		return id;
	}

	// ======================================================================
	// Retrieve Class Loader
	// ======================================================================
	/**
	 * Retrieve default class loader of this node.
	 *
	 * @return class loader
	*/
	public ClassLoader getClassLoader(){
		return loader;
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
	//
	// ======================================================================
	/**
	 * Retrieve total job count that this node successfully execute.
	 *
	 * @return total job count
	 */
	public long getTotalJobCount(){
		return totalJobCount;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * Retrieve total job time that this node successfully execute.
	 *
	 * @return total job time
	 */
	public long getTotalJobTime(){
		return totalJobTime;
	}

	// ======================================================================
	// Start Node
	// ======================================================================
	/**
	 * Start workers on this node.
	*/
	public void start(){
		logger.debug("start node " + getId());
		threads = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, queue);
		threads.setThreadFactory(new ThreadFactory() {
			private volatile int seq = 0;
			@Override
			public Thread newThread(Runnable r) {
				int num = seq;
				seq = Math.abs(seq+1);
				String name = Node.this.id + "-" + num;
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
	// Stop Node
	// ======================================================================
	/**
	 * Stop workers on this node.
	*/
	public void stop(){
		logger.debug("stop node " + getId());
		threads.shutdown();
		threads = null;
		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * @param job job to execute
	 * @throws JyroException if node is not running
	*/
	public void post(final Job job) throws JyroException{
		if(threads == null){
			throw new JyroException("node is not running");
		}
		threads.execute(new Runnable(){
			@Override
			public void run(){
				exec(job);
			}
		});
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
	private Object exec(Job job){
		NDC.push(getId());
		long start = rb.getUptime();
		Object result = null;
		try {
			result = worker.exec(job);
			totalJobCount ++;
			totalJobTime += rb.getUptime() - start;
		} catch(WorkerException ex){
			logger.error("", ex);
		} catch(Throwable ex){
			if(ex instanceof ThreadDeath){
				throw (ThreadDeath)ex;
			}
			logger.fatal("unexpected exception in worker", ex);
		} finally {
			if(logger.isDebugEnabled()){
				long end = rb.getUptime();
				NumberFormat nf = NumberFormat.getNumberInstance();
				logger.debug("exec(" + job + ") := " + result + " " + nf.format(end-start) + "ms");
			}
			NDC.pop();
		}
		return result;
	}

}
