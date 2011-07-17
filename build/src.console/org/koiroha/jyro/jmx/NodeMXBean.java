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
	// Refer Maximum Workers
	// ======================================================================
	/**
	 * Refer maximum workers of node.
	 *
	 * @return maximum workers
	*/
	public int getMaximumWorkers();

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
	// Refer Stack Size
	// ======================================================================
	/**
	 * Refer stack size of thread in node.
	 *
	 * @return stack size
	*/
	public int getStackSize();

	// ======================================================================
	// Refer Load Average
	// ======================================================================
	/**
	 * Refer 1min load average of node.
	 *
	 * @return load average
	*/
	public double getLoadAverage1();

	// ======================================================================
	// Refer Load Average
	// ======================================================================
	/**
	 * Refer 5min load average of node.
	 *
	 * @return load average
	*/
	public double getLoadAverage5();

	// ======================================================================
	// Refer Load Average
	// ======================================================================
	/**
	 * Refer 15min load average of node.
	 *
	 * @return load average
	*/
	public double getLoadAverage15();

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
