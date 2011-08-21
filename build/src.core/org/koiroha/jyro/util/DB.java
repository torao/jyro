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

import java.sql.*;

import org.koiroha.jyro.JyroException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// DB: Database Utility
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Database utility class.
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/21 Java SE 6
 */
public final class DB {

	/**
	 *
	 */
	private DB() {
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 *
	 */
	public static <T> T create(Class<T> type, ResultSet rs) throws SQLException, JyroException {
		T obj = type.newInstance();
		ResultSetMetaData meta = rs.getMetaData();
		for(int i=0; i<meta.getColumnCount(); i++){
			Beans.setProperty(obj, meta.getColumnName(i+1), rs.getString(i+1));
		}
		return obj;
	}

}
