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

import java.net.InetAddress;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Server: Jyro
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @author takami torao
 */
public class Server {

	// ========================================================================
	// Log Output
	// ========================================================================
	/**
	 * Log output of this class.
	 */
	private Logger logger = Logger.getLogger(Server.class);

	// ========================================================================
	// Default Server Port
	// ========================================================================
	/**
	 * Default server port {@value} for Jyro Console that accessed by browser.
	 */
	public static final int DEFAULT_PORT = 2648;

	// ========================================================================
	// Interface
	// ========================================================================
	/**
	 * Network interface to listen.
	 */
	private InetAddress bindAddress = null;

	// ========================================================================
	// Server Port
	// ========================================================================
	/**
	 * Listening port of console for this server.
	 */
	private int port = DEFAULT_PORT;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 */
	public Server() {
		return;
	}

	// ======================================================================
	// Retrieve Port
	// ======================================================================
	/**
	 * Retrieve server port
	 *
	 * @return server port
	 */
	public int getPort() {
		return port;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
