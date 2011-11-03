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

import java.lang.management.ManagementFactory;
import java.net.*;
import java.sql.*;
import java.util.*;

import javax.naming.*;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;
import org.koiroha.jyro.util.Util;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JdbcSessionQueue: DB セッションキュー
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * データベースを使用したセッションキューです。
 * クロール対象の URL を保存しセッションを参照することができます。
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/26 Java SE 6
 */
public class JdbcSessionQueue implements SessionQueue {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JdbcSessionQueue.class);

	// ======================================================================
	// Configuration
	// ======================================================================
	/**
	 * このセッションキューの設定です。
	 */
	protected Jyrobot jyrobot = null;

	// ======================================================================
	// Configuration
	// ======================================================================
	/**
	 * このセッションキューの設定です。
	 */
	protected Config config = null;

	// ======================================================================
	// Application ID
	// ======================================================================
	/**
	 * このアプリケーションの ID です。
	 */
	private String appId = ManagementFactory.getRuntimeMXBean().getName();

	// ======================================================================
	// Data Source
	// ======================================================================
	/**
	 * このスケジューラーが使用するデータソースです。
	 */
	private DataSource dataSource = null;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 */
	public JdbcSessionQueue() {
		return;
	}

	// ======================================================================
	// Configure Queue
	// ======================================================================
	/**
	 * このキューの設定を行います。
	 *
	 * @param jyrobot application instance
	 * @param config configuration for this queue
	 */
	@Override
	public void configure(Jyrobot jyrobot, Config config) throws CrawlerException{
		logger.debug("configure(" + config + ")");
		this.jyrobot = jyrobot;
		this.config = config;

		// データソース設定の参照
		Object value = config.getObject("datasource");
		if(value instanceof Map<?,?>){
			Properties prop = new Properties();
			for(Map.Entry<?,?> e: ((Map<?,?>)value).entrySet()){
				prop.setProperty(e.getKey().toString(), e.getValue().toString());
			}
			try {
				this.dataSource = BasicDataSourceFactory.createDataSource(prop);
			} catch(Exception ex){
				throw new CrawlerException(ex);
			}
		} else if(value instanceof String){
			try {
				InitialContext ic = new InitialContext();
				this.dataSource = (DataSource)ic.lookup((String)value);
			} catch(NamingException ex){
				throw new CrawlerException(ex);
			}
		} else {
			throw new CrawlerException("invalid datasource configuration: " + config.getPathname("datasource") + "=" + value);
		}
		return;
	}

	// ======================================================================
	// Refer Polling Interval
	// ======================================================================
	/**
	 * セッションキューのポーリング間隔をミリ秒で参照します。
	 *
	 * @return queue polling interval in milliseconds
	 */
	public long getQueuePollingInterval() {
		return config.getLong("queue_polling_interval");
	}

	// ======================================================================
	// Set Polling Interval
	// ======================================================================
	/**
	 * セッションキューのポーリング間隔をミリ秒で設定します。
	 *
	 * @param queuePollingInterval queue polling interval in milliseconds
	 */
	public void setQueuePollingInterval(long queuePollingInterval) {
		if(queuePollingInterval <= 0){
			throw new IllegalArgumentException("invalid polling interval: " + queuePollingInterval);
		}
		config.getMap().put("queue_polling_interval", queuePollingInterval);
		return;
	}

	// ======================================================================
	// Session Available Interval
	// ======================================================================
	/**
	 * 前回のアクセスからセッションがクローリング対象となるまでの時間をミリ秒で参照します。
	 *
	 * @return session available interval in milliseconds
	 */
	public long getSiteAccessInterval() {
		return config.getLong("site_access_interval");
	}

	// ======================================================================
	// Set Polling Interval
	// ======================================================================
	/**
	 * セッションキューのポーリング間隔をミリ秒で設定します。
	 *
	 * @param sessionAvailableInterval queue polling interval in milliseconds
	 */
	public void setSessionAvailableInterval(long sessionAvailableInterval) {
		if(sessionAvailableInterval <= 0){
			throw new IllegalArgumentException("invalid polling interval: " + sessionAvailableInterval);
		}
		config.getMap().put("session_available_interval", sessionAvailableInterval);
		return;
	}

	// ======================================================================
	// 全セッションのリセット
	// ======================================================================
	/**
	 * すべてのセッションの実行中フラグと前回アクセス日時をリセットし、次回の
	 * 処理で即時実行されるようにします。
	 *
	 * @throws CrawlerException if fail to reset sessions
	 */
	@Override
	public int resetAllSessions() throws CrawlerException{
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("UPDATE jyrobot_sessions SET activated=NULL, accessed=NULL");
			int count = stmt.executeUpdate();
			con.commit();
			logger.info("reset all " + count + " sessions");
			return count;
		} catch(SQLException ex){
			throw new CrawlerException(ex);
		} finally {
			Util.close(stmt, con);
		}
	}

	// ======================================================================
	// セッションの参照
	// ======================================================================
	/**
	 * このスケジューラーから次のセッションを参照します。
	 *
	 * @return 次のセッション
	 * @throws InterruptedException ジョブの待機中に割り込まれた場合
	 */
	@Override
	public Session poll() throws InterruptedException {
		boolean errorReported = false;
		while(true){

			// セッションの復元に成功したらそれを返す
			try {
				JdbcSession session = pull();
				if(session != null){
					logger.debug("next(): " + session);
					return session;
				}
				errorReported = false;
			} catch(SQLException ex){
				if(! errorReported){
					logger.fatal("unexpected exception in poll session", ex);
				}
				errorReported = true;
			}

			// 次のポーリングまでしばらく待機
			java.lang.Thread.sleep(getQueuePollingInterval());
		}
	}

	// ======================================================================
	// Register Request URL
	// ======================================================================
	/**
	 * 指定された URL に対するリクエストが次回以降に行われるようこのキューに投入します。
	 *
	 * @param url request url
	 */
	@Override
	public void offer(URL url) throws CrawlerException{

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = getConnection();

			// データベースに保存する情報を取得
			URI uri = url.toURI();
			String scheme = uri.getScheme().toLowerCase();
			int port = uri.getPort();
			if(port < 0){
				port = Util.getDefaultPort(scheme);
			}
			String host = uri.getHost().toLowerCase();

			// URL に対応するセッションを取得
			long sessionId = find(con, scheme, host, port);
			if(sessionId < 0){
				create(con, scheme, host, port);
				sessionId = find(con, scheme, host, port);
			}

			// パスの参照
			String path = uri.normalize().getPath();
			if(path == null){
				path = "";
			} else {
				// 先頭の連続した '/' を削除
				while(path.length() > 0 && path.charAt(0) == '/'){
					path = path.substring(1);
				}
			}

			// ロケーションを登録
			offer(con, sessionId, path);

			con.commit();
		} catch(URISyntaxException ex){
			throw new CrawlerException(ex);
		} catch(SQLException ex){
			throw new CrawlerException(ex);
		} finally {
			Util.close(rs, stmt, con);
		}
		return;
	}

	// ======================================================================
	// Find Session ID
	// ======================================================================
	/**
	 * 指定された URL に対するセッション ID を参照します。
	 *
	 * @param con database connection
	 * @param scheme URL scheme
	 * @param host hostname
	 * @param port port number
	 * @return session ID, or negative value if not found
	 * @throws SQLException if fail to execute query
	 */
	private long find(Connection con, String scheme, String host, int port) throws SQLException{
		long sessionId = -1;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(
				"SELECT id FROM jyrobot_sessions WHERE scheme=? AND host=? AND port=?",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, scheme);
			stmt.setString(2, host);
			stmt.setInt(3, port);
			rs = stmt.executeQuery();
			rs.setFetchSize(1);

			// セッションを取得
			if(rs.next()){
				sessionId = rs.getLong("id");
				assert(! rs.next()): "重複セッション検出: " + scheme + "://" + host + ":" + port;
			}
		} finally {
			Util.close(rs, stmt);
		}
		return sessionId;
	}

	// ======================================================================
	// Create Session
	// ======================================================================
	/**
	 * 指定された URL に対するセッションを新規に構築します。
	 *
	 * @param con database connection
	 * @param scheme URL scheme
	 * @param host hostname
	 * @param port port number
	 * @throws SQLException if fail to execute query
	 */
	private void create(Connection con, String scheme, String host, int port) throws SQLException{
		Timestamp now = new Timestamp(System.currentTimeMillis());
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(
				"INSERT INTO jyrobot_sessions(scheme,host,port,created) VALUES(?,?,?,?)");
			stmt.setString(1, scheme);
			stmt.setString(2, host);
			stmt.setInt(3, port);
			stmt.setTimestamp(4, now);
			stmt.executeUpdate();
		} finally {
			Util.close(stmt);
		}
		return;
	}

	// ======================================================================
	// Offer Location
	// ======================================================================
	/**
	 * 指定された URL を実行対象として登録します。
	 *
	 * @param con database connection
	 * @param sessionId session ID
	 * @param path path for URL
	 * @throws SQLException if fail to execute query
	 */
	private void offer(Connection con, long sessionId, String path) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(
				"SELECT * FROM jyrobot_locations WHERE session_id=? AND path=? LIMIT 1",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			stmt.setLong(1, sessionId);
			stmt.setString(2, path);
			rs = stmt.executeQuery();
			rs.setFetchSize(1);

			// 存在していなければ新規に作成
			if(! rs.next()){
				rs.moveToInsertRow();
				rs.updateLong("session_id", sessionId);
				rs.updateString("path", path);
				rs.updateInt("visit", 0);
				rs.insertRow();
			}
		} finally {
			Util.close(rs);
			Util.close(stmt);
		}
		return;
	}

	// ======================================================================
	// Pull Session
	// ======================================================================
	/**
	 * セッションを取得します。
	 *
	 * @return session instance, or null if not found
	 * @throws SQLException if fail to execute query
	 */
	private JdbcSession pull() throws SQLException{
		logger.debug("poll()");

		// 絞り込みの期限を参照
		Timestamp limit = new Timestamp(System.currentTimeMillis() - getSiteAccessInterval());

		JdbcSession session = null;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(
				"SELECT * FROM jyrobot_sessions" +
				" WHERE activated IS NULL AND accessed < ?" +
				" ORDER BY priority DESC, accessed ASC LIMIT 1 FOR UPDATE",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			stmt.setTimestamp(1, limit);
			rs = stmt.executeQuery();
			rs.setFetchSize(1);

			String appid = java.lang.Thread.currentThread().getId() + "#" + this.appId;
			Timestamp now = new Timestamp(System.currentTimeMillis());
			if(rs.next()){
				// 既存のセッションを実行状態に設定
				rs.updateString("appid", appid);
				rs.updateTimestamp("activated", now);
				rs.updateTimestamp("accessed", now);
				rs.updateInt("visit", rs.getInt("visit") + 1);
				rs.updateRow();

				// セッションを参照
				session = new JdbcSession(jyrobot, dataSource, rs);
			}
			con.commit();
		} finally {
			Util.close(rs, stmt, con);
		}
		return session;
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
	protected Connection getConnection() throws SQLException{
		Connection con = dataSource.getConnection();
		con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		con.setAutoCommit(false);
		return Util.wrap(con);
	}

}
