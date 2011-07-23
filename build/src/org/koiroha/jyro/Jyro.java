/* **************************************************************************
 * Copyright (C) 2008 BJoRFUAN. All Right Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * BSD License, and comes with NO WARRANTY.
 *
 *                                                 torao <torao@bjorfuan.com>
 *                                                       http://www.moyo.biz/
 * $Id:$
*/
package org.koiroha.jyro;

import java.util.ResourceBundle;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyro:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/23 Java SE 6
 */
public final class Jyro {

	// ======================================================================
	// Application Name
	// ======================================================================
	/**
	 * Human readable application name.
	 */
	public static final String NAME;

	// ======================================================================
	// Application ID
	// ======================================================================
	/**
	 * Application ID to be able to use file or directory name, part of uri
	 * and so on.
	 */
	public static final String ID;

	// ======================================================================
	// Version
	// ======================================================================
	/**
	 * The three numbers separated with period that specifies version of Jyro
	 * such as "1.0.9".
	 */
	public static final String VERSION;

	// ======================================================================
	// Build Number
	// ======================================================================
	/**
	 * Read build number from application bundle resource and return.
	 */
	public static final String BUILD;

	// ======================================================================
	// Variable Name
	// ======================================================================
	/**
	 * Common variable name for Jyro home directory.
	 */
	public static final String JYRO_HOME = "jyro.home";

	// ======================================================================
	// Static Initializer
	// ======================================================================
	/**
	 * Read and set version constants.
	 */
	static {
		ResourceBundle res = ResourceBundle.getBundle("org.koiroha.jyro.version");
		NAME = res.getString("name");
		ID = res.getString("id");
		VERSION = res.getString("version");
		BUILD = res.getString("build");

		assert(VERSION.matches("\\d+\\.\\d+\\.\\d+"));
	}

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 * <p>
	 */
	public Jyro() {
		// TODO 自動生成されたコンストラクター・スタブ
		return;
	}

}
