/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.script

import scala.actors._

import javax.script._

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Batch: 
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * This actor execute a script.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/21 Java SE 6
 */
class Batch(val script:ScriptEngine) extends Actor {

  // ========================================================================
  // Script
  // ========================================================================
  /**
   * Script to execute on this batch.
   */

  // ========================================================================
  // Configuration XML
  // ========================================================================
  /**
   * Configuration XML of script context.
   */
  def act = loop {
    react {
      case s => 
    }
  }

}