/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.filters;

import java.sql.*;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.impl.Job;
import org.koiroha.jyro.util.IO;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// TransactionFilter: Filter for Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/03 Java SE 6
*/
public abstract class Transaction implements WorkerFilter {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Transaction.class);

	// ======================================================================
	// Thread Local
	// ======================================================================
	/**
	 * Thread load variable to store connection.
	*/
	private static final ThreadLocal<DataSource> DATASOURCE = new ThreadLocal<DataSource>();

	// ======================================================================
	// Thread Local
	// ======================================================================
	/**
	 * Thread load variable to store connection.
	*/
	private static final ThreadLocal<Connection> CONNECTION = new ThreadLocal<Connection>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	*/
	protected Transaction() {
		return;
	}

	// ======================================================================
	// Refer DataSource
	// ======================================================================
	/**
	 * Refer DataSource
	 *
	*/
	protected abstract DataSource getDataSource();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	*/
	@Override
	public Object filter(Job job, Next hop) throws JyroException {
		boolean success = false;
		try {
			DATASOURCE.set(getDataSource());
			Object result = hop.execute(job);
			success = true;
			return result;
		} finally {
			finish(success);
			IO.close(CONNECTION.get());
			CONNECTION.set(null);
		}
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	*/private void finish(boolean success) throws JyroException {
		Connection con = CONNECTION.get();
		if(con == null){
			return;
		}

		// try commit or rollback
		try {
			if(success){
				con.commit();
			} else {
				con.rollback();
			}
		} catch(SQLException ex){
			logger.error("fail to close database connection", ex);
			throw new JyroException(ex);
		}
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	*/
	public static Connection getConnection() throws SQLException {
		Connection con = CONNECTION.get();
		if(con == null){
			DataSource ds = DATASOURCE.get();
			con = ds.getConnection();
		}
		return con;
	}

}
