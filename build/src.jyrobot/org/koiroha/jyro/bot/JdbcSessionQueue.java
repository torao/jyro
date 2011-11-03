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
public class JdbcSessionQueue extends AbstractSessionQueue {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JdbcSessionQueue.class);

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
		super.configure(jyrobot, config);
		logger.debug("configure(" + config + ")");

		// データソース設定の参照
		Object value = config.getObject("datasource");
		if(value instanceof Map<?,?>){

			// データソース初期化用のプロパティを構築
			Properties prop = new Properties();
			for(Map.Entry<?,?> e: ((Map<?,?>)value).entrySet()){
				String name = Util.yaml2propertyName(e.getKey().toString());
				prop.setProperty(name, e.getValue().toString());
			}

			// データソースの構築
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
			Key key = new Key(url);

			// URL に対応するセッションを取得
			long sessionId = find(con, key);

			// セッションが存在しなければ新規に作成
			if(sessionId < 0){
				create(con, key);
				sessionId = find(con, key);
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
	// Reset Crawling URL
	// ======================================================================
	/**
	 * 指定された URL がキューに存在する場合、クローリングスケジュールをリセットし未アクセス状態にします。
	 * このメソッドの呼び出しにより指定された URL は早い時期にクローリングが行われるようになります。
	 * URL が存在しない場合や既に実行中の場合は何も行わず false を返します。
	 *
	 * @param url URL to reset crawling schedule
	 * @param zombie accessed before timestamp of session that recognized
	 * as zombie and force reset
	 * @return true if crawling schedule reset normally, false if specified
	 * URL is not enqueued or now on crawling
	 * @throws CrawlerException if fail to reset schedule
	 */
	@Override
	public boolean reset(URL url, long zombie) throws CrawlerException{
		logger.debug("reset(" + url + "," + zombie + ")");
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
			Key key = new Key(url);
			stmt = con.prepareStatement(
				"UPDATE jyrobot_sessions SET appid=NULL, accessed=NULL, activated=NULL" +
				" WHERE scheme=? AND host=? AND port=? AND (activated IS NULL OR accessed<=?)");
			stmt.setString(1, key.scheme);
			stmt.setString(2, key.host);
			stmt.setInt(3, key.port);
			stmt.setTimestamp(4, new Timestamp(zombie));
			int count = stmt.executeUpdate();
			assert(count == 1 || count == 0): count;
			con.commit();
			return (count == 1);
		} catch(SQLException ex){
			throw new CrawlerException(ex);
		} finally {
			Util.close(stmt, con);
		}
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
	public int resetAll() throws CrawlerException{
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getConnection();
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
	// Pull Session
	// ======================================================================
	/**
	 * セッションを取得します。
	 *
	 * @param lastAccessBefore target session that last access before
	 * @return next session, or null if no available session exists
	 * @throws CrawlerException if fail to retrieve session
	 */
	@Override
	protected Session take(long lastAccessBefore) throws CrawlerException{
		logger.debug("take()");

		// 絞り込みの期限を参照
		Timestamp limit = new Timestamp(lastAccessBefore);

		JdbcSession session = null;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			stmt = con.prepareStatement(
				"SELECT * FROM jyrobot_sessions" +
				" WHERE activated IS NULL AND (accessed IS NULL OR accessed < ?)" +
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
		} catch(SQLException ex){
			throw new ConfigurationException(ex);
		} finally {
			Util.close(rs, stmt, con);
		}
		return session;
	}

	// ======================================================================
	// Find Session ID
	// ======================================================================
	/**
	 * 指定された URL に対するセッション ID を参照します。
	 *
	 * @param con database connection
	 * @param key session key
	 * @return session ID, or negative value if not found
	 * @throws SQLException if fail to execute query
	 */
	private long find(Connection con, Key key) throws SQLException{
		long sessionId = -1;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(
				"SELECT id FROM jyrobot_sessions WHERE scheme=? AND host=? AND port=?",
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, key.scheme);
			stmt.setString(2, key.host);
			stmt.setInt(3, key.port);
			rs = stmt.executeQuery();
			rs.setFetchSize(1);

			// セッションを取得
			if(rs.next()){
				sessionId = rs.getLong("id");
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
	 * @param key session key
	 * @throws SQLException if fail to execute query
	 */
	private void create(Connection con, Key key) throws SQLException{
		Timestamp now = new Timestamp(System.currentTimeMillis());
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(
				"INSERT INTO jyrobot_sessions(scheme,host,port,created) VALUES(?,?,?,?)");
			stmt.setString(1, key.scheme);
			stmt.setString(2, key.host);
			stmt.setInt(3, key.port);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Key: セッションキー
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * セッションテーブル検索用に使用するキークラスです。
	 */
	private static class Key {
		/** スキーム */
		public final String scheme;
		/** ホスト名 */
		public final String host;
		/** ポート番号 */
		public final int port;
		/**
		 * コンストラクタ
		 * @param url
		 */
		public Key(URL url){;
			this.scheme = url.getProtocol().toLowerCase();
			this.host = url.getHost().toLowerCase();
			if(url.getPort() >= 0){
				this.port = url.getPort();
			} else {
				this.port = Util.getDefaultPort(scheme);
			}
			return;
		}
	}
}
