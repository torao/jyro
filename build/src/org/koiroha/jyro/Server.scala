/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Server: 
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 
 * @author takami torao
*/
class Server {

  // ========================================================================
  // Server Port
  // ========================================================================
  /**
   * Listening port of console for this server.
   */
  var port = Server.defaultPort

}

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Server: 
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 
 * @author takami torao
*/
object Server {

  // ========================================================================
  // Default Server Port
  // ========================================================================
  /**
   * Default server port {@value} for Jyro Console that accessed by browser.
   */
  val defaultPort = 2648

  // ========================================================================
  // Start Server Application
  // ========================================================================
  /**
   * Start standalone Jyro server.
   * @param args commandline parameter
   */
  def main(args:Array[String]):Unit = {
    val server = new Server()
    parseOptions(server, args.toList)
    return
  }

  // ========================================================================
  // Parse Commandline Parameters
  // ========================================================================
  /**
   * Parse commandline parameters and setup server.
   * 
   * @param server server to setup
   * @param args commandline arguments
   */
  private def parseOptions(server:Server, args:List[String]):Unit = {
    args match {
      case ("-p" | "--port") :: port :: rest =>
        server.port = port.toInt
        parseOptions(server, rest)
      case _ =>
        args
    }
    return
  }

  // ========================================================================
  // Parse Commandline Parameters
  // ========================================================================
  /**
   * Parse commandline parameters and setup server.
   * 
   * @param server server to setup
   * @param args commandline arguments
   */
  private def help():Unit = {
    import org.koiroha.jyro._
    System.err.printf(
"""Usage: %s [option]
Startup %s %s Server 

Server options:
  -p, --port   listening port (default %d)
""", Array(getClass.getName, Jyro.name, Jyro.version, defaultPort))
  }

}