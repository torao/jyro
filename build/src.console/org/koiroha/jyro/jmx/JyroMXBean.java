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

import org.koiroha.jyro.JyroException;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroMXBean:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/16 Java SE 6
 */
@MXBean
public interface JyroMXBean {

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
	 * Refer active workers of all nodes.
	 *
	 * @return active workers
	*/
	public int getActiveWorkers();

	// ======================================================================
	// Startup Instance
	// ======================================================================
	/**
	 * Startup Jyro instance.
	 *
	 * @throws JyroException if fail to startup instance
	*/
	public void startup() throws JyroException;

	// ======================================================================
	// Shutdown Instance
	// ======================================================================
	/**
	 * Shutdown Jyro instance.
	 *
	 * @throws JyroException if fail to shutdown instance
	*/
	public void shutdown() throws JyroException;

	// ======================================================================
	// Reload Cores
	// ======================================================================
	/**
	 * Reload all cores.
	*/
	public void reload();

}
