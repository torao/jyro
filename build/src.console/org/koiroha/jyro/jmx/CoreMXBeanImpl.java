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
import org.koiroha.jyro.impl.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// CoreMXBeanImpl: Core MXBean Implementation
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Implementation of Core MXBean. The this refers {@link JyroCore} instance
 * by core name. It means that the same CoreMXBean will be used if core
 * reloaded and change instance.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/17 Java SE 6
 */
public class CoreMXBeanImpl implements CoreMXBean {

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
	public CoreMXBeanImpl(JyroMXBeanImpl mxbean, String name){
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
		return getCore().getName();
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
		return getCore().getStatus().toString();
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
		return getCore().isModified();
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
		return getCore().getDirectory().getAbsolutePath();
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
		return getCore().getUptime();
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
		for(Node node: getCore().getNodes()){
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
		for(Node node: getCore().getNodes()){
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
		for(Node node: getCore().getNodes()){
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
		for(Node node: getCore().getNodes()){
			la += node.getLoadAverage()[2];
		}
		return la;
	}

	// ======================================================================
	// Refer Core
	// ======================================================================
	/**
	 * Refer core of this instance.
	 *
	 * @return jyro core
	*/
	private JyroCore getCore(){
		return mxbean.getJyro().getCore(name);
	}

}
