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

import java.util.concurrent.ThreadPoolExecutor;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// PooledThreadCell: Pooled-Thread Cell
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Cell class that uses thread pool for worker threads.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/30 Java SE 6
 */
public class PooledThreadCell implements Cell {

	// ======================================================================
	// Core Pool Size
	// ======================================================================
	/**
	 * The number of threads to keep in the pool.
	 */
	private int corePoolSize = 5;

	// ======================================================================
	// Max Pool Size
	// ======================================================================
	/**
	 * The number of threads to keep in the pool.
	 */
	private int maximumPoolSize = 10;

	// ======================================================================
	// Max Pool Size
	// ======================================================================
	/**
	 * The number of threads to keep in the pool.
	 */
	private ThreadPoolExecutor executor = null;

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 * <p>
	 */
	public PooledThreadCell() {
		return;
	}

	// ======================================================================
	// Start Cell
	// ======================================================================
	/**
	 * Start job execution of this cell.
	*/
	public void start(){
		return;
	}

	// ======================================================================
	// Stop Cell
	// ======================================================================
	/**
	 * Stop job execution of this cell.
	*/
	public void stop(){
		return;
	}

	// ======================================================================
	// 
	// ======================================================================
	/**
	 * 
	 * <p>
	 * @param job
	 */
	@Override
	public void post(Job job) {
		// TODO Auto-generated method stub

	}

}
