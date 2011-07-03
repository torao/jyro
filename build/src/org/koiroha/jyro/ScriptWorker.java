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

import java.io.File;

import javax.script.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// ScriptWorker: Script Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The worker class for variable scripts these are supported by Java Scrpting
 * API.
 *
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/02 Java SE 6
 */
public class ScriptWorker implements Worker {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(ScriptWorker.class);

	// ======================================================================
	// Static Initializer
	// ======================================================================
	/**
	 *
	 */
	static {
	}

	// ======================================================================
	// Script Engine
	// ======================================================================
	/**
	 *
	 */
	private final ScriptEngine engine;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param loader class loader that used in script
	 * @param type MIME-Type or script name
	 * @param includes included script files
	 * @throws JyroException
	 */
	public ScriptWorker(ClassLoader loader, String type, File... includes) throws JyroException {

		// log output default supported script
		ScriptEngineManager manager = new ScriptEngineManager(loader);
		for(ScriptEngineFactory f: manager.getEngineFactories()){
			logger.debug(String.format(
				"%s %s (%s %s); name=[%s], mime-type=[%s], ext=[%s]",
				f.getLanguageName(), f.getLanguageVersion(),
				f.getEngineName(), f.getEngineVersion(),
				f.getNames(), f.getMimeTypes(), f.getExtensions()));
		}

		// determine script engine
		ScriptEngine engine = manager.getEngineByMimeType(type);
		if(engine == null){
			engine = manager.getEngineByName(type);
			if(engine == null){
				throw new JyroException("unsupported script type: " + type);
			}
		}
		this.engine = engine;

		// load all script files
		this.engine.
		return;
	}

	// ======================================================================
	// Execute Process
	// ======================================================================
	/**
	 * Execute this process with specified arguments. This method called in
	 * multi-thread environment.
	 *
	 * @param args arguments
	 * @return result
	*/
	public Object exec(Object... args);

}
