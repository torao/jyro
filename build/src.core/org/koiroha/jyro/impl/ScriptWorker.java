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

import java.io.*;

import javax.script.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;
import org.koiroha.jyro.util.IO;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// ScriptWorker: Script Worker
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The worker class for variable scripts these are supported by Java Scrpting
 * API.
 *
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/02 Java SE 6
 */
public class ScriptWorker extends Worker {

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
	// Constructor
	// ======================================================================
	/**
	 * @param loader class loader that used in script
	 * @param type MIME-Type or script name
	 * @param includes included script files
	 * @param charsets character set for each include files
	 * @param content content of xml
	 * @throws JyroException fail to load script
	 */
	public ScriptWorker(ClassLoader loader, String type, File[] includes, String[] charsets, String content) throws JyroException {

		// log output default supported script
		ScriptEngineManager manager = new ScriptEngineManager(loader);
		for(ScriptEngineFactory f: manager.getEngineFactories()){
			logger.debug(String.format(
				"available %s %s (%s %s); name=%s, mime-type=%s, ext=%s",
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

				logger.debug("load script " + src + ",charset=" + charset);
			} catch(FileNotFoundException ex){
				logger.warn("script file not found: " + src);
			} catch(Exception ex){
				throw new JyroException("fail to evaluate script: " + src, ex);
			} finally {
				IO.close(in);
			}
		}

		try {
			engine.eval(content);
		} catch(Exception ex){
			throw new JyroException("fail to evaluate script", ex);
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
	 * @param job job argument
	 * @return script result
	 * @throws WorkerException fail to execute script
	*/
	@Override
	public void init(){
		((ScriptEngine)engine).put("jyro", getContext());
		return;
	}

	// ======================================================================
	// Execute Process
	// ======================================================================
	/**
	 * Execute this process with specified arguments. This method called in
	 * multi-thread environment.
	 *
	 * @param job job argument
	 * @return script result
	 * @throws WorkerException fail to execute script
	*/
	@Override
	public Object execute(Job job) throws JyroException {
		String func = job.getFunction();
		Object[] args = job.getArguments();
		try {
			return engine.invokeFunction(func, args);
		} catch(NoSuchMethodException ex){
			throw new JyroException("function " + func + " not defined in script", ex);
		} catch(ScriptException ex){
			throw new JyroException("unexpected script execution error", ex);
		}
	}

}
