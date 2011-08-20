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

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.koiroha.jyro.JyroException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// TransactionFilter: Filter for Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/03 Java SE 6
*/
public class JDBCTransaction extends Transaction {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JDBCTransaction.class);

	// ======================================================================
	// DataSource
	// ======================================================================
	/**
	 * DataSource
	*/
	private final BasicDataSource ds = new BasicDataSource();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	*/
	public JDBCTransaction() {
		ds.setLogAbandoned(true);
		return;
	}

	public void setUrl(String url){
		ds.setUrl(url);
		return;
	}
	public void setUsername(String username){
		ds.setUsername(username);
		return;
	}
	public void setPassword(String password){
		ds.setPassword(password);
		return;
	}
	public void setDriverClassName(String driverClassName){
		ds.setDriverClassName(driverClassName);
		return;
	}

	// ======================================================================
	// Initialize
	// ======================================================================
	/**
	 * Initialize filter.
	 *
	 * @throws JyroException if fail to initialize filter
	*/
	@Override
	public void init() throws JyroException{
		return;
	}

	// ======================================================================
	// Destroy
	// ======================================================================
	/**
	 * Destory filter.
	*/
	@Override
	public void destroy(){
		try {
			ds.close();
			logger.debug("close jdbc transaction");
		} catch(SQLException ex){
			logger.warn("fail to close datasource", ex);
		}
		return;
	}

	// ======================================================================
	// Refer DataSource
	// ======================================================================
	/**
	 * Refer DataSource
	 *
	*/
	@Override
	protected DataSource getDataSource(){
		return ds;
	}

}
