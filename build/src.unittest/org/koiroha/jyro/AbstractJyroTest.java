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

import static org.junit.Assert.assertTrue;

import java.io.*;
import java.lang.reflect.*;


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// UtilityClassTest:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @author takami torao
 */
public class AbstractJyroTest {

	/**
	 * Test whether specified class declared as utility.
	 * @param clazz test class
	 */
	protected void testUtilityClass(Class<?> clazz){

		// Class should b declared as final.
		assertTrue("class is not declared as final", Modifier.isFinal(clazz.getModifiers()));

		// All constructors should be declared as private.
		for(Constructor<?> c: clazz.getConstructors()){
			assertTrue("constructor is not declared as private", Modifier.isPrivate(c.getModifiers()));
		}

		// All methods should be declared as static.
		for(Method m: clazz.getDeclaredMethods()){
			assertTrue("method is not declared as static", Modifier.isStatic(m.getModifiers()));
		}
		return;
	}

	/**
	 * Delete all files and subdirectories contains
	 * @param dir directory to remove
	 * @throws IOException if fail to remote
	 */
	protected static void deleteAll(File dir) throws IOException{
		File[] files = dir.listFiles();
		if(files != null){
			for(File f: files){
				if(f.isDirectory()){
					deleteAll(f);
				} else {
					if(! f.delete()){
						throw new IOException("cannot delete file: " + f);
					}
				}
			}
		}
		if(! dir.delete()){
			throw new IOException("cannot delete directory: " + dir);
		}
		return;
	}

}
