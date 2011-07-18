/* **************************************************************************
 * Copyright (C) 2008 BJoRFUAN. All Right Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * BSD License, and comes with NO WARRANTY.
 *
 *                                                 torao <torao@bjorfuan.com>
 *                                                       http://www.moyo.biz/
 * $Id:$
*/
package org.koiroha.jyro.util;

import java.io.*;
import java.text.DateFormat;
import java.util.*;

import org.apache.log4j.Logger;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Dependency:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/17 Java SE 6
 */
public class Dependency implements Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Dependency.class);

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 */
	private final Map<File,Long> dependency = new HashMap<File, Long>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 */
	public Dependency() {
		return;
	}

	// ======================================================================
	// Add File
	// ======================================================================
	/**
	 * Add specified file to this dependency.
	 *
	 * @param file file
	 */
	public void add(File file){
		dependency.put(file, file.lastModified());
		return;
	}

	// ======================================================================
	// Add Files
	// ======================================================================
	/**
	 * Add specified files to this dependency.
	 *
	 * @param files file collections
	 */
	public void add(Collection<File> file){
		for(File f: file){
			dependency.put(f, f.lastModified());
		}
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param file file
	 */
	public boolean modified(){
		for(File file: dependency.keySet()){
			long cur = file.lastModified();
			long att = dependency.get(file);
			if(cur != att){
				if(logger.isDebugEnabled()){
					DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);
					logger.debug("file modification detected: " + file + " {" + df.format(new Date(att)) + " -> " + df.format(new Date(cur)) + "}");
				}
				return true;
			}
		}
		return false;
	}

}
