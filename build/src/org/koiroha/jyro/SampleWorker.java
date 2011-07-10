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
package org.koiroha.jyro;



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
	 * @param args
	 * @return result
	 */
	@Override
	public Object exec(Object... args) {
		long sum = 0;
		for(Object o: args){
			sum += ((Long)o);
		}
		try { Thread.sleep(100); } catch(InterruptedException e){/* */}
		return sum;
	}

}
