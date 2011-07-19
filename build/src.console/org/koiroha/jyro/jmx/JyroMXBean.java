/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.jmx;

import javax.management.MXBean;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroMXBean: Jyro MXBean
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * MXBean for Jyro instance.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/16 Java SE 6
 */
@MXBean
public interface JyroMXBean {

	// ======================================================================
	// Domain Name
	// ======================================================================
	/**
	 * The domain name for Jyro on JMX.
	 */
	public static final String DOMAIN = "org.koiroha.jyro";

	// ======================================================================
	// Refer Name
	// ======================================================================
	/**
	 * Refer human-readable name of instance.
	 *
	 * @return name
	 */
	public String getName();

	// ======================================================================
	// Retrieve Home Directory
	// ======================================================================
	/**
	 * Retrieve home directory of jyro instance.
	 *
	 * @return home directory
	 */
	public String getDirectory();

	// ======================================================================
	// Retrieve Core Count
	// ======================================================================
	/**
	 * Retrieve core count of jyro instance.
	 *
	 * @return core count
	 */
	public int getCoreCount();

	// ======================================================================
	// Refer Active Workers
	// ======================================================================
	/**
	 * Refer total active worker count of all nodes.
	 *
	 * @return active workers
	*/
	public int getActiveWorkers();

	// ======================================================================
	// Startup Instance
	// ======================================================================
	/**
	 * Startup Jyro instance.
	*/
	public void startup();

	// ======================================================================
	// Shutdown Instance
	// ======================================================================
	/**
	 * Shutdown Jyro instance.
	*/
	public void shutdown();

	// ======================================================================
	// Reload Cores
	// ======================================================================
	/**
	 * Reload all cores.
	*/
	public void reload();

}
