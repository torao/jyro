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

import java.lang.reflect.*;
import java.util.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Worker: Node Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Worker process bound to node.
 *
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/02 Java SE 6
 */
public class Worker {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(Worker.class);

	// ======================================================================
	// Worker Context
	// ======================================================================
	/**
	 * The context of this worker implementation.
	 */
	private WorkerContext context = null;

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
	 * Default constructor called dynamically.
	 */
	public Worker() {
		return;
	}

	// ======================================================================
	// initialize worker
	// ======================================================================
	/**
	 * Initialize this worker.
	 */
	public void init(){
		logger.debug("init()");
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
		return context;
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
	public final void setContext(WorkerContext context) throws IllegalStateException{
		if(this.context != null){
			throw new IllegalStateException("context already specified");
		}
		this.context = context;

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
	// Retrieve Interfaces
	// ======================================================================
	/**
	 * Retrieve distributed interfaces of this worker.
	 *
	 * @param context worker context
	 */
	public Object execute(Job job) throws JyroException {

		// search distributed method on this instance
		String func = job.getFunction();
		Method method = functions.get(func);
		try {
			return method.invoke(this, job.getArguments());
		} catch(InvocationTargetException ex){
			if(ex.getCause() instanceof JyroException){
				throw (JyroException)ex.getCause();
			}
			if(ex.getCause() instanceof RuntimeException){
				throw (RuntimeException)ex.getCause();
			}
			if(ex.getCause() instanceof Error){
				throw (Error)ex.getCause();
			}
			throw new JyroException(ex);
		} catch(IllegalAccessException ex){
			throw new JyroException(ex);
		}
	}

}
