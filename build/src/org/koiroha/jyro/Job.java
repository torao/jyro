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

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import org.koiroha.jyro.util.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Job: Job
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The job class to send or receive between {@link JobQueue}.
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
	// Name Pattern
	// ======================================================================
	/**
	 * The name of this job.
	 */
	private static final Pattern NAME = Pattern.compile("[^\"\'\\{\\}\\s]+");

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
	 * Create job instance by to specify job name without any attributes.
	 *
	 * @param name name of this job
	 */
	public Job(String name) {
		this(name, null);
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Create job instance by to specify job name and attributes.
	 *
	 * @param name name of this job
	 * @param attrs attribute set of this job
	 */
	public Job(String name, Map<String,String> attrs) {
		this.name = name;
		if(attrs != null){
			this.attributes.putAll(attrs);
		}

		// validate name restriction
		if(! NAME.matcher(name).matches()){
			throw new IllegalArgumentException("invalid job name: " + name);
		}
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
	// Export Job
	// ======================================================================
	/**
	 * Export contentt of this job to specified output.
	 *
	 * @param out output to export this instance
	 * @throws IOException if fail to output
	 * @see #parse(String)
	*/
	public void export(Appendable out) throws IOException {
		out.append(name);
		if(! attributes.isEmpty()){
			out.append('{');
			Iterator<String> it = attributes.keySet().iterator();
			while(it.hasNext()){
				String key = it.next();
				String value = attributes.get(key);
				out.append(key);
				out.append(':');
				Text.literize(out, value);
				if(it.hasNext()){
					out.append(',');
				}
			}
			out.append('}');
		}
		return;
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

	// ======================================================================
	// String
	// ======================================================================
	/**
	 * Refer string presentation of this job instance.
	 *
	 * @return string
	*/
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder(name);
		try {
			export(buffer);
		} catch(IOException ex){/* */}
		return buffer.toString();
	}

	// ======================================================================
	// Parse Text
	// ======================================================================
	/**
	 * Parse specified plain text and build job instance. The plain text
	 * must be formatted JSON-like as follows.
	 * <pre>
	 * name{attr1:"value1",attr2:"value2",...}
	 * </pre>
	 *
	 * @param job the job representation plain text
	 * @return job instance
	 * @throws ParseException fail to parse
	*/
	public static final Job parse(String job) throws ParseException{
		String name = job.trim();
		Map<String,String> attr = new HashMap<String,String>();
		int sep = job.indexOf('{');
		if(sep >= 0){

			// split name
			name = job.substring(0, sep).trim();

			// split attribute fields.
			job = job.substring(sep+1).trim();
			if(job.endsWith("}")){
				throw new ParseException("'}' expected on end of text: " + job);
			}
			job = job.substring(job.length()-1);

			// parse attribute field
			StringBuilder buffer = new StringBuilder(job);
			while(true){
				String id = parseIdentifier(buffer, ':');
				if(id == null){
					break;
				}
				String value = parseValue(buffer, ',');
				attr.put(id, value);
			}
		}
		return new Job(name, attr);
	}

	// ======================================================================
	// Parse Identifier
	// ======================================================================
	/**
	 * Parse and split identifier from head of buffer to delimiter.
	 *
	 * @param buffer buffer
	 * @param delim delimiter character
	 * @return identifier string, or null if buffer has no delimiter
	*/
	private static String parseIdentifier(StringBuilder buffer, char delim){
		int i = 0;
		while(true){
			if(i == buffer.length()){
				return null;
			}
			char ch = buffer.charAt(i);
			if(ch == delim){
				break;
			}
			i ++;
		}
		String id = buffer.substring(0, i);
		buffer.delete(0, i);
		return id.trim();
	}

	// ======================================================================
	// Parse Identifier
	// ======================================================================
	/**
	 * Parse and split identifier from head of buffer to delimiter.
	 *
	 * @param buffer buffer
	 * @param delim delimiter character
	 * @return identifier string, or null if buffer has no delimiter
	 * @throws ParseException
	*/
	private static String parseValue(StringBuilder buffer, char delim) throws ParseException{
		int i = 0;
		for(/* */; i<buffer.length(); i++){
			char ch = buffer.charAt(i);
			if(ch == '\\'){
				i ++;
			} else if(ch == delim){
				break;
			}
		}
		String value = buffer.substring(0, i);
		buffer.delete(0, i);
		return Text.unliterize(value.trim());
	}

}
