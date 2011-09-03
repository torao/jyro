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

import org.koiroha.jyrobot.model.JPARequest;


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Session: セッション
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * クローラーの 1 セッションを表すクラスです。このセッション中にクロールすべき
 * URL を参照することができます。
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
	// リクエスト対象の参照
	// ======================================================================
	/**
	 * このセッションから次のリクエスト対象を参照します。セッション中にリク
	 * エスト対象が存在しない場合は null を返します。
	 *
	 * @return リクエスト対象
	 */
	public Request take() {
		Request request = null;
		EntityTransaction tran = manager.getTransaction();
		try {
			tran.begin();

			// 一番過去にアクセスのあったリクエスト対象を参照
			TypedQuery<JPARequest> query = manager.createQuery(
				"select request from JPARequest request" +
				" where request.accessed < :accessed" +
				" order by request.accessed, request.id", JPARequest.class);
			query.setParameter("accessed", new Timestamp(access));
			query.setFirstResult(0);
			query.setMaxResults(1);
			List<JPARequest> list = query.getResultList();
			if(list.isEmpty()){
				return null;
			}

			// リクエスト対象の作成
			JPARequest r = list.get(0);
			URI uri = base.resolve(r.getPath());
			request = new Request(uri);

			tran.commit();
		} finally {
			if(tran.isActive()){
				tran.rollback();
			}
		}
		return request;
	}

	// ======================================================================
	// セッションのクローズ
	// ======================================================================
	/**
	 * このセッションをクローズします。
	 */
	@Override
	public void close() {
		EntityTransaction tran = manager.getTransaction();
		tran.begin();
		try {

			// 実行中フラグをオフに設定
			Query query = manager.createQuery(
				"update JPASession session" +
				" set session.activated=null, session.accessed=?1" +
				" where session.id=?2");
			query.setParameter(1, new Timestamp(access));
			query.setParameter(2, getId());
			query.executeUpdate();

			tran.commit();
		} finally {
			if(tran.isActive()){
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
		return "[" + id + "]" + base;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Request: リクエストクラス
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * リクエスト情報を保持するためのクラスです。
	 */
	public static class Request implements Serializable {

		// ==================================================================
		// Serial Version
		// ==================================================================
		/**
		 * Serial version of this class.
		 */
		private static final long serialVersionUID = 1L;

		// ==================================================================
		// リクエスト URI
		// ==================================================================
		/**
		 * リクエスト URI です。
		 */
		private final URI uri;

		// ==================================================================
		// コンストラクタ
		// ==================================================================
		/**
		 * リクエスト URI を指定して構築を行います。
		 *
		 * @param uri リクエスト URI
		 */
		public Request(URI uri){
			assert(uri.isAbsolute());
			this.uri = uri;
			return;
		}

		// ==================================================================
		// リクエスト URI の参照
		// ==================================================================
		/**
		 * リクエスト URI を参照します。
		 *
		 * @return リクエスト URI
		 */
		public URI getUri() {
			return uri;
		}

	}

}
