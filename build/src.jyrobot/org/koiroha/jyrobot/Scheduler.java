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

import java.net.*;
import java.sql.*;
import java.util.List;

import javax.persistence.*;

import org.apache.log4j.Logger;
import org.koiroha.jyrobot.model.JPASession;

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
	// コンストラクタ
	// ======================================================================
	/**
	 * データソースを指定して構築を行います。
	 */
	public Scheduler() {
		this.factory = Persistence.createEntityManagerFactory("jyrobot");
		return;
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
				return session;
			}

			// 次のポーリングまでしばらく待機
			Thread.sleep(queuePollingInterval);
		}
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
		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();
		try {

			// キューから優先順位の最も高いセッションを参照
			TypedQuery<JPASession> query = manager.createQuery(
				"select session from JPASession session" +
				" where session.activated is null" +
				" order by session.priority desc, session.created asc", JPASession.class);
			query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
			List<JPASession> list = query.getResultList();
			if(list.size() == 0){
				return null;
			}
			JPASession jpaSession = list.get(0);

			// セッションの復元
			int id = jpaSession.getId();
			long now = System.currentTimeMillis();
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
			manager.persist(jpaSession);

			// コミットの実行
			transaction.commit();
			transaction = null;
		} finally {

			// トランザクション中に例外が発生している場合
			if(transaction != null){
				transaction.rollback();
			}

			// セッションの構築に失敗している場合
			if(session == null){
				manager.close();
			}
		}
		return session;
	}

}
