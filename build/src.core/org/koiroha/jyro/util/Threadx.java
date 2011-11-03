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

import org.apache.log4j.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Thread: Thread Utility Functions
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The utility class to operate threads.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/10 Java SE 6
 */
public final class Threadx {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Threadx.class);

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Constructor hidden in class.
	 */
	private Threadx() {
		return;
	}

	// ======================================================================
	// Kill Thread
	// ======================================================================
	/**
	 * Stop specified thread forcely. This method will try to interrupt the
	 * thread and wait milliseconds first to stop gracefully. If still alive
	 * after interruption and wait, {@code Thread.stop()} will called
	 * forcely.
	 *
	 * @param thread thread to stop
	 * @param millis waittime after interrupt as milliseconds
	 */
	public static void kill(java.lang.Thread thread, long millis){
		if(thread.isAlive()){

			// interrupt thread and wait finish
			thread.interrupt();
			if(millis > 0){
				try {
					thread.join(millis);
				} catch(InterruptedException ex){/* */}
			}

			// force stop thread if still alive
			if(thread.isAlive()){
				logger.warn("force stopping thread " + thread.getName() + ":" + thread.getId());
				forceKill(thread);
			}
		}
		return;
	}

	// ======================================================================
	// Stop Thread
	// ======================================================================
	/**
	 * Stop specified thread forcely. This method will call
	 * {@code Thread.stop()} directly.
	 *
	 * @param thread thread to stop
	 */
	@SuppressWarnings("deprecation")
	public static void forceKill(java.lang.Thread thread){

		// output warning log and stacktrace
		if(logger.isEnabledFor(Level.WARN)){
			logger.warn("trying to stop thread \"" + thread.getName() + "\" (#" + thread.getId() + ") forcely");
			logger.warn(getStackTrace(thread));
		}

		// stop thread forcely
		thread.stop();
		return;
	}

	// ======================================================================
	// Retrieve StackTrace
	// ======================================================================
	/**
	 * Retrieve stacktrace of specified thread for diagnostic.
	 *
	 * @param thread thread to retrieve stacktrace
	 * @return stacktrace string
	 */
	public static String getStackTrace(java.lang.Thread thread){
		StringBuilder buffer = new StringBuilder();
		for(StackTraceElement e: thread.getStackTrace()){
			String pos = "Compiled Code";
			if(e.getFileName() != null){
				pos = e.getFileName() + ':' + e.getLineNumber();
			}
			buffer.append(String.format(
				"\tat %s.%s(%s)%n",
				e.getClassName(), e.getMethodName(), pos));
		}
		return buffer.toString();
	}

}
