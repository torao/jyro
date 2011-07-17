/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.util;

import org.apache.log4j.Logger;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Thread: Thread Utility Functions
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/10 Java SE 6
 */
public final class Thread {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Thread.class);

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Constructor hidden in class.
	 */
	private Thread() {
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Constructor hidden in class.
	 *
	 * @param thread thread to stop
	 * @param millis
	 */
	public static void kill(java.lang.Thread thread, long millis){
		if(thread.isAlive()){

			// interrupt thread and wait finish
			thread.interrupt();
			try {
				thread.join(millis);
			} catch(InterruptedException ex){/* */}

			// force stop thread if still alive
			if(thread.isAlive()){
				logger.warn("force stopping thread " + thread.getName() + ":" + thread.getId());
				stop(thread);
			}
		}
		return;
	}

	// ======================================================================
	// Stop Thread
	// ======================================================================
	/**
	 * Stop specified thread.
	 *
	 * @param thread thread to stop
	 */
	@SuppressWarnings("deprecation")
	private static void stop(java.lang.Thread thread){
		thread.stop();
		return;
	}

}
