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

import javax.persistence.EntityManager;

import org.koiroha.jyrobot.model.JPAJob;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Request:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/09/03 Java SE 6
 */
public class Request {

	// ======================================================================
	// エンティティマネージャ
	// ======================================================================
	/**
	 * エンティティマネージャです。
	 */
	private final EntityManager manager;

	// ======================================================================
	// ジョブ
	// ======================================================================
	/**
	 * エンティティマネージャです。
	 */
	private final JPAJob job;

	// ======================================================================
	// エンティティマネージャ
	// ======================================================================
	/**
	 * エンティティマネージャです。
	 */
	public Request(EntityManager manager, JPAJob job) {
		this.manager = manager;
		this.job = job;
		return;
	}

}
