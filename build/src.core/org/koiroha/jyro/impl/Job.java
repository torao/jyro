/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.impl;

import java.io.Serializable;
import java.util.*;

import org.koiroha.jyro.util.Text;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Job: Job
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The job class to send and receive on {@link Bus}.
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
	// Function Name
	// ======================================================================
	/**
	 * Target function name to execute this job.
	 */
	private final String func;

	// ======================================================================
	// Method Arguments
	// ======================================================================
	/**
	 * Arguments to call method.
	 */
	private final Object[] args;

	// ======================================================================
	// Callback Function
	// ======================================================================
	/**
	 * Execution callback interface.
	 */
	private final Callback callback;

	// ======================================================================
	// Callback Function
	// ======================================================================
	/**
	 * Execution callback interface.
	 */
	private transient Map<String,Object> attributes = null;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Create job instance by to specify function without any attributes.
	 *
	 * @param method method to call as worker
	 * @param args method arguments
	 * @param callback callback
	 */
	public Job(String func, Object[] args, Callback callback) {
		this.func = func;
		this.args = args;
		this.callback = callback;
		return;
	}

	// ======================================================================
	// Retrieve Function Name
	// ======================================================================
	/**
	 * Retrieve function name of worker to execute this job.
	 *
	 * @return function name
	 */
	public String getFunction(){
		return func;
	}

	// ======================================================================
	// Retrieve Arguments
	// ======================================================================
	/**
	 * Retrieve arguments of worker to execute this job.
	 *
	 * @return arguments
	 */
	public Object[] getArguments(){
		return args;
	}

	// ======================================================================
	// Refer Callback
	// ======================================================================
	/**
	 * Refer callback.
	 *
	 * @return callback object
	 */
	public Callback getCallback(){
		return callback;
	}

	// ======================================================================
	// Set Attribute
	// ======================================================================
	/**
	 *
	 * @return callback object
	 */
	public void setAttribute(String name, Object value){
		if(attributes == null){
			attributes = new HashMap<String, Object>();
		}
		attributes.put(name, value);
		return;
	}

	// ======================================================================
	// Set Attribute
	// ======================================================================
	/**
	 *
	 * @return callback object
	 */
	public Object getAttribute(String name){
		if(attributes == null){
			return null;
		}
		return attributes.get(name);
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
		StringBuilder buffer = new StringBuilder();
		buffer.append(func);
		buffer.append('(');
		for(int i=0; i<args.length; i++){
			if(i != 0){
				buffer.append(',');
			}
			buffer.append(Text.json(args[i]));
		}
		buffer.append(')');
		return buffer.toString();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Result:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 */
	public class Result implements Serializable {

		// ==================================================================
		// Serial Version
		// ==================================================================
		/**
		 * Serial version UID of this class.
		 */
		private static final long serialVersionUID = 1L;

		// ==================================================================
		// Result
		// ==================================================================
		/**
		 * Result of this job
		 */
		private final Object result;

		// ==================================================================
		// Exception
		// ==================================================================
		/**
		 * Exception of this job
		 */
		private final Throwable exception;

		// ==================================================================
		// Exception
		// ==================================================================
		/**
		 * Exception of this job
		 */
		public Result(Object result, Throwable exception){
			assert(result == null || exception == null);
			this.result = result;
			this.exception = exception;
			return;
		}

		// ==================================================================
		// Refer Job
		// ==================================================================
		/**
		 * Refer job of this result.
		 *
		 * @return job
		 */
		public Job getJob(){
			return Job.this;
		}

		// ==================================================================
		// Refer Result
		// ==================================================================
		/**
		 * Refer result of this job.
		 *
		 * @return result object
		 */
		public Object getResult(){
			return result;
		}

		// ==================================================================
		// Refer Exception
		// ==================================================================
		/**
		 * Refer exception of this job.
		 *
		 * @return exception object
		 */
		public Throwable getException(){
			return exception;
		}

		// ==================================================================
		// Callback
		// ==================================================================
		/**
		 * Execute callback in current thread.
		 */
		public void callback(){
			Job job = getJob();
			Job.Callback callback = job.getCallback();
			if(callback != null){
				callback.callback(this);
			}
			return;
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Callback:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 */
	public interface Callback extends Serializable {

		// ==================================================================
		// Finish Callback
		// ==================================================================
		/**
		 * Callback to finish job execution.
		 *
		 * @param job target job instance
		 * @param result execution result
		 * @param ex exception if error occured
		*/
		public void callback(Result result);

	}

}
