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

import java.beans.ConstructorProperties;
import java.io.Serializable;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// LoadAverage:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/18 Java SE 6
 */
public class LoadAverage implements Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Load Average
	// ======================================================================
	/**
	 * Load average of 1, 5, 15 min.
	 */
	private final double[] loadAverage = new double[3];

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param la1 load average for 1min
	 * @param la5 load average for 5min
	 * @param la15 load average for 15 min
	 */
	@ConstructorProperties({"loadAverage1Min", "loadAverage5Min", "loadAverage15Min"})
	public LoadAverage(double la1, double la5, double la15) {
		loadAverage[0] = la1;
		loadAverage[1] = la5;
		loadAverage[2] = la15;
		return;
	}

	/** Refer load average for 1min.
	 * @return load average
	 */
	public double getLoadAverage1Min() {
		return loadAverage[0];
	}

	/** Refer load average for 5min.
	 * @return load average
	 */
	public double getLoadAverage5Min() {
		return loadAverage[1];
	}

	/** Refer load average for 15min.
	 * @return load average
	 */
	public double getLoadAverage15Min() {
		return loadAverage[2];
	}
}
