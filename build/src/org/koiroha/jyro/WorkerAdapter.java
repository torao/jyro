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

import java.net.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// WorkerAdapter: Node Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/02 Java SE 6
 */
public class WorkerAdapter {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	*/
	private static final Logger logger = Logger.getLogger(WorkerAdapter.class);

	// ======================================================================
	// Class Loader
	// ======================================================================
	/**
	 * Class loader that this worker use.
	*/
	private final URLClassLoader loader;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * 
	*/
	public WorkerAdapter(URL[] urls){
		this.loader = new URLClassLoader(urls);
		return;
	}

	// ======================================================================
	// Execute Process
	// ======================================================================
	/**
	 * Execute this process with specified arguments. This method called in
	 * multi-thread environment.
	 * 
	 * @param args arguments
	 * @return result
	*/
	public Object exec(Object... args){
		return null;
	}

}
