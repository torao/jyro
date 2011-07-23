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
package org.koiroha.jyro.impl;

import org.koiroha.jyro.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// SampleWorker: Sample Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Sample worker
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
public class SampleWorker implements Worker {

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 */
	public SampleWorker() {
		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * <p>
	 * @param job job argument
	 * @return result
	 */
	@Override
	public Object exec(Job job) {
		long sum = 0;
		try { Thread.sleep(10000); } catch(InterruptedException e){/* */}
		return sum;
	}

}
