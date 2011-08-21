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

import java.lang.management.*;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.*;

import org.apache.log4j.*;
import org.koiroha.jyro.*;
import org.koiroha.jyro.WorkerFilter.Next;

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
	// Filter
	// ======================================================================
	/**
	 * Worker filters.
	 */
	private final List<WorkerFilter> filters;

	// ======================================================================
	// Bus
	// ======================================================================
	/**
	 * Bus.
	 */
	private final Bus bus;

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
	private Dispatcher dispatcher = null;

	// ======================================================================
	// Thread Pool
	// ======================================================================
	/**
	 * Thread pool to run workers.
	 */
	private final ThreadPool threads;

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
	 * @param id task name of this node
	 * @param loader class loader of this node
	 * @param queue job queue
	 * @param proc process to execute on this node
	 */
	public Node(String id, ClassLoader loader, Bus bus, ThreadPool threads, Collection<WorkerFilter> filters, Worker proc) {
		assert(id != null);
		assert(loader != null);
		assert(proc != null);
		this.id = id;
		this.loader = loader;
		this.bus = bus;
		this.filters = new ArrayList<WorkerFilter>(filters);
		this.worker = proc;

		this.threads = threads;
		return;
	}

	// ======================================================================
	// Refer Bus
	// ======================================================================
	/**
	 * Refer bus of this node use.
	 *
	 * @return bus
	*/
	Bus getBus(){
		return bus;
	}

	// ======================================================================
	// Retrieve Thread Pool
	// ======================================================================
	/**
	 * Retrieve thread pool to execute worker in this node.
	 *
	 * @return thread pool
	 */
	public ThreadPool getThreadPool() {
		return threads;
	}

	// ======================================================================
	// Retrieve Method Names
	// ======================================================================
	/**
	 * Retrieve all distributed methods in this instance.
	 * The default behavior of this method is to return methods with
	 * {@link Distribute} annotation.
	 *
	 * @return distributed methods
	 */
	public Method[] getDistributedMethods(){
		return worker.getDistributedMethods();
	}

	// ======================================================================
	// Return Worker Function Names
	// ======================================================================
	/**
	 * Return worker function names that has {@link Distribute} marker.
	 * 0 length array returns if no annotation specified.
	 *
	 * @return worker functions
	*/
	public String[] getFunctions(){
		List<String> names = new ArrayList<String>();
		for(Method m: getDistributedMethods()){
			String name = Jyro.getFunctionName(m);
			if(name != null){
				names.add(name);
			}
		}
		return names.toArray(new String[names.size()]);
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
	// Initialize Node
	// ======================================================================
	/**
	 * Initialize workers on this node.
	*/
	public void init() throws JyroException{
		logger.debug("initializing " + getId() + " node");
		for(WorkerFilter f: filters){
			f.init();
		}
		worker.init();
		return;
	}

	// ======================================================================
	// Start Node
	// ======================================================================
	/**
	 * Start workers on this node.
	*/
	public void start(){
		logger.debug("starting " + getId() + " node");

		// start thread pool
		threads.start();

		// start job dispatcher
		dispatcher = new Dispatcher(this);
		dispatcher.start();

		return;
	}

	// ======================================================================
	// Stop Node
	// ======================================================================
	/**
	 * Stop workers on this node.
	*/
	public void stop(){
		logger.debug("stopping " + getId() + " node");

		// start job dispatcher
		dispatcher.stop();
		dispatcher = null;

		// shutdown thread pool
		threads.stop();
		return;
	}

	// ======================================================================
	// Destroy Node
	// ======================================================================
	/**
	 * Destroy workers on this node.
	*/
	public void destroy() {
		logger.debug("destroying " + getId() + " node");
		worker.destroy();
		List<WorkerFilter> rev = new ArrayList<WorkerFilter>(filters);
		Collections.reverse(rev);
		for(WorkerFilter f: rev){
			f.destroy();
		}
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
	void post(Runnable r){
		threads.execute(r);
		return;
	}

	// ======================================================================
	// Execute Worker
	// ======================================================================
	/**
	 * Execute specified worker process in current thread (synchronously).
	 *
	 * @param job arguments for worker
	 * @return result
	*/
	public Object execute(Job job) {
		NDC.push(getId());
		long start = rb.getUptime();
		Object result = null;
		try {
			result = execFilters(job);
			totalJobCount ++;
			totalJobTime += rb.getUptime() - start;

			// success callback
			bus.callback(job.new Result(result, null));

		} catch(Throwable ex){
			logger.fatal("unexpected exception in worker", ex);

			// error callback
			try {
				bus.callback(job.new Result(null, ex));
			} catch (JyroException e) {
				logger.fatal("fail to callback", ex);
			}

			// through Error because thread pool cleanup this thread
			if(ex instanceof Error){
				throw (Error)ex;
			}
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

	// ======================================================================
	// Execute Worker
	// ======================================================================
	/**
	 * Execute worker process.
	 *
	 * @param job arguments for worker
	 * @return result
	*/
	private Object execFilters(Job job) throws JyroException{

		// last
		WorkerFilter.Next last = new Next() {
			@Override
			public Object execute(Job job) throws JyroException {
				return worker.execute(job);
			}
		};
		for(int i=filters.size()-1; i>=0; i--){
			WorkerFilter.Next hop = new FilterRunnableChain(filters.get(i), last);
			last = hop;
		}

		return last.execute(job);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// NodeThreadFactory
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * The thread factory for this node.
	*/
	private class FilterRunnableChain implements WorkerFilter.Next {

		/** Next runnable. */
		private final WorkerFilter.Next next;

		/** Next runnable. */
		private final WorkerFilter filter;

		/**
		 * Constructor.
		 * @param filter filter to call next
		 * @param next next called runnable object
		 */
		public FilterRunnableChain(WorkerFilter filter, WorkerFilter.Next next){
			this.filter = filter;
			this.next = next;
			return;
		}

		/**
		 * Execute next runnable object.
		 */
		@Override
		public Object execute(Job job) throws JyroException {
			return filter.filter(job, next);
		}
	}

}
