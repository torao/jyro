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

import java.io.*;
import java.net.URI;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.*;

import org.koiroha.jyrobot.model.*;


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Session:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/26 Java SE 6
 */
public class Session implements Serializable, Closeable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// エンティティマネージャ
	// ======================================================================
	/**
	 * エンティティマネージャです。
	 */
	private final EntityManager manager;

	// ======================================================================
	// セッション ID
	// ======================================================================
	/**
	 * このセッションの ID です。
	 */
	private final long id;

	// ======================================================================
	// セッション ID
	// ======================================================================
	/**
	 * このセッションの ID です。
	 */
	private final long access;

	// ======================================================================
	// URI
	// ======================================================================
	/**
	 * このセッションでアクセスする URI プレフィクスです。
	 */
	private final URI base;

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * このセッションでアクセスする URI の接頭辞 URI を指定して構築を行います。
	 *
	 * @param manager エンティティマネージャ
	 * @param id セッション ID
	 * @param uri URI
	 */
	public Session(EntityManager manager, long id, long access, URI uri){
		this.manager = manager;
		this.id = id;
		this.base = uri;
		this.access = access;
		return;
	}

	// ======================================================================
	// セッション ID の参照
	// ======================================================================
	/**
	 * このセッションの ID を参照します。
	 *
	 * @return セッション ID
	 */
	public long getId(){
		return id;
	}

	// ======================================================================
	// 接頭辞 URI の参照
	// ======================================================================
	/**
	 * このセッションでアクセスするサイトの接頭辞 URI を参照します。
	 *
	 * @return 接頭辞 URI
	 */
	public URI getPrefixURI(){
		return base;
	}

	// ======================================================================
	// ジョブの投入
	// ======================================================================
	/**
	 * 指定されたセッションジョブをキューに追加します。
	 */
	public void offer(Job job) {
		EntityTransaction tran = manager.getTransaction();
		try {
			tran.begin();
			URI uri = job.getUri();
			String scheme = uri.getScheme();
			String host = uri.getHost();
			int port = uri.getPort();

			TypedQuery<JPASession> query = manager.createQuery(
				"select session from JPASession session " +
				"where session.scheme=? and session.host=? and session.port=?", JPASession.class);
			query.setParameter(1, scheme);
			query.setParameter(2, host);
			query.setParameter(3, port);
			JPASession s = query.getSingleResult();

			if(s == null){
				s = new JPASession();
				s.setScheme(scheme);
				s.setHost(host);
				s.setPort(port);
				s.setCreated(new Timestamp(System.currentTimeMillis()));
				manager.persist(s);
			}

			String path = uri.getPath();
			if(path == null){
				path = "/";
			}
			TypedQuery<JPAJob> query2 = manager.createQuery(
				"select * from jyrobot_session_jobs " +
				"where session=? and path=?", JPAJob.class);
			query2.setParameter(1, s.getId());
			query2.setParameter(2, path);
			JPAJob j = query2.getSingleResult();

			if(j == null){
				j = new JPAJob();
				j.setPath(path);
				j.setReferer(job.getReferer().toString());
				manager.persist(j);
			}

			tran.commit();
		} finally {
			tran.rollback();
		}
		return;
	}

	// ======================================================================
	// ジョブの参照
	// ======================================================================
	/**
	 * このセッション中の次のジョブを参照します。
	 */
	Job take() {
		EntityTransaction tran = manager.getTransaction();
		try {
			tran.begin();
			TypedQuery<JPAJob> query = manager.createQuery(
				"select job from JPAJob job" +
				" order by job.accessed", JPAJob.class);
			query.setFirstResult(0);
			query.setMaxResults(1);
			List<JPAJob> list = query.getResultList();
			if(list.isEmpty()){
				return null;
			}
			JPAJob job = list.get(0);

			tran.commit();
			tran = null;
		} finally {
			if(tran == null){
				tran.rollback();
			}
		}
		return;
	}

	// ======================================================================
	// セッションのクローズ
	// ======================================================================
	/**
	 * このセッションをクローズします。
	 */
	@Override
	public void close() {

		// 実行中フラグをオフに設定
		EntityTransaction tran = manager.getTransaction();
		try {
			tran.begin();
			Query query = manager.createQuery(
				"update JPASession session set session.activated=null where session.id=?");
			query.setParameter(1, getId());
			query.executeUpdate();
			tran.commit();
			tran = null;
		} finally {
			if(tran != null){
				tran.rollback();
			}

			// エンティティマネージャをクローズ
			manager.close();
		}
		return;
	}

	// ======================================================================
	// インスタンスの文字列化
	// ======================================================================
	/**
	 * このインスタンスを文字列化します。
	 *
	 * @return インスタンスの文字列
	 */
	@Override
	public String toString() {
		return id + ":" + base;
	}

	// ======================================================================
	// 接頭辞 URI の参照
	// ======================================================================
	/**
	 * このセッションでアクセスするサイトの接頭辞 URI を参照します。
	 *
	 * @return 接頭辞 URI
	 */
	public static class Job implements Serializable {

		// ==================================================================
		// Serial Version
		// ==================================================================
		/**
		 * Serial version of this class.
		 */
		private static final long serialVersionUID = 1L;

		private final URI uri;
		private final URI referer;
		public Job(URI uri, URI referer){
			this.uri = uri;
			this.referer = referer;
			return;
		}
		public URI getUri() {
			return uri;
		}
		public URI getReferer() {
			return referer;
		}
	}

}
