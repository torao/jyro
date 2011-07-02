/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.cell;

import java.io.Serializable;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Process: Job Process
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The class to execute some process asynchronously.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/30 Java SE 6
 * @param <T> result type of execution
 */
public interface Process<T> extends Serializable {

	// ======================================================================
	// Execute
	// ======================================================================
	/**
	 * Execute some process.
	 * 
	 * @return result
	*/
	public T execute();

}
