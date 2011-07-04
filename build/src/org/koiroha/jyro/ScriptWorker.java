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

import javax.script.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.util.IO;

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
	// Script Engine
	// ======================================================================
	/**
	 * Invocable script engine.
	 */
	private final Invocable engine;

	// ======================================================================
	// Function Name
	// ======================================================================
	/**
	 * Function name to call script.
	 */
	private final String function = "main";

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param loader class loader that used in script
	 * @param type MIME-Type or script name
	 * @param includes included script files
	 * @param charsets character set for each include files
	 * @throws JyroException fail to load script
	 */
	public ScriptWorker(ClassLoader loader, String type, File[] includes, String[] charsets) throws JyroException {

		// log output default supported script
		ScriptEngineManager manager = new ScriptEngineManager(loader);
		for(ScriptEngineFactory f: manager.getEngineFactories()){
			logger.debug(String.format(
				"%s %s (%s %s); name=%s, mime-type=%s, ext=%s",
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

		if(! (engine instanceof Invocable)){
			throw new JyroException("function invocation is not supported by script engine: " + type);
		}
		this.engine = (Invocable)engine;

		// load all script files and evaluate
		for(int i=0; i<includes.length; i++){
			File src = includes[i];
			String charset = charsets[i];
			Reader in = null;
			try {

				// open source file
				if(charset == null) {
					in = new FileReader(src);
				} else {
					in = new InputStreamReader(new FileInputStream(src), charset);
				}
				in = new BufferedReader(in);

				// evaluate source file
				engine.put(ScriptEngine.FILENAME, src.getAbsolutePath());
				engine.eval(in);
			} catch(FileNotFoundException ex){
				logger.warn("script file not found: " + src);
			} catch(Exception ex){
				throw new JyroException("fail to evaluate script: " + src, ex);
			} finally {
				IO.close(in);
			}
		}

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
	 * @return script result
	 * @throws WorkerException fail to execute script
	*/
	@Override
	public Object exec(Object... args) throws WorkerException {
		try {
			return engine.invokeFunction(function, args);
		} catch(NoSuchMethodException ex){
			throw new WorkerException("function " + function + " not defined in script", ex);
		} catch(ScriptException ex){
			throw new WorkerException("invalid script", ex);
		}
	}

}
