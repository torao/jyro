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

import org.apache.log4j.Logger;


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// AbstractSessionQueue:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version
 * @author torao
 * @since 2011/11/04 jyro 1.0
 */
public abstract class AbstractSessionQueue implements SessionQueue {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(AbstractSessionQueue.class);

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
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 *
	 */
	protected AbstractSessionQueue() {
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
		this.jyrobot = jyrobot;
		this.config = config;
		return;
	}

	// ======================================================================
	// セッションの参照
	// ======================================================================
	/**
	 * このスケジューラーから次のセッションを参照します。
	 *
	 * @return next session, or null if crawler end
	 * @throws InterruptedException ジョブの待機中に割り込まれた場合
	 */
	@Override
	public Session poll() throws InterruptedException {
		boolean errorReported = false;
		while(true){
			try {

				// 実行対象のセッションを取得できた場合はそれを返す
				long interval = config.getLong("site_access_interval");
				long lastAccessBefore = System.currentTimeMillis() - interval;
				Session session = take(lastAccessBefore);
				if(session != null){
					logger.debug("poll(): " + session);
					return session;
				}

				// 永続低ポーリングでなければ終了する
				if(! config.getBoolean("persistent_polling")) {
					return null;
				}
				errorReported = false;
			} catch(CrawlerException ex){
				// 連続した例外を冗長出力しないようフラグ制御
				if(! errorReported){
					logger.fatal("unexpected exception in polling session", ex);
				} else {
					logger.fatal("unexpected exception in polling session: " + ex);
				}
				errorReported = true;
			}

			// 次のポーリングまでしばらく待機
			Thread.sleep(config.getLong("queue_polling_interval"));
		}
	}

	// ======================================================================
	// セッションの参照
	// ======================================================================
	/**
	 * このスケジューラーから次のセッションを参照します。
	 *
	 * @param lastAccessBefore target session that last access before
	 * @return next session, or null if no available session exists
	 * @throws CrawlerException if fail to retrieve session
	 */
	protected abstract Session take(long lastAccessBefore) throws CrawlerException;

}
