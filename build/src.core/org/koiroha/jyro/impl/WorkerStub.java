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

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Worker: Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Worker process bound to node.
 *
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/02 Java SE 6
 */
public class WorkerStub {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(WorkerStub.class);

	// ======================================================================
	// Distribute Function Name Pattern
	// ======================================================================
	/**
	 * Regular expression to validate distribute function name.
	 */
	private static final Pattern DIST_FUNC_NAME = Pattern.compile(".+");

	// ======================================================================
	// Distribute Function Name Pattern
	// ======================================================================
	/**
	 * Regular expression to validate distribute function name.
	 */
	private static final Set<Class<?>> DIST_ARG_TYPE = new HashSet<Class<?>>();

	static {
		DIST_ARG_TYPE.add(boolean.class);
		DIST_ARG_TYPE.add(byte.class);
		DIST_ARG_TYPE.add(short.class);
		DIST_ARG_TYPE.add(int.class);
		DIST_ARG_TYPE.add(long.class);
		DIST_ARG_TYPE.add(float.class);
		DIST_ARG_TYPE.add(double.class);
		DIST_ARG_TYPE.add(char.class);
		DIST_ARG_TYPE.add(Boolean.class);
		DIST_ARG_TYPE.add(Byte.class);
		DIST_ARG_TYPE.add(Short.class);
		DIST_ARG_TYPE.add(Integer.class);
		DIST_ARG_TYPE.add(Long.class);
		DIST_ARG_TYPE.add(Float.class);
		DIST_ARG_TYPE.add(Double.class);
		DIST_ARG_TYPE.add(Character.class);
		DIST_ARG_TYPE.add(String.class);
		DIST_ARG_TYPE.add(boolean.class);
		DIST_ARG_TYPE.add(boolean.class);
		DIST_ARG_TYPE.add(boolean.class);
		DIST_ARG_TYPE.add(boolean.class);
	}

	// ======================================================================
	// Worker
	// ======================================================================
	/**
	 * The worker of this stub handling.
	 */
	private final Worker worker;

	// ======================================================================
	// Function Mappings
	// ======================================================================
	/**
	 * Function name to method mappings.
	 */
	private final Map<String,Method> functions = new HashMap<String, Method>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param worker worker of this stub
	 */
	public WorkerStub(Worker worker) {
		this.worker = worker;

		// build function mappings
		for(Method m: getClass().getMethods()){
			Distribute d = m.getAnnotation(Distribute.class);
			if(d != null){
				String f = d.value();
				if(f == null || f.length() == 0){
					f = m.getDeclaringClass().getCanonicalName() + "." + m.getName();
				}
				this.functions.put(f, m);
			}
		}
		return;
	}

	// ======================================================================
	// initialize worker
	// ======================================================================
	/**
	 * Initialize this worker.
	 */
	public void init(){
		worker.init();
		return;
	}

	// ======================================================================
	// Refer Context
	// ======================================================================
	/**
	 * Refer context of this worker.
	 *
	 * @return worker context
	 */
	public WorkerContext getContext() {
		return worker.getContext();
	}

	// ======================================================================
	// Set Context
	// ======================================================================
	/**
	 * Set specified context to this worker instance. This method is called
	 * framework and can call only once for instance.
	 *
	 * @param context worker context
	 */
	public final void setContext(WorkerContext context) {
		worker.setContext(context);
		return;
	}

	// ======================================================================
	// Retrieve Function Names
	// ======================================================================
	/**
	 * Retrieve all function names implemented by this instance.
	 * The default behavior of this method is to return the names of methods
	 * with {@link Distribute} annotation.
	 *
	 * @return function names
	 */
	public String[] getFunctions(){
		List<String> dist = new ArrayList<String>(functions.keySet());
		return dist.toArray(new String[dist.size()]);
	}

	// ======================================================================
	// Execute Distribute Method
	// ======================================================================
	/**
	 * Execute distribute method on this worker and return result.
	 *
	 * @param job job to execute
	 * @return execution result
	 * @throws JyroException if fail to execute
	 */
	public Object execute(Job job) throws JyroException {
		logger.debug("execute(" + job + ")");

		// search distributed method on this instance
		String func = job.getFunction();
		Method method = functions.get(func);
		if(method == null){
			logger.fatal("function not found: " + func + " on worker " + worker.getClass().getName());
			throw new FunctionNotFoundException("function \"" + func + "\" not found on worker " + worker.getClass().getName());
		}

		// execute distributed method
		try {
			return method.invoke(worker, job.getArguments());
		} catch(InvocationTargetException ex){

			// through JyroException
			if(ex.getCause() instanceof JyroException){
				throw (JyroException)ex.getCause();
			}

			// through RuntimeException
			if(ex.getCause() instanceof RuntimeException){
				throw (RuntimeException)ex.getCause();
			}

			// through Error
			if(ex.getCause() instanceof Error){
				throw (Error)ex.getCause();
			}

			throw new JyroException(ex);
		} catch(IllegalAccessException ex){
			throw new JyroException(ex);
		}
	}

	// ======================================================================
	// Validation Check
	// ======================================================================
	/**
	 * Validate specified distribute function.
	 *
	 * @param name distribute function name
	 * @param method method
	 * @throws JyroException if fail to execute
	 */
	private static void validate(String name, Method method) throws JyroException {
		if(! DIST_FUNC_NAME.matcher(name).matches()){
			throw new JyroException("invalid method name: " + name);
		}
		return;
	}

	// ======================================================================
	// Validation Check
	// ======================================================================
	/**
	 * Validate specified distribute function.
	 *
	 * @param name distribute function name
	 * @param method method
	 * @throws JyroException if fail to execute
	 */
	private static void validateArgs(Object arg) throws JyroException {
		if(! DIST_FUNC_NAME.matcher(name).matches()){
			throw new JyroException("invalid method name: " + name);
		}
		return;
	}

}
