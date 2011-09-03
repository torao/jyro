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

import java.net.URI;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyrobotAdapter: Jyrobot アダプタ
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/09/03 Java SE 6
 */
public interface JyrobotAdapter {

	// ======================================================================
	// URL Filtering
	// ======================================================================
	/**
	 * 指定された URL を次回以降のクローリングに使用するかを判定します。
	*/
	public boolean accept(URI uri);

}
