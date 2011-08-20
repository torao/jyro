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

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.management.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.impl.*;



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
public class NodeMXBeanImpl extends StandardMBean implements NodeMXBean {

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
	public NodeMXBeanImpl(JyroMXBeanImpl mxbean, String core, String node) throws NotCompliantMBeanException{
		super(NodeMXBean.class);
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
	// Refer Active Workers
	// ======================================================================
	/**
	 * Refer active workers of node.
	 *
	 * @return active workers
	*/
	@Override
	public int getActiveWorkers(){
		return getNode().getThreadPool().getActiveWorkers();
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
		return getNode().getThreadPool().getMinimumWorkers();
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
		getNode().getThreadPool().setMinimumWorkers(min);
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
		return getNode().getThreadPool().getMaximumWorkers();
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
		getNode().getThreadPool().setMaximumWorkers(max);
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
		return getNode().getThreadPool().getPriority();
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
		getNode().getThreadPool().setPriority(priority);
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
		return getNode().getThreadPool().isDaemon();
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
		getNode().getThreadPool().setDaemon(daemon);
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
		return getNode().getThreadPool().getStackSize();
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
		getNode().getThreadPool().setStackSize(stackSize);
		return;
	}

	/** Refer load average for 1min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage1Min() {
		return getNode().getThreadPool().getLoadAverage()[0];
	}

	/** Refer load average for 5min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage5Min() {
		return getNode().getThreadPool().getLoadAverage()[1];
	}

	/** Refer load average for 15min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage15Min() {
		return getNode().getThreadPool().getLoadAverage()[2];
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
	// Retrieve MBeanInfo
	// ======================================================================
	/**
	 * Retrieve node for this mxbean.
	 *
	 * @return MBeanInfo instance for this node
	*/
	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
		logger.debug("invoke(" + actionName + ",{" + Arrays.toString(params) + "},{" + Arrays.toString(signature) + "})");
		Job job = new Job(actionName, params, null);
		return getNode().execute(job);
	}

	// ======================================================================
	// Retrieve MBeanInfo
	// ======================================================================
	/**
	 * Retrieve node for this mxbean.
	 *
	 * @return MBeanInfo instance for this node
	*/
	@Override
	public MBeanInfo getMBeanInfo() {
		logger.debug("getMBeanInfo()");

		// build operation info
		Method[] method = getNode().getDistributedMethods();
		MBeanOperationInfo[] op = new MBeanOperationInfo[method.length];
		for(int i=0; i<method.length; i++){
			op[i] = new MBeanOperationInfo("", method[i]);
		}

		MBeanInfo info = super.getMBeanInfo();
		return new MBeanInfo(
			info.getClassName(),
			"Node",
			info.getAttributes(),
			info.getConstructors(),
			op,
			info.getNotifications());
	}

	// ======================================================================
	// Retrieve MBeanInfo
	// ======================================================================
	/**
	 * Retrieve node for this mxbean.
	 *
	 * @return MBeanInfo instance for this node
	*/
	public static MBeanOperationInfo getMBeanOperationInfo(Method method) {
		Distribute dist = method.getAnnotation(Distribute.class);

		// build MBean parameter info
		Class<?>[] params = method.getParameterTypes();
		String[] paramNames = dist.params();
		MBeanParameterInfo[] paramInfo = new MBeanParameterInfo[params.length];
		for(int i=0; i<params.length; i++){
			String name = paramNames[i];
			String type = params[i].getCanonicalName();
			String desc = "";
			paramInfo[i] = new MBeanParameterInfo(name, type, desc);
		}

		// build MBean Operation info
		String name = Jyro.getFunctionName(method);
		String desc = "";
		String type = method.getReturnType().getSimpleName();
		return new MBeanOperationInfo(name, desc, paramInfo, type, MBeanOperationInfo.ACTION);
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
