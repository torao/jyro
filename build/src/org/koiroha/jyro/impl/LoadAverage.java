/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.impl;

import java.util.*;


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// LoadAverage: Unix-like Load Average Calculator
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Calculate unix-like load average for specified queue.
 *
 * @author takami torao
 */
class LoadAverage extends TimerTask {

	// ======================================================================
	// Queue
	// ======================================================================
	/**
	 * Job queue to calculate load average.
	 */
	private final Collection<?> queue;

	// ======================================================================
	// Load Value
	// ======================================================================
	/**
	 * The number of enqueued jobs per second.
	 */
	private final int[] load = new int[15 * 60];

	// ======================================================================
	// Time
	// ======================================================================
	/**
	 * The timestamp for each load array.
	 */
	private final long[] time = new long[load.length];

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Construct load average calculator for specified job queue.
	 *
	 * @param queue job queue
	 */
	public LoadAverage(Collection<?> queue) {
		this.queue = queue;
		return;
	}

	// ======================================================================
	// Sampling Load Average
	// ======================================================================
	/**
	 * Sampling current size of enqueued jobs to internal load array.
	 */
	@Override
	public void run(){
		synchronized(load){
			System.arraycopy(load, 0, load, 1, load.length-1);
			System.arraycopy(time, 0, time, 1, time.length-1);
			time[0] = System.currentTimeMillis();
			load[0] = queue.size();
		}
		return;
	}

	// ======================================================================
	// Start Calculation
	// ======================================================================
	/**
	 * Start to calculate enqueued size of jobs.
	 */
	public void start(){
		CoreConfig.TIMER.scheduleAtFixedRate(this, 0, 1000);
		return;
	}

	// ======================================================================
	// Stop Calculation
	// ======================================================================
	/**
	 * Stop to calculate enqueued size of jobs.
	 */
	public void stop(){
		this.cancel();
		return;
	}

	// ======================================================================
	// Retrieve Load Average
	// ======================================================================
	/**
	 * Retrieve load averages per 1, 5, and 15 minutes.
	 *
	 * @return load average (1min, 5min, 15min)
	 */
	public double[] getLoadAverage(){
		int sum1 = 0;
		int cnt1 = 0;
		int sum5 = 0;
		int cnt5 = 0;
		int sum15 = 0;
		int cnt15 = 0;
		synchronized(load){
			long now = System.currentTimeMillis();
			for(int i=0; i<load.length && load[i]>=0; i++){
				long diff = now - time[i];
				if(diff < 1 * 60 * 1000){
					sum1 += load[i];
					cnt1 ++;
				}
				if(diff < 5 * 60 * 1000){
					sum5 += load[i];
					cnt5 ++;
				}
				if(diff < 15 * 60 * 1000){
					sum15 += load[i];
					cnt15 ++;
				}
			}
		}
		return new double[]{
			(cnt1==0)? Double.NaN: ((double)sum1 / cnt1),
			(cnt5==0)? Double.NaN: ((double)sum5 / cnt5),
			(cnt15==0)? Double.NaN: ((double)sum15 / cnt15)
		};
	}

}
