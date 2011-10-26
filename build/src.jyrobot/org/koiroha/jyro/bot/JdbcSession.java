/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.bot;

import java.sql.*;

import javax.sql.DataSource;

import org.koiroha.jyro.util.Util;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JdbcSession:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version
 * @author torao
 * @since 2011/10/25 jyro 1.0
 */
class JdbcSession extends Session {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// データソース
	// ======================================================================
	/**
	 * このセッションで使用するデータソースです。
	 */
	private final DataSource dataSource;

	// ======================================================================
	// URL Scheme
	// ======================================================================
	/**
	 * このセッションのスキームです。
	 */
	private final String scheme;

	// ======================================================================
	// Host Name
	// ======================================================================
	/**
	 * このセッションのホスト名です。
	 */
	private final String host;

	// ======================================================================
	// Port Number
	// ======================================================================
	/**
	 * このセッションのポート番号です。
	 */
	private final int port;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 *
	 * @param id
	 * @param ds data source that used in this session
	 * @param scheme URL scheme
	 * @param host host name
	 * @param port port number
	 */
	public JdbcSession(long id, DataSource ds, String scheme, String host, int port) {
		super(id);
		this.dataSource = ds;
		this.scheme = scheme;
		this.host = host;
		this.port = port;
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 *
	 * @param ds data source that used in this session
	 * @param rs database record of this session
	 * @throws SQLException if fail to retrieve fields
	 */
	JdbcSession(DataSource ds, ResultSet rs) throws SQLException {
		super(rs.getLong("id"));
		this.dataSource = ds;
		this.scheme = rs.getString("scheme");
		this.host = rs.getString("host");
		this.port = rs.getInt("port");
		return;
	}

	// ======================================================================
	// Refer scheme
	// ======================================================================
	/**
	 * URL スキームを参照します。
	 *
	 * @return scheme スキーム
	 */
	public String getScheme() {
		return scheme;
	}

	// ======================================================================
	// Refer host
	// ======================================================================
	/**
	 * ホスト名を参照します。
	 *
	 * @return host hostname
	 */
	public String getHost() {
		return host;
	}

	// ======================================================================
	// Refer port
	// ======================================================================
	/**
	 * ポート番号を参照します。
	 *
	 * @return port port number
	 */
	public int getPort() {
		return port;
	}

	// ======================================================================
	// Close Session
	// ======================================================================
	/**
	 * このセッションをクローズします。
	 */
	@Override
	public void close() throws CrawlerException{
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(
				"UPDATE jyro_sessions SET appid=NULL, activated=NULL, accessed=? WHERE id=?");
			stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			stmt.setLong(2, getId());
			stmt.executeUpdate();
			con.commit();
		} catch(SQLException ex){
			throw new CrawlerException(ex);
		} finally {
			Util.close(stmt);
			Util.close(con);
		}
		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 * このインスタンスを文字列化します。
	 * @return インスタンスの文字列
	 */
	@Override
	public String toString() {
		return scheme + "://" + host + ":" + port;
	}

	// ======================================================================
	// Retrieve Connection
	// ======================================================================
	/**
	 * データベース接続を参照します。
	 *
	 * @return database connection
	 * @throws SQLException if fail to retrieve connection
	 */
	private Connection getConnection() throws SQLException{
		Connection con = dataSource.getConnection();
		con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		con.setAutoCommit(false);
		return con;
	}

}
