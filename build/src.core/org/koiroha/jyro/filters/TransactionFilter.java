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

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.impl.Job;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// TransactionFilter: Filter for Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/03 Java SE 6
*/
public class TransactionFilter implements WorkerFilter {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(TransactionFilter.class);

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	*/
	public TransactionFilter() {
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	*/
	@Override
	public Object filter(Job job, Next hop) throws JyroException {
		boolean success = false;
		Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost/jyro", "mysql", "mysql");
			con.setAutoCommit(false);
			Object result = hop.execute(job);
			success = true;
			return result;
		} catch(SQLException ex){

		} finally {
			try {
				if(con != null){
					if(success){
						con.commit();
					} else {
						con.rollback();
					}
				}
			} catch(SQLException ex){
				logger.error("fail to close database connection", ex);
				throw new JyroException(ex);
			}
			try {
				if(con != null){
					con.close();
				}
			} catch(SQLException ex){
				logger.error("fail to close database connection", ex);
			}
		}
		return null;
	}

}
