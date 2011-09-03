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
import java.util.*;

import javax.management.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.JyroException;
import org.koiroha.jyro.impl.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// ClusterMXBeanImpl: Cluster MXBean Implementation
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Implementation of Cluster MXBean. The this refers {@link Cluster}
 * instance by core name. It means that the same ClusterMXBean will be used
 * if cluster reloaded and change instance.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/17 Java SE 6
 */
public class ClusterMXBeanImpl extends StandardMBean implements ClusterMXBean {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(ClusterMXBeanImpl.class);

	// ======================================================================
	// JyroMXBean
	// ======================================================================
	/**
	 * JyroMXBean to refer core instance of this mxbean.
	*/
	private final JyroMXBeanImpl mxbean;

	// ======================================================================
	// Core Name
	// ======================================================================
	/**
	 * Core name of this instance.
	*/
	private final String name;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param mxbean MXBean to refer core instance
	 * @param name core name that this instance mapped to
	 * @throws NotCompliantMBeanException
	*/
	public ClusterMXBeanImpl(JyroMXBeanImpl mxbean, String name) throws NotCompliantMBeanException{
		super(ClusterMXBean.class);
		this.mxbean = mxbean;
		this.name = name;
		return;
	}

	// ======================================================================
	// Refer Name
	// ======================================================================
	/**
	 * Refer name of core.
	 *
	 * @return name
	*/
	@Override
	public String getName(){
		return getCluster().getName();
	}

	// ======================================================================
	// Refer Status
	// ======================================================================
	/**
	 * Refer status of core.
	 *
	 * @return status
	 */
	@Override
	public String getStatus(){
		return getCluster().getStatus().toString();
	}

	// ======================================================================
	// Retrieve Modified
	// ======================================================================
	/**
	 * Retrieve that whether core-dependent files are modified or not.
	 *
	 * @return true if one or more dependency files are modified
	 */
	@Override
	public boolean isModified(){
		return getCluster().isModified();
	}

	// ======================================================================
	// Refer Directory
	// ======================================================================
	/**
	 * Refer directory of core.
	 *
	 * @return directory
	*/
	@Override
	public String getDirectory(){
		return getCluster().getDirectory().getAbsolutePath();
	}

	// ======================================================================
	// Refer Uptime
	// ======================================================================
	/**
	 * Refer uptime of core.
	 *
	 * @return uptime
	*/
	@Override
	public long getUptime(){
		return getCluster().getUptime();
	}

	// ======================================================================
	// Refer Active Workers
	// ======================================================================
	/**
	 * Refer active workers of all nodes.
	 *
	 * @return active workers
	*/
	@Override
	public int getActiveWorkers(){
		int count = 0;
		for(Node node: getCluster().getNodes()){
			count += node.getThreadPool().getActiveWorkers();
		}
		return count;
	}

	/** Refer load average for 1min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage1Min() {
		double la = 0.0;
		for(Node node: getCluster().getNodes()){
			la += node.getThreadPool().getLoadAverage()[0];
		}
		return la;
	}

	/** Refer load average for 5min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage5Min() {
		double la = 0.0;
		for(Node node: getCluster().getNodes()){
			la += node.getThreadPool().getLoadAverage()[1];
		}
		return la;
	}

	/** Refer load average for 15min.
	 * @return load average
	 */
	@Override
	public double getLoadAverage15Min() {
		double la = 0.0;
		for(Node node: getCluster().getNodes()){
			la += node.getThreadPool().getLoadAverage()[2];
		}
		return la;
	}


	// ======================================================================
	// Invoke Operation
	// ======================================================================
	/**
	 * Invoke specified operation.
	 *
	 * @param actionName distributed function name
	 * @param params invocation parameters
	 * @param signature type name
	 * @return result object
	*/
	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
		logger.debug("invoke(" + actionName + "," + Arrays.toString(params) + "," + Arrays.toString(signature) + ")");
		Job job = new Job(actionName, params, null);
		try {
			getCluster().send(job);
		} catch(JyroException ex){
			throw new ReflectionException(ex);
		}
		return null;
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

		// retrieve all distributed functions
		List<Method> list = new ArrayList<Method>();
		for(Node node: getCluster().getNodes()){
			list.addAll(Arrays.asList(node.getDistributedMethods()));
		}

		// build operation info
		MBeanOperationInfo[] op = new MBeanOperationInfo[list.size()];
		for(int i=0; i<list.size(); i++){
			Method method = list.get(i);
			op[i] = NodeMXBeanImpl.getMBeanOperationInfo(method);
		}

		MBeanInfo info = super.getMBeanInfo();
		return new MBeanInfo(
			info.getClassName(),
			"The instance of cluster " + getName(),
			info.getAttributes(),
			info.getConstructors(),
			op,
			info.getNotifications());
	}

	// ======================================================================
	// Refer Cluster
	// ======================================================================
	/**
	 * Refer cluster of this instance.
	 *
	 * @return jyro cluster
	*/
	private Cluster getCluster(){
		return mxbean.getJyro().getCluster(name);
	}

}
