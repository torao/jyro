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
import org.koiroha.jyro.impl.Job;

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
	// Distributed Methods
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

		// retrieve distributed methods
		for(Method method: getClass().getMethods()){
			String name = Jyro.getFunctionName(method);
			if(name != null){
				// TODO check duplicate name definition
				this.functions.put(name, method);
			}
		}
		return;
	}

	// ======================================================================
	// Initialize Worker
	// ======================================================================
	/**
	 * Initialize this worker.
	 */
	public void init() throws JyroException{
		logger.debug("init()");
		return;
	}

	// ======================================================================
	// Destroy Worker
	// ======================================================================
	/**
	 * Destroy this worker.
	 */
	public void destroy(){
		logger.debug("destroy()");
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
		return;
	}

	// ======================================================================
	// Retrieve Method Names
	// ======================================================================
	/**
	 * Retrieve all distributed methods in this instance.
	 * The default behavior of this method is to return methods with
	 * {@link Distribute} annotation.
	 *
	 * @return distributed methods
	 */
	public Method[] getDistributedMethods(){
		Set<Method> dist = new HashSet<Method>(functions.values());
		return dist.toArray(new Method[dist.size()]);
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
			logger.trace(method.toGenericString());
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
