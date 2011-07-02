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
// Cell: 
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The class of process unit that has job queue and worker threads.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/28 Java SE 6
 */
public interface Cell {

	// ======================================================================
	// Start Cell
	// ======================================================================
	/**
	 * Start job execution of this cell.
	*/
	public void start();

	// ======================================================================
	// Stop Cell
	// ======================================================================
	/**
	 * Stop job execution of this cell.
	*/
	public void stop();

	// ======================================================================
	// Enqueue Job
	// ======================================================================
	/**
	 * Enqueue job to this cell.
	 * 
	 * @param job 
	*/
	public void post(Job<?> job);

}
