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

import java.net.*;
import java.sql.*;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
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
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JdbcSession.class);

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
	// Visit Number
	// ======================================================================
	/**
	 * このセッションに対する訪問回数です。
	 */
	private final int visit;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 *
	 * @param jyrobot application instance
	 * @param ds data source that used in this session
	 * @param rs database record of this session
	 * @throws SQLException if fail to retrieve fields
	 */
	JdbcSession(Jyrobot jyrobot, DataSource ds, ResultSet rs) throws SQLException {
		super(jyrobot, rs.getLong("id"));
		this.dataSource = ds;
		this.scheme = rs.getString("scheme");
		this.host = rs.getString("host");
		this.port = rs.getInt("port");
		this.visit = rs.getInt("visit") + 1;
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
	// Refer visit
	// ======================================================================
	/**
	 * このセッションでの訪問回数を参照します。
	 *
	 * @return visit visit count over this session
	 */
	public int getVisit() {
		return visit;
	}

	// ======================================================================
	// Retrieve Request URL
	// ======================================================================
	/**
	 * このセッションから次のリクエスト対象となる URL を参照します。
	 *
	 * @return request url, or null if no more url remained
	 * @throws CrawlerException if fail to retrieve URL
	 */
	@Override
	protected URL pollURL() throws CrawlerException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(
				"SELECT * FROM jyrobot_locations WHERE session_id=? AND visit<>? LIMIT 1 FOR UPDATE",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, getId());
			stmt.setInt(2, getVisit());
			rs = stmt.executeQuery();
			while(rs.next()){
				String path = rs.getString("path");

				// 訪問回数の更新
				rs.updateInt("visit", getVisit());
				rs.updateRow();
				con.commit();

				// URL の構築
				try {
					return new URL(getScheme(), getHost(), getPort(), path);
				} catch(MalformedURLException ex){
					logger.warn("invalid url contains: ", ex);
				}
			}
			con.commit();
		} catch(SQLException ex){
			throw new CrawlerException(ex);
		} finally {
			Util.close(rs, stmt, con);
		}
		return null;
	}

	// ======================================================================
	// Close Session
	// ======================================================================
	/**
	 * このセッションをクローズします。
	 */
	@Override
	public void close() throws CrawlerException{
		logger.debug("close()");
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(
				"UPDATE jyrobot_sessions SET appid=NULL, activated=NULL, accessed=? WHERE id=?");
			stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			stmt.setLong(2, getId());
			stmt.executeUpdate();
			con.commit();
		} catch(SQLException ex){
			throw new CrawlerException(ex);
		} finally {
			Util.close(stmt, con);
		}
		return;
	}

	// ======================================================================
	// Refer Instance String
	// ======================================================================
	/**
	 * このインスタンスを文字列化します。
	 *
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
		return Util.wrap(con);
	}

}
