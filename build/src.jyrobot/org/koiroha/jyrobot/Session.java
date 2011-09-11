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
	// Total Retrieval URLs
	// ======================================================================
	/**
	 * このセッションで取得したURLの総数です。
	 */
	private long totalUrl = 0;

	// ======================================================================
	// Total Retrieval Bytes
	// ======================================================================
	/**
	 * このセッションで取得したデータの総バイト数です。
	 */
	private long totalRetrieval = 0;

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
	// Refer Total URLs
	// ======================================================================
	/**
	 * このセッションで取得した URL 数を参照します。
	 *
	 * @return total urls
	 */
	public long getTotalUrl() {
		return totalUrl;
	}

	// ======================================================================
	// Refer Total Retrieval
	// ======================================================================
	/**
	 * このセッションで取得した総データ数を参照します。
	 *
	 * @return total retrieval in bytes
	 */
	public long getTotalRetrieval() {
		return totalRetrieval;
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
				" where request.accessed is null or request.accessed < :accessed" +
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
			String id = r.getId();
			request = new Request(id, uri);

			tran.commit();
		} finally {
			if(tran.isActive()){
				tran.rollback();
			}
		}
		return request;
	}

	// ======================================================================
	// リクエスト結果の設定
	// ======================================================================
	/**
	 * 指定されたリクエストの結果を設定します。
	 *
	 * @return リクエスト対象
	 */
	public void save(Request request, Content content){
		assert(request.getId() != null);

		synchronized(this){
			totalUrl ++;
			totalRetrieval += content.response.getContent().length;
		}

		EntityTransaction tran = manager.getTransaction();
		try {
			tran.begin();

			// 一番過去にアクセスのあったリクエスト対象を参照
			Query query = manager.createQuery(
				"update JPARequest request" +
				" set request.response=?1, request.accessed=?2" +
				" where request.id=?3");
			query.setParameter(1, content == null? -1: content.response.getCode());
			query.setParameter(2, new Timestamp(access));
			query.setParameter(3, request.getId());
			query.executeUpdate();

			tran.commit();
		} finally {
			if(tran.isActive()){
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
		// Request ID
		// ==================================================================
		/**
		 * リクエスト ID です。
		 */
		private final String id;

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
			this.id = null;
			this.uri = uri;
			return;
		}

		// ==================================================================
		// コンストラクタ
		// ==================================================================
		/**
		 * リクエスト URI を指定して構築を行います。
		 *
		 * @param uri リクエスト URI
		 */
		private Request(String id, URI uri){
			assert(uri.isAbsolute());
			this.id = id;
			this.uri = uri;
			return;
		}

		// ==================================================================
		// リクエスト ID の参照
		// ==================================================================
		/**
		 * リクエスト ID を参照します。
		 *
		 * @return リクエスト ID
		 */
		public String getId() {
			return id;
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

		// ==================================================================
		// インスタンスの文字列化
		// ==================================================================
		/**
		 * このインスタンスを文字列化します。
		 *
		 * @return インスタンスの文字列
		 */
		@Override
		public String toString() {
			return uri.toString();
		}

	}

}
