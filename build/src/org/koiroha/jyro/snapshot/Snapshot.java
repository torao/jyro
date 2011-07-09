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
package org.koiroha.jyro.snapshot;

import java.io.Serializable;
import java.util.*;

import org.koiroha.jyro.Jyro;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Snapshot: Snapshot Utility
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Utility class to retrieve system snapshot of {@link Jyro}.
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/04 Java SE 6
 */
public final class Snapshot implements Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * The default constructor is hidden in class.
	 */
	private Snapshot() {
		return;
	}

	// ======================================================================
	// Retrieve Snapshot
	// ======================================================================
	/**
	 * Retrieve system graph of specified Jyro instance.
	 *
	 * @param jyro Jyro instance
	 * @return system graph
	 */
	public static Iterable<Server> getGraph(Jyro jyro){
		return null;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Server: Server Info
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 */
	public static class Server implements Serializable {
		/** Serial version UID of this class. */
		private static final long serialVersionUID = 1L;

		/**
		 * Name of this server. This value may specify the hostname or IP
		 * address.
		 */
		public final String name;

		/**
		 * The number of server port.
		 */
		public final int port;

		/**
		 * Jyro instances working on this server.
		 */
		public final List<Instance> instances = new ArrayList<Instance>();

		/**
		 * @param name server name
		 * @param port server port
		 */
		public Server(String name, int port) {
			this.name = name;
			this.port = port;
			return;
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Instance: Jyro Instance
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * The instance of jyro.
	 */
	public static class Instance implements Serializable {
		/** Serial version UID of this class. */
		private static final long serialVersionUID = 1L;

		/**
		 * Home directory of this Jyro instance.
		 */
		public final File home;

		/**
		 * Name of this server. This value may specify the hostname or IP
		 * address.
		 */
		public final String name;

		/**
		 * The number of server port.
		 */
		public final int port;

		/**
		 * @param name server name
		 * @param port server port
		 */
		public Instance(String name, int port) {
			this.name = name;
			this.port = port;
			return;
		}

	}

}
