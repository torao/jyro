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

import javax.management.MXBean;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// NodeMXBean:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/17 Java SE 6
 */
@MXBean
public interface NodeMXBean {

	// ======================================================================
	// Refer ID
	// ======================================================================
	/**
	 * Refer id of node.
	 *
	 * @return ID
	*/
	public String getId();

	// ======================================================================
	// Refer Waiting Jobs
	// ======================================================================
	/**
	 * Refer waiting jobs of node.
	 *
	 * @return waiting jobs
	*/
	public int getWaitingJobs();

	// ======================================================================
	// Refer Active Workers
	// ======================================================================
	/**
	 * Refer active workers of node.
	 *
	 * @return active workers
	*/
	public int getActiveWorkers();

	// ======================================================================
	// Refer Minimum Workers
	// ======================================================================
	/**
	 * Refer minimum workers of node.
	 *
	 * @return minimum workers
	*/
	public int getMinimumWorkers();

	// ======================================================================
	// Set Minimum Workers
	// ======================================================================
	/**
	 * Set minimum workers of node.
	 *
	 * @param min minimum workers
	*/
	public void setMinimumWorkers(int min);

	// ======================================================================
	// Refer Maximum Workers
	// ======================================================================
	/**
	 * Refer maximum workers of node.
	 *
	 * @return maximum workers
	*/
	public int getMaximumWorkers();

	// ======================================================================
	// Set Maximum Workers
	// ======================================================================
	/**
	 * Set maximum workers of node.
	 *
	 * @param max maximum workers
	*/
	public void setMaximumWorkers(int max);

	// ======================================================================
	// Retrieve Thread Priority
	// ======================================================================
	/**
	 * Retrieve priority of worker thread on this node.
	 *
	 * @return thread priority
	 */
	public int getPriority();

	// ======================================================================
	// Set Thread Priority
	// ======================================================================
	/**
	 * Set priority of worker thread on this node.
	 *
	 * @param priority thread priority that defined in class {@link Thread}
	 */
	public void setPriority(int priority);

	// ======================================================================
	// Refer Daemon
	// ======================================================================
	/**
	 * Refer daemon of node.
	 *
	 * @return true if daemon
	*/
	public boolean isDaemon();

	// ======================================================================
	// Set Daemon
	// ======================================================================
	/**
	 * Set daemon of node.
	 *
	 * @param daemon true if daemon
	*/
	public void setDaemon(boolean daemon);

	// ======================================================================
	// Refer Stack Size
	// ======================================================================
	/**
	 * Refer stack size of thread in node.
	 *
	 * @return stack size
	*/
	public int getStackSize();

	// ======================================================================
	// Set Stack Size
	// ======================================================================
	/**
	 * Set stack size of thread in node.
	 *
	 * @param stackSize stack size
	*/
	public void setStackSize(int stackSize);


	/** Refer load average for 1min.
	 * @return load average
	 */
	public double getLoadAverage1Min();

	/** Refer load average for 5min.
	 * @return load average
	 */
	public double getLoadAverage5Min();

	/** Refer load average for 15min.
	 * @return load average
	 */
	public double getLoadAverage15Min();

	// ======================================================================
	// Refer Total Job Count
	// ======================================================================
	/**
	 * Refer total job count of node.
	 *
	 * @return total job count
	*/
	public long getTotalJobCount();

	// ======================================================================
	// Refer Total Job Time
	// ======================================================================
	/**
	 * Refer total job time of node.
	 *
	 * @return total job time
	*/
	public long getTotalJobTime();

	// ======================================================================
	// Post Job
	// ======================================================================
	/**
	 * Post specified job to node.
	 *
	 * @param text job to post
	*/
	public void post(String text);

}
