/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.korioha.jyro

import java.io._

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyro: Application Class
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The class of Jyro application instance.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/20 Java SE 6
 */
class Jyro(dir:File) {
	
  // ========================================================================
  // Script Directory
  // ========================================================================
  /**
   * Script directory this application uses.
   */
  def scriptDirectory = { new File(dir, "scripts") }

}

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyro: Application Object
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Jyro application core object.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/20 Java SE 6
 */
object Jyro {

  // ========================================================================
  // Version Resource
  // ========================================================================
  /**
   * Version resource bundle of Jyro.
   */
  private val res = java.util.ResourceBundle.getBundle("org.koiroha.jyro.version");

  // ========================================================================
  // Application ID
  // ========================================================================
  /**
   * Application ID to be able to use file or directory name, part of uri
   * and so on.
   */
  def id = { res.getString("id") }

  // ========================================================================
  // Version
  // ========================================================================
  /**
   * The three numbers separated with period that specifies version of Jyro
   * such as "1.0.9".
   */
  def version = { res.getString("version") }

  // ========================================================================
  // Build Number
  // ========================================================================
  /**
   * Read build number from application bundle resource and return.
   */
  def build = { res.getString("build") }

  // ========================================================================
  // Start Application
  // ========================================================================
  /**
   * Start Jyro application.
   * 
   * @param args commandline parameter
   */
  def main(args:Array[String]):Unit = {
	  return
  }

}