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
import org.koiroha.jyro.impl.Node;



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
	// JyroMXBeanImpl
	// ======================================================================
	/**
	 * JyroMXBeanImpl
	*/
	private final JyroMXBeanImpl mxbean;

	// ======================================================================
	// Core Name
	// ======================================================================
	/**
	 * The core name of this node belong to.
	*/
	private final String core;

	// ======================================================================
	// Node ID
	// ======================================================================
	/**
	 * The node id of this node.
	*/
	private final String node;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 *
	 * @param mxbean instance of Jyro MXBean
	 * @param core core of specified node belong to
	 * @param node node to management this MXBean
	*/
	public NodeMXBeanImpl(JyroMXBeanImpl mxbean, String core, String node){
		this.mxbean = mxbean;
		this.core = core;
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
		return getNode().getId();
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 *
	 * @return
	*/
	@Override
	public String[] getFunctions(){
		return getNode().getFunctions();
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
		return getNode().getActiveWorkers();
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
		return getNode().getMinimumWorkers();
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
		getNode().setMinimumWorkers(min);
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
		return getNode().getMaximumWorkers();
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
		getNode().setMaximumWorkers(max);
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
	@Override
	public int getPriority(){
		return getNode().getPriority();
	}

	// ======================================================================
	// Set Thread Priority
	// ======================================================================
	/**
	 * Set priority of worker thread on this node.
	 *
	 * @param priority thread priority that defined in class {@link Thread}
	 */
	@Override
	public void setPriority(int priority){
		getNode().setPriority(priority);
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
		return getNode().isDaemon();
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
		getNode().setDaemon(true);
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
		return getNode().getStackSize();
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
		getNode().setStackSize(stackSize);
		return;
	}

	/** Refer load average for 1min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage1Min() {
		return getNode().getLoadAverage()[0];
	}

	/** Refer load average for 5min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage5Min() {
		return getNode().getLoadAverage()[1];
	}

	/** Refer load average for 15min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage15Min() {
		return getNode().getLoadAverage()[2];
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
		return getNode().getTotalJobCount();
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
		return getNode().getTotalJobTime();
	}

	// ======================================================================
	// Retrieve Node
	// ======================================================================
	/**
	 * Retrieve node for this mxbean.
	 *
	 * @return node
	*/
	private Node getNode(){
		return mxbean.getJyro().getCluster(core).getNode(node);
	}

}
