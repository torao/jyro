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

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// UserAgent: ユーザエージェント
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * ユーザエージェントを表すクラスです。
 *
 * @version
 * @author torao
 * @since 2011/09/16 jyro 1.0
 */
public abstract class UserAgent {

	// ======================================================================
	// User-Agent Profile
	// ======================================================================
	/**
	 * このユーザエージェントのプロフィールです。
	 */
	private final Profile profile;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * このユーザエージェントのプロフィールを指定して構築を行います。
	 *
	 * @param profile profile of this user-agent
	 */
	public UserAgent(Profile profile){
		this.profile = new Profile(profile);
		return;
	}

	// ======================================================================
	// Refer User-Agent Profile
	// ======================================================================
	/**
	 * このユーザエージェントのプロフィールを参照します。返値のプロフィールへの変更は
	 * このインスタンスに反映されます。
	 *
	 * @return profile of this user-agent
	 */
	public Profile getProfile() {
		return profile;
	}

	// ======================================================================
	// Create Session
	// ======================================================================
	/**
	 * このユーザエージェントから新しいセッションを作成します。
	 *
	 * @return 新規のセッション
	 */
	public abstract Session newSession();

}
