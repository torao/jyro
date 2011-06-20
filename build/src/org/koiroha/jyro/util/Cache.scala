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

import scala.reflect.BeanProperty
import java.net.URI
import java.util._
import java.util.concurrent.locks._
import java.io._

import org.apache.log4j._

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Cache: Cache
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Class to cache variable instance on Java VM heap. This will detect
 * resource modification and reload internally.
 * 
 * @author takami torao
*/
class Cache[T] {

  // ========================================================================
  // Log Output
  // ========================================================================
  /**
   * Log output of this class.
   */
  private val logger = Logger.getLogger(classOf[Cache[T]])

  // ========================================================================
  // Cache
  // ========================================================================
  /**
   * URI-Cache mapping.
   */
  private val cache:Map[URI,Entry[T]] = new HashMap[URI,Entry[T]]

  // ========================================================================
  // Lock
  // ========================================================================
  /**
   * Access lock for uri-cache mapping.
   */
  private val lock = new ReentrantReadWriteLock(true)

  // ========================================================================
  // Resource Reader
  // ========================================================================
  /**
   * Default function to read resource from URI.
   */
  var read:(URI, Long) => (InputStream, Long) = null

  // ========================================================================
  // Cache Builder
  // ========================================================================
  /**
   * Default function to build cache instance from specified stream.
   */
  var build:(InputStream) => T = null

  // ========================================================================
  // Validation Span
  // ========================================================================
  /**
   * Re-validation interval for resource timestamp in millseconds from last
   * validation.
   */
  var validation:Long = 1000

  // ========================================================================
  // Set Resource Reader
  // ========================================================================
  /**
   * Set resource reader that create InputStream from some uri.
   * 
   * @param raed function of resource reader
   */
  def setRead(read:(URI, Long) => (InputStream, Long)):Unit = {
    this.read = read
    return
  }

  // ========================================================================
  // Set Cache Builder
  // ========================================================================
  /**
   * Set cache builder function to create instance from InputStream
   * 
   * @param build function of cache builder
   */
  def setBuild(build:(InputStream) => T):Unit = {
    this.build = build
    return
  }

  // ========================================================================
  // Retrieve Instance
  // ========================================================================
  /**
   * Retrieve cached instance from specified uri.
   * 
   * @param uri resource uri
   * @return cached instance
   */
  def get(uri:URI):T = {
    return get(uri, read, build)
  }

  // ========================================================================
  // Retrieve Cached Instance
  // ========================================================================
  /**
   * Retrieve cached instance from specified uri. If cache already 
   * 
   * @param uri resource uri
   * @return cached instance
   */
  def get(uri:URI, read:(URI,Long)=>(InputStream,Long), build:(InputStream)=>T):T = {
    val entry = cache.get(uri)
    if(entry == null){
      throw new NoSuchElementException(uri.toString())
    }
    return entry.get(read, build)
  }

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  // Builder: Builder Utility Functions
  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  /**
   * The collection of general purpose builder functions for
   * {@code Cache.setBuild()}.
   */
  object Builder {

    // ======================================================================
    // Properties
    // ======================================================================
    /**
     * Properties builder function.
     * 
     * @param in input stream
     * @return properties
     */
    def ofProperties(in:InputStream): java.util.Properties = {
      val prop = new java.util.Properties()
      prop.load(in)
      return prop
    }

  }

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  // Reader: Reader Utility Functions
  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  /**
   * The collection of general purpose resource reader functions for 
   * {@code Cache.setRead()}.
   */
  object Reader {

    // ======================================================================
    // Properties
    // ======================================================================
    /**
     * Properties builder function.
     * 
     * @param in input stream
     * @return properties
     */
    def ofProperties(in:InputStream): java.util.Properties = {
      val prop = new java.util.Properties()
      prop.load(in)
      return prop
    }

  }

  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  // Entry: Cache Entry
  // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  /**
   * 
   */
  private class Entry[T](val uri:URI){

    /**
     * Internal cached object built from resource specified uri of this entry.
     */
    private var cache:T = _

    /**
     * Timestamp of resource that build cache.
     */
    private var lastModified:Long = 0

    /**
     * Recent validation timestamp of localsystem.
     */
    private var validate:Long = 0

    // ======================================================================
    // Retrieve Cach Object
    // ======================================================================
    /**
     * Retrieve cached object of this entry. In case modification detected,
     */
    def get(read:(URI,Long)=>(InputStream,Long), build:(InputStream)=>T):T = synchronized {

      // return cache if not pass from recent validation
      val now = System.currentTimeMillis
      if(validate + validation < now){
        return cache
      }

      // return cache if not modified
      val (in, mod) = read(uri, lastModified)
      if(in == null){
    	validate = now
        return cache
      }

      // rebuild cache and keep modified timestamp
      try {
        cache = build(in)
        validate = now
        lastModified = mod
        logger.debug("")
      } finally {
        Util.close(in)
      }
      return cache
    }
  }

}