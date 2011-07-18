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
package org.koiroha.jyro.jmx;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.util.ParseException;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// NodeMXBeanImpl:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/17 Java SE 6
 */
public class NodeMXBeanImpl implements NodeMXBean {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(NodeMXBeanImpl.class);

	// ======================================================================
	// Node
	// ======================================================================
	/**
	 * Node instance that this MXBean manages.
	*/
	private final Node node;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param node Jyro Node
	*/
	public NodeMXBeanImpl(Node node){
		this.node = node;
		return;
	}

	// ======================================================================
	// Refer ID
	// ======================================================================
	/**
	 * Refer id of node.
	 *
	 * @return ID
	*/
	@Override
	public String getId(){
		return node.getId();
	}

	// ======================================================================
	// Refer Waiting Jobs
	// ======================================================================
	/**
	 * Refer waiting jobs of node.
	 *
	 * @return waiting jobs
	*/
	@Override
	public int getWaitingJobs(){
		return node.getWaitingJobs();
	}

	// ======================================================================
	// Refer Active Workers
	// ======================================================================
	/**
	 * Refer active workers of node.
	 *
	 * @return active workers
	*/
	@Override
	public int getActiveWorkers(){
		return node.getActiveWorkers();
	}

	// ======================================================================
	// Refer Minimum Workers
	// ======================================================================
	/**
	 * Refer minimum workers of node.
	 *
	 * @return minimum workers
	*/
	@Override
	public int getMinimumWorkers(){
		return node.getMinimumWorkers();
	}

	// ======================================================================
	// Set Minimum Workers
	// ======================================================================
	/**
	 * Set minimum workers of node.
	 *
	 * @param min minimum workers
	*/
	@Override
	public void setMinimumWorkers(int min){
		node.setMinimumWorkers(min);
		return;
	}

	// ======================================================================
	// Refer Maximum Workers
	// ======================================================================
	/**
	 * Refer maximum workers of node.
	 *
	 * @return maximum workers
	*/
	@Override
	public int getMaximumWorkers(){
		return node.getMaximumWorkers();
	}

	// ======================================================================
	// Set Maximum Workers
	// ======================================================================
	/**
	 * Set maximum workers of node.
	 *
	 * @param max maximum workers
	*/
	@Override
	public void setMaximumWorkers(int max){
		node.setMaximumWorkers(max);
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
		return node.getPriority();
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
		node.setPriority(priority);
		return;
	}

	// ======================================================================
	// Refer Daemon
	// ======================================================================
	/**
	 * Refer daemon of node.
	 *
	 * @return true if daemon
	*/
	@Override
	public boolean isDaemon(){
		return node.isDaemon();
	}

	// ======================================================================
	// Set Daemon
	// ======================================================================
	/**
	 * Set daemon of node.
	 *
	 * @param daemon true if daemon
	*/
	@Override
	public void setDaemon(boolean daemon){
		node.setDaemon(true);
		return;
	}

	// ======================================================================
	// Refer Stack Size
	// ======================================================================
	/**
	 * Refer stack size of thread in node.
	 *
	 * @return stack size
	*/
	@Override
	public int getStackSize(){
		return node.getStackSize();
	}

	// ======================================================================
	// Set Stack Size
	// ======================================================================
	/**
	 * Set stack size of thread in node.
	 *
	 * @param stackSize stack size
	*/
	@Override
	public void setStackSize(int stackSize){
		node.setStackSize(stackSize);
		return;
	}

	/** Refer load average for 1min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage1Min() {
		return node.getLoadAverage()[0];
	}

	/** Refer load average for 5min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage5Min() {
		return node.getLoadAverage()[1];
	}

	/** Refer load average for 15min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage15Min() {
		return node.getLoadAverage()[2];
	}

	// ======================================================================
	// Refer Total Job Count
	// ======================================================================
	/**
	 * Refer total job count of node.
	 *
	 * @return total job count
	*/
	@Override
	public long getTotalJobCount(){
		return node.getTotalJobCount();
	}

	// ======================================================================
	// Refer Total Job Time
	// ======================================================================
	/**
	 * Refer total job time of node.
	 *
	 * @return total job time
	*/
	@Override
	public long getTotalJobTime(){
		return node.getTotalJobTime();
	}

	// ======================================================================
	// Post Job
	// ======================================================================
	/**
	 * Post specified job to node.
	 *
	 * @param text job to post
	*/
	@Override
	public void post(String text) {
		try {
			Job job = Job.parse(text);
			node.post(job);
		} catch(ParseException ex){
			logger.error("invalid job text format: " + text, ex);
			throw new IllegalArgumentException(ex.toString());
		} catch(JyroException ex){
			logger.error("fail to post job: " + text, ex);
			throw new IllegalArgumentException(ex.toString());
		} catch(RuntimeException ex){
			logger.error("fail to post job: " + text, ex);
			throw ex;
		}
		return;
	}

}
