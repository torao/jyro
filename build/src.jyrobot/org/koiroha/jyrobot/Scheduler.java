/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyrobot;

import java.lang.management.ManagementFactory;
import java.net.*;
import java.sql.*;
import java.util.List;

import javax.persistence.*;

import org.apache.log4j.Logger;
import org.koiroha.jyrobot.Session.Request;
import org.koiroha.jyrobot.model.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Scheduler: スケジューラー
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * クロール対象の URL を受け付けてクローラーにジョブを投入するクラスです。
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/26 Java SE 6
 */
public class Scheduler {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Scheduler.class);

	// ======================================================================
	// エンティティマネージャファクトリ
	// ======================================================================
	/**
	 * エンティティマネージャのファクトリです。
	 */
	private final EntityManagerFactory factory;

	// ======================================================================
	// キューポーリング間隔
	// ======================================================================
	/**
	 * セッションキューに対するポーリング間隔です。
	 */
	private long queuePollingInterval = 1000;

	// ======================================================================
	// サイト更新間隔
	// ======================================================================
	/**
	 * 一度クロールしたサイトに再び訪れるまでの間隔です。
	 */
	private long revisitInterval = 24 * 60 * 60 * 1000L;

	// ======================================================================
	// サイト更新間隔
	// ======================================================================
	/**
	 * 一度クロールしたサイトに再び訪れるまでの間隔です。
	 */
	private String node = ManagementFactory.getRuntimeMXBean().getName();

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * {@code jyrobot} という名のエンティティマネージャファクトリ名を使用して
	 * 構築します。
	 */
	public Scheduler() {
		this("jyrobot");
		return;
	}

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * エンティティマネージャファクトリの名前を指定して構築を行います。
	 *
	 * @param name エンティティマネージャファクトリの名前
	 */
	public Scheduler(String name) {
		this.factory = Persistence.createEntityManagerFactory(name);
		return;
	}

	// ======================================================================
	// 全セッションのリセット
	// ======================================================================
	/**
	 * すべてのセッションの実行中フラグと前回アクセス日時をリセットし、次回の
	 * 処理で即時実行されるようにします。
	 *
	 * @return 全セッション数
	 */
	public int resetAllSessions() {
		logger.debug("resetAllSessions()");
		EntityManager manager = factory.createEntityManager();
		EntityTransaction tran = manager.getTransaction();
		try {
			tran.begin();

			// 実行中時刻と前回アクセス時刻をリセット
			Query query = manager.createQuery(
				"update JPASession session set session.activated=null, session.accessed=null");
			int count = query.executeUpdate();

			tran.commit();
			tran = null;
			return count;
		} finally {
			if(tran != null){
				tran.rollback();
			}
			manager.close();
		}
	}

	// ======================================================================
	// セッションの参照
	// ======================================================================
	/**
	 * このスケジューラーから次のセッションを参照します。
	 *
	 * @return 次のセッション
	 * @throws SQLException セッションの参照に失敗した場合
	 * @throws InterruptedException ジョブの待機中に割り込まれた場合
	 */
	public Session next() throws InterruptedException {
		while(true){

			// セッションの復元に成功したらそれを返す
			Session session = restore();
			if(session != null){
				logger.debug("next(): " + session);
				return session;
			}

			// 次のポーリングまでしばらく待機
			Thread.sleep(queuePollingInterval);
		}
	}

	// ======================================================================
	// ジョブの投入
	// ======================================================================
	/**
	 * 指定されたセッションジョブをキューに追加します。
	 */
	public void put(Request job) {
		EntityManager manager = factory.createEntityManager();
		try {
			put(manager, job);
		} finally {
			manager.close();
		}
		return;
	}

	// ======================================================================
	// リクエストの投入
	// ======================================================================
	/**
	 * 指定されたリクエストを実行キューに投入します。
	 *
	 * @param manager エンティティマネージャ
	 * @param request 投入するリクエスト
	 */
	void put(EntityManager manager, Request request) {
		EntityTransaction tran = manager.getTransaction();
		try {
			tran.begin();

			// リクエストに該当するセッションを参照
			URI uri = request.getUri();
			String scheme = uri.getScheme();
			String host = uri.getHost();
			int port = uri.getPort();
			TypedQuery<JPASession> query = manager.createQuery(
				"select session from JPASession session" +
				" where session.scheme=?1 and session.host=?2 and session.port=?3", JPASession.class);
			query.setParameter(1, scheme);
			query.setParameter(2, host);
			query.setParameter(3, port);
			List<JPASession> list = query.getResultList();
			assert(list.size() <= 1);

			// セッションが存在しなければ新規に作成
			JPASession session = null;
			if(list.isEmpty()){
				session = new JPASession();
				session.setScheme(scheme);
				session.setHost(host);
				session.setPort(port);
				session.setCreated(new Timestamp(System.currentTimeMillis()));
				manager.persist(session);
				logger.debug("add session: " + session);
			} else {
				session = list.get(0);
			}

			// リクエストキューに保存
			String path = uri.getPath();
			if(path == null){
				path = "/";
			}
			TypedQuery<Long> query2 = manager.createQuery(
				"select count(request) from JPARequest request" +
				" where request.session=?1 and request.path=?2", Long.class);
			query2.setParameter(1, session.getId());
			query2.setParameter(2, path);
			long count = query2.getSingleResult();

			if(count == 0){
				JPARequest r = new JPARequest();
				r.setSession(session.getId());
				r.setPath(path);
				if(request.getReferer() != null){
					r.setReferer(request.getReferer().toString());
				}
				manager.persist(r);
				logger.debug("add request: " + request);
			}

			tran.commit();
		} finally {
			if(tran.isActive()){
				tran.rollback();
			}
		}
		return;
	}

	// ======================================================================
	// セッションの復元
	// ======================================================================
	/**
	 * 指定されたコネクションを使用してセッションキューから実行対象のセッショ
	 * ンを復元します。実行対象のセッションが存在しない場合は null を返します。
	 *
	 * @param con データベース接続
	 * @return 復元したセッション
	 * @throws SQLException セッションの参照に失敗した場合
	 */
	private Session restore() {
		Session session = null;
		EntityManager manager = factory.createEntityManager();
		EntityTransaction tran = manager.getTransaction();
		tran.begin();
		try {
			long now = System.currentTimeMillis();

			// キューから優先順位の最も高いセッションを参照
			TypedQuery<JPASession> query = manager.createQuery(
				"select session from JPASession session" +
				" where session.activated is null and (session.accessed is null or session.accessed<?1)" +
				" order by session.priority desc, session.accessed, session.created asc", JPASession.class);
			query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
			query.setParameter(1, new Timestamp(now - revisitInterval));
			List<JPASession> list = query.getResultList();
			if(list.size() == 0){
				return null;
			}
			JPASession jpaSession = list.get(0);

			// セッションの復元
			int id = jpaSession.getId();
			String scheme = jpaSession.getScheme();
			String host = jpaSession.getHost();
			int port = jpaSession.getPort();
			String path = "/";
			try {
				URI uri = new URI(scheme, null, host, port, path, null, null);
				session = new Session(manager, id, now, uri);
			} catch(URISyntaxException ex){
				logger.warn("invalid uri data: " + scheme + "://" + host + ":" + port + path, ex);
				manager.remove(jpaSession);
				return null;
			}

			// セッションをアクティブ状態に設定
			jpaSession.setActivated(new Timestamp(now));
			jpaSession.setAppid(node);
			manager.persist(jpaSession);

			tran.commit();
		} finally {
			if(tran.isActive()){
				tran.rollback();
			}

			// セッションの構築に失敗している場合はマネージャをクローズ
			if(session == null){
				manager.close();
			}
		}
		return session;
	}

}
