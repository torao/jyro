/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.util

import java.io._

import org.apache.log4j._

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Util: 
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 
 * @author t_takami
*/
object Util {

  /**
   * Log output of this class.
   */
  private val logger = Logger.getLogger(Util.getClass())

  // ========================================================================
  // Format Text
  // ========================================================================
  /**
   * Format 
   */
  def format(text:String, param:Map[String,String]):String = {
    val placeholder = 
    return ""
  }

  // ========================================================================
  // 
  // ========================================================================
  /**
   * Close specified stream quietly.
   * 
   * @param o closeable object
   */
  def close(o:Closeable):Unit = {
    if(o != null){
      try {
        o.close();
      } catch {
        case ex:IOException => logger.warn("fail to close stream", ex)
      }
      return
    }
  }

}