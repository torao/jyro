/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.cell;

import java.io.Serializable;
import java.util.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Job: 
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/06/28 Java SE 6
 */
public class Job implements Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Timestamp
	// ======================================================================
	/**
	 * Construction timestamp of this job.
	 */
	private final long timestamp;

	// ======================================================================
	// Attributes
	// ======================================================================
	/**
	 * Application-depend attribute.
	 */
	private final Map<String,String> attribute = new HashMap<String,String>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * 
	 */
	public Job() {
		this.timestamp = System.currentTimeMillis();
		return;
	}

	// ======================================================================
	// Retrieve Timestamp
	// ======================================================================
	/**
	 * Retrieve creation timestamp of this job.
	 * 
	 * @return creation timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	// ======================================================================
	// Retrieve Attribute
	// ======================================================================
	/**
	 * Retrieve application-specified attribute for this job. Return null if
	 * value is not exist.
	 * 
	 * @param name attribute name
	 * @return attribute value
	 */
	public String getAttribute(String name) {
		return attribute.get(name);
	}

	// ======================================================================
	// Set Attribute
	// ======================================================================
	/**
	 * Set application-specified attribute for this job.
	 * <p>
	 * @param name attribute name
	 * @param value attribute value
	 * @return old value of specified name
	 */
	public String setAttribute(String name, String value) {
		return attribute.put(name, value);
	}
	
}
