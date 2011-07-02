/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.cell;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Process: 
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/28 Java SE 6
 */
public interface Process {

	// ======================================================================
	// 
	// ======================================================================
	/**
	 * Post specified job to this process.
	*/
	public void post(Job job);
	
}
