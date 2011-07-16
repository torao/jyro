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
	// Refer Load Average
	// ======================================================================
	/**
	 * Refer 1min load average of node.
	 *
	 * @return load average
	*/
	@Override
	public double getLoadAverage1(){
		return node.getLoadAverage()[0];
	}

	// ======================================================================
	// Refer Load Average
	// ======================================================================
	/**
	 * Refer 5min load average of node.
	 *
	 * @return load average
	*/
	@Override
	public double getLoadAverage5(){
		return node.getLoadAverage()[1];
	}

	// ======================================================================
	// Refer Load Average
	// ======================================================================
	/**
	 * Refer 15min load average of node.
	 *
	 * @return load average
	*/
	@Override
	public double getLoadAverage15(){
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
	 * @throws ParseException inbalid job text
	*/
	@Override
	public void post(String text) throws ParseException{
		Job job = Job.parse(text);
		node.post(job);
		return;
	}

}
