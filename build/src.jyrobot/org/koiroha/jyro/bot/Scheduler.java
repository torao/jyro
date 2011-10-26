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
// Scheduler: スケジューラー
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * クロール対象の URL を受け付けてクローラーにジョブを投入するクラスです。
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/26 Java SE 6
 */
class Scheduler {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Scheduler.class);

	// ======================================================================
	// Scheduler Configuration
	// ======================================================================
	/**
	 * このスケジューラの設定です。
	 */
	private final Config config;

	// ======================================================================
	// Session Queue
	// ======================================================================
	/**
	 * このスケジューラが使用するセッションキューです。
	 */
	private final SessionQueue queue;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * スケジューラ設定を指定して構築を行います。
	 *
	 * @param config scheduler configuration that means "scheduler" section in jyrobot yaml
	 */
	public Scheduler(Config config) {
		this.config = config;

		// セッションキューの作成
		String className = config.getString("session_queue", "class");
		try {
			this.queue = (SessionQueue)Class.forName(className).newInstance();
		} catch(Exception ex){
			throw new ConfigurationException(config.getPathname("session_queue", "class") + "=" + className, ex);
		}
		this.queue.configure(config.getSubconfig("session_queue"));
		return;
	}

	// ======================================================================
	// Refer Site Access Interval
	// ======================================================================
	/**
	 * 同一サイトに対してのアクセス間隔を参照します。
	 *
	 * @return visit interval in milliseconds
	 */
	public long getSiteAccessInterval() {
		return config.getLong("site_access_interval");
	}

	// ======================================================================
	// Retrieve Session Queue
	// ======================================================================
	/**
	 * このスケジューラが使用するセッションキューを参照します。
	 *
	 * @return session queue
	 */
	public SessionQueue getSessionQueue(){
		return queue;
	}

}
