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
package org.koiroha.jyro.util;

import java.io.*;

import org.apache.log4j.Logger;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// IO:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
public final class IO {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(IO.class);

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Constructor is hidden in class.
	 */
	private IO() {
		return;
	}

	// ======================================================================
	// Close Stream
	// ======================================================================
	/**
	 * Close specified stream quietly.
	 *
	 * @param o stream to close
	 */
	public static void close(Closeable o){
		if(o != null){
			try {
				o.close();
			} catch(IOException ex){
				logger.warn("fail to close stream", ex);
			}
		}
		return;
	}

}
