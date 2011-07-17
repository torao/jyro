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
import java.util.*;



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
	// Constructor
	// ======================================================================
	/**
	 * @param file file
	 */
	public void add(File file){
		dependency.put(file, file.lastModified());
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
			if(file.lastModified() != dependency.get(file)){
				return false;
			}
		}
		return true;
	}

}
