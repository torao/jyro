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
package org.koiroha.jyro.env;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.impl.*;
import org.koiroha.jyro.jmx.JyroMXBeanImpl;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroPlatform:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public class JyroPlatform {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JyroPlatform.class);

	// ======================================================================
	// Jyro MXBean
	// ======================================================================
	/**
	 * MXBean to manage Jyro instance.
	 */
	private final JyroMXBeanImpl mxbean;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param name instance name
	 * @param dir home directory
	 * @param parent default class loader
	 * @param prop initialization property
	 * @throws JyroException if fail to initialize instance
	 */
	public JyroPlatform(String name, File dir, ClassLoader parent, Properties prop) throws JyroException {
		mxbean = new JyroMXBeanImpl(name, dir, parent, null);
		return;
	}

	// ======================================================================
	// Refer Instance
	// ======================================================================
	/**
	 * Refer Jyro instance of this platform.
	 *
	 * @return jyro implementation
	 */
	public JyroImpl getJyro() {
		return mxbean.getJyro();
	}

	// ======================================================================
	// Post Job
	// ======================================================================
	/**
	 * Post specified job to node.
	 *
	 * @param core core name
	 * @param node node name
	 * @param job job to post
	 * @throws JyroException if fail to post job
	 */
	public void post(String core, String node, Job job) throws JyroException {
		JyroImpl j = mxbean.getJyro();
		ClusterImpl c = j.getCluster(core);
		NodeImpl n = c.getNode(node);
		n.post(job);
		return;
	}

	// ======================================================================
	// Startup
	// ======================================================================
	/**
	 * Startup all services on this platform.
	 *
	 * @throws JyroException if fail to startup
	 */
	public void startup() throws JyroException {
		try {
			mxbean.register();
			mxbean.startup();
		} catch(Exception ex){
			throw new JyroException(ex);
		}
		return;
	}

	// ======================================================================
	// Shutdown
	// ======================================================================
	/**
	 * Shutdown all services on this platform.
	 */
	public void shutdown() {
		try {
			mxbean.shutdown();
			mxbean.unregister();
		} catch(Exception ex){
			logger.fatal("fail to shutdown jyro", ex);
		}
		return;
	}

}
