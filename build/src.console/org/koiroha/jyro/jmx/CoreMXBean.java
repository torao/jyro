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
// Core:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/17 Java SE 6
 */
@MXBean
public interface CoreMXBean {

	// ======================================================================
	// Refer Name
	// ======================================================================
	/**
	 * Refer name of core.
	 *
	 * @return name
	*/
	public String getName();

	// ======================================================================
	// Refer Directory
	// ======================================================================
	/**
	 * Refer directory of core.
	 *
	 * @return directory
	*/
	public String getDirectory();

	// ======================================================================
	// Refer Uptime
	// ======================================================================
	/**
	 * Refer uptime of core.
	 *
	 * @return uptime
	*/
	public long getUptime();

	// ======================================================================
	// Refer Active Workers
	// ======================================================================
	/**
	 * Refer active workers of all nodes.
	 *
	 * @return active workers
	*/
	public int getActiveWorkers();

	/** Refer load average for 1min.
	 * @return load average
	 */
	public double getLoadAverage1Min();

	/** Refer load average for 5min.
	 * @return load average
	 */
	public double getLoadAverage5Min();

	/** Refer load average for 15min.
	 * @return load average
	 */
	public double getLoadAverage15Min();

}
