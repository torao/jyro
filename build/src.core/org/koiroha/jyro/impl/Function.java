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

import org.koiroha.jyro.util.Text;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Function: Function
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * This class specifies function to call
 *
 * @author takami torao
 */
public final class Function implements Serializable {

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
	 * Function name.
	 */
	private final String func;

	// ======================================================================
	// Function Arguments
	// ======================================================================
	/**
	 * Arguments of function.
	 */
	private final Object[] args;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Create job instance by to specify function without any attributes.
	 *
	 * @param func function name
	 * @param args function arguments
	 */
	public Function(String func, Object[] args) {
		this.func = func;
		this.args = args;
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
	public String getName(){
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
		// Constructor
		// ==================================================================
		/**
		 * @param result result of execution, or null if exception occurred
		 * @param exception exception of execution, or null if success
		 */
		public Result(Object result, Throwable exception){
			assert(result == null || exception == null);
			this.result = result;
			this.exception = exception;
			return;
		}

		// ==================================================================
		// Refer Function
		// ==================================================================
		/**
		 * Refer job of this result.
		 *
		 * @return job
		 */
		public Function getFunction(){
			return Function.this;
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

	}

}
