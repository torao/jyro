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

import scala.collection.JavaConversions._
import scala.xml._

import java.io._
import javax.script._

import org.apache.log4j.Logger

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Configuration: Script Configuration
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * This class defines scripts, database connection resources, ldap, message
 * queue or else
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/20 Java SE 6
 */
class Configuration(private val conf:File) {

  // ========================================================================
  // Configuration XML
  // ========================================================================
  /**
   * Configuration XML of script context.
   */
  val xml = Configuration.load(conf)

  // ========================================================================
  // Script Engine
  // ========================================================================
  /**
   * Script engine of this configuration.
   */

}


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Configuration: Script Configuration
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * This class defines scripts, database connection resources, ldap, message
 * queue or else
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/20 Java SE 6
 */
object Configuration {

  // ========================================================================
  // Log Output
  // ========================================================================
  /**
   * Log output of this object and class.
   */
  private val logger = Logger.getLogger(Configuration.getClass)

  // ========================================================================
  // Script Engine Manager
  // ========================================================================
  /**
   * Script engine manager to be used by application.
   */
  private val manager = new ScriptEngineManager()

  // report all available script engines in this environment
  for(f <- manager.getEngineFactories){
    logger.info(String.format("%s %s (%s %s), content-type=%s, extension=%s, language=%s",
      f.getLanguageName, f.getLanguageVersion,
      f.getEngineName, f.getEngineVersion,
      f.getMimeTypes, f.getExtensions, f.getNames,
      f.getScriptEngine.asInstanceOf[Invocable]))
  }

  // ========================================================================
  // Load XML File
  // ========================================================================
  /**
   * Load specified xml file. This method is defined because original
   * {@code XML.load(file:File)} will not close stream while gc.
   * @param conf xml file
   * @return xml element
   */
  private def load(conf:File):Elem = {
    var in:InputStream = null;
    try {
    	in = new BufferedInputStream(new FileInputStream(conf))
    	return XML.load(in)
    } finally {
    	org.koiroha.jyro.util.Util.close(in)
    }
  }
}
