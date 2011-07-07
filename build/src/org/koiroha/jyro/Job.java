/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro;

import java.io.Serializable;
import java.util.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Job:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @author takami torao
 */
public final class Job implements Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Job Name
	// ======================================================================
	/**
	 * The name of this job.
	 */
	private final String name;

	// ======================================================================
	// Attributes
	// ======================================================================
	/**
	 * Attributes of this job mapped by key.
	 */
	private final Map<String,String> attributes = new HashMap<String,String>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Create job instance by to specify job name.
	 *
	 * @param name name of this job
	 */
	public Job(String name) {
		this.name = name;
		return;
	}

	// ======================================================================
	// Retrieve Job Name
	// ======================================================================
	/**
	 * Retrieve the name of this job. Note that the job name does not
	 * identify each of its instance.
	 *
	 * @return the name of this job
	 */
	public String getName() {
		return name;
	}

	// ======================================================================
	// Retrieve Job Attribute
	// ======================================================================
	/**
	 * Retrieve attribute value of this job associated with specified key.
	 * If no value found, the null will return.
	 *
	 * @param key key of attribute
	 * @return value of attribute
	 */
	public String getAttribute(String key){
		return attributes.get(key);
	}

	// ======================================================================
	// Set Job Attribute
	// ======================================================================
	/**
	 * Set attribute value of this job. If you specify null as value, map
	 * entry removed.
	 *
	 * @param key key of attribute
	 * @param value of attribute
	 * @return old value for key
	 */
	public String setAttribute(String key, String value){
		if(value == null){
			return attributes.remove(key);
		}
		return attributes.put(key, value);
	}

	// ======================================================================
	// Retrieve Hash Value
	// ======================================================================
	/**
	 * Retrieve hash value of this job instance.
	 *
	 * @return hash value
	*/
	@Override
	public int hashCode() {
		return name.hashCode() + attributes.size();
	}

	// ======================================================================
	// Evaluate Equality
	// ======================================================================
	/**
	 * Evaluate equality for specified object.
	 *
	 * @param obj object
	 * @return true if equals to obj
	*/
	@Override
	public boolean equals(Object obj) {

		// false if instance is not Job
		if(! (obj instanceof Job)){
			return false;
		}
		Job other = (Job)obj;

		// false if job name not matches
		if(! this.name.equals(other.name)){
			return false;
		}

		// false if attribute count not matches
		if(this.attributes.size() != other.attributes.size()){
			return false;
		}

		// false if attribute value not matches
		for(Map.Entry<String,String> e: this.attributes.entrySet()){
			if(e.getValue().equals(other.attributes.get(e.getKey()))){
				return false;
			}
		}
		return true;
	}

}
