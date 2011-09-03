/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro;

import java.lang.annotation.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Distribute: Job
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The marker annotation to specify distributed function on worker.
 *
 * @author takami torao
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Distribute {

	// ======================================================================
	// Distributed Function Name
	// ======================================================================
	/**
	 * Distributed function name.
	 * if empty string "" specified, the fully-qualified method name such as
	 * "org.koiroha.sample.SampleWorker.helloWorld" will be used.
	 */
	public String name();

	// ======================================================================
	// Distributed Function Name
	// ======================================================================
	/**
	 * Distributed function name.
	 * if empty string "" specified, the fully-qualified method name such as
	 * "org.koiroha.sample.SampleWorker.helloWorld" will be used.
	 */
	public String[] params();

}
