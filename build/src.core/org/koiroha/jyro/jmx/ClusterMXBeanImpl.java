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

import javax.management.*;

import org.koiroha.jyro.*;
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
public class ClusterMXBeanImpl implements ClusterMXBean {

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
	*/
	public ClusterMXBeanImpl(JyroMXBeanImpl mxbean, String name){
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
			count += node.getActiveWorkers();
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
			la += node.getLoadAverage()[0];
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
			la += node.getLoadAverage()[1];
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
			la += node.getLoadAverage()[2];
		}
		return la;
	}

	// ======================================================================
	// Refer Uptime
	// ======================================================================
	/**
	 * Refer uptime of core.
	 *
	 * @return uptime
	*/
	private void send0(String func, Object... args) throws JyroException{
		Job job = new Job(func, args, null);
		getCluster().send(job);
		return;
	}
	@Override
	public void send(String func) throws JyroException { send0(func); }
	@Override
	public void send(String func, String a1) throws JyroException { send0(func, a1); }
	@Override
	public void send(String func, String a1, String a2) throws JyroException { send0(func, a1, a2); }
	@Override
	public void send(String func, String a1, String a2, String a3) throws JyroException { send0(func, a1, a2, a3); }
	@Override
	public void send(String func, String a1, String a2, String a3, String a4) throws JyroException { send0(func, a1, a2, a3, a4); }

	// ======================================================================
	// Refer Operation Info
	// ======================================================================
	/**
	 * TODO How to describe MBeanOperationInfo?
	 *
	 * @return operation information
	 */
	protected MBeanOperationInfo[] createMBeanOperationInfo(){
		return new MBeanOperationInfo[] {
			new MBeanOperationInfo("post", "post specified job to this core", new MBeanParameterInfo[]{
				new MBeanParameterInfo("nodeId", "String", "node id to post job"),
				new MBeanParameterInfo("job", "String", "job content"),
			}, "void", MBeanOperationInfo.ACTION),
		};
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
