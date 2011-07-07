/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro;

import java.util.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// LoadAverage:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Calculate unix-like load average for specified job queue.
 *
 * @author takami torao
 */
public class LoadAverage {

	// ======================================================================
	// Calculation Timer
	// ======================================================================
	/**
	 * Calculation timer for all load average in system.
	 */
	private static final Timer TIMER = new Timer("LoadAverage Calculation", true);

	// ======================================================================
	// Timer Task
	// ======================================================================
	/**
	 * Called per 1 second and store current size of enqueued jobs.
	 */
	private final TimerTask task = new TimerTask(){
		@Override
		public void run() { store(); }
	};

	// ======================================================================
	// Queue
	// ======================================================================
	/**
	 * Job queue.
	 */
	private final Collection<Object> queue;

	// ======================================================================
	// Load Value
	// ======================================================================
	/**
	 * Load values per second.
	 */
	private final int[] load = new int[15 * 60];

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Construct load average calculator for specified job queue.
	 *
	 * @param queue job queue
	 */
	public LoadAverage(Collection<Object> queue) {
		this.queue = queue;
		return;
	}

	// ======================================================================
	// Start Calculation
	// ======================================================================
	/**
	 * Start to calculate enqueued size of jobs.
	 */
	public void start(){
		TIMER.scheduleAtFixedRate(task, 0, 1000);
		return;
	}

	// ======================================================================
	// Stop Calculation
	// ======================================================================
	/**
	 * Stop to calculate enqueued size of jobs.
	 */
	public void stop(){
		task.cancel();
		return;
	}

	// ======================================================================
	// Retrieve Load Average
	// ======================================================================
	/**
	 * Retrieve load averages per 1, 5, and 15 minutes.
	 *
	 * @return load average
	 */
	public double[] get(){
		int s1 = 0;
		int s5 = 0;
		int s15 = 0;
		int count = 0;
		synchronized(load){
			for(int i=0; i<load.length && load[i]>=0; i++){
				if(i < 60){
					s1 += load[i];
				}
				if(i < 5 * 60){
					s5 += load[i];
				}
				s15 += load[i];
				count ++;
			}
		}
		if(count == 0){
			return new double[]{ 0.0, 0.0, 0.0 };
		}
		return new double[]{ s1 / count, s5 / count, s15 / count };

	}

	// ======================================================================
	// Calculate Load Average
	// ======================================================================
	/**
	 * Store current size of enqueued jobs to load array.
	 */
	private void store(){
		synchronized(load){
			System.arraycopy(load, 0, load, 1, load.length-1);
			load[0] = queue.size();
		}
		return;
	}

}
