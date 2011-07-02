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

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Node: Node Interface
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/02 Java SE 6
 */
public interface Node {

	// ======================================================================
	// Retrieve Name
	// ======================================================================
	/**
	 * Retrieve task name of this node. The return value may be ununiquely in
	 * node container scope if same node running on context.
	 * 
	 * @return name of this node
	*/
	public String getTaskName();

	// ======================================================================
	// Retrieve 
	// ======================================================================
	/**
	 * 
	*/

}
