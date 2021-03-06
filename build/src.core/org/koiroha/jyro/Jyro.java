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

import java.lang.reflect.Method;
import java.util.ResourceBundle;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyro: Constants and Functions
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Common constant values and functions in Jyro.
 *
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
	 * Application ID that can use for such as file, directory name or part
	 * of uri.
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
	 * The build number of current Jyro module.
	 */
	public static final String BUILD;

	// ======================================================================
	// Variable Name
	// ======================================================================
	/**
	 * Common variable name to find Jyro home directory from system
	 * properties.
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
	// Constructor
	// ======================================================================
	/**
	 * Constructor is hidden in class.
	 */
	private Jyro() {
		return;
	}

	// ======================================================================
	// Refer Function Name
	// ======================================================================
	/**
	 * Refer distributed function name of specified method. If the method
	 * is not distributed function (no {@link Distribute} present on method),
	 * then null will return.
	 *
	 * @param method method
	 * @return distributed function name
	 */
	public static String getFunctionName(Method method){
		Distribute dist = method.getAnnotation(Distribute.class);
		if(dist == null){
			return null;
		}
		String name = dist.name();
		if(name == null || name.length() == 0){
			name = method.getDeclaringClass().getCanonicalName() + "." + method.getName();
		}
		return name;
	}

}
