/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.util;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// IO: I/O Utility
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Utility class for I/O operation
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
public final class IO {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(IO.class);

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * Constructor is hidden in class.
	 */
	private IO() {
		return;
	}

	// ======================================================================
	// Retrieve Filename Extension
	// ======================================================================
	/**
	 * Retrieve extension part of specified filename.
	 *
	 * @param path path of file
	 * @return file extension
	 */
	public static String getExtension(String path){
		int sep = path.lastIndexOf('/');
		if(sep >= 0){
			path = path.substring(sep + 1);
		}
		sep = path.lastIndexOf(File.separatorChar);
		if(sep >= 0){
			path = path.substring(sep + 1);
		}

		sep = path.lastIndexOf('.');
		if(sep < 0){
			return "";
		}
		return path.substring(sep + 1);
	}

	// ======================================================================
	// Retrieve File Set
	// ======================================================================
	/**
	 * Retrieve fileset for specified wildcard. The path parameter may
	 * contains "*" as sequence of any character, and "**" as subdirectories.
	 * <p>
	 * For example, if the files "f1", "f2", "d/f3" exists in base directory,
	 * pattern "f2" matches "f2", "f*" matches "f1" and "f2", "**<!---->/f*"
	 * matches "f1", "f2" and "d/f3".
	 *
	 * @param base base directory
	 * @param path path that may contains wildcard
	 * @return iterable of file
	 * @throws IOException if invalid path
	 */
	public static Iterable<File> fileSet(File base, String path) throws IOException{

		// if path does'nt contain wildcard
		int wc = path.indexOf('*');
		if(wc < 0){
			File file = new File(base, path);
			if(! file.isFile()){
				return Collections.emptyList();
			}
			return Arrays.asList(file);
		}

		// find most near separator position from wildcard
		int sep = 0;
		for(int i=0; i<wc; i++){
			char ch = path.charAt(wc - 1 - i);
			if(ch == '/' || ch == '\\' || ch == File.separatorChar){
				sep = wc - i;
				break;
			}
		}

		// retrieve prefix directory to find files shortly
		String prefix = path.substring(0, sep);
		String search = path.substring(sep);
		File start = new File(base, prefix).getCanonicalFile();
		if(! start.isDirectory()){
			return Collections.emptyList();
		}

		// split path to components
		List<String> cmp = new ArrayList<String>();
		for(String c: search.split("[/\\\\]+")){
			if(c.length() == 0){
				/* do nothing */
			} else if(c.equals("**") && cmp.size()!=0 && cmp.get(cmp.size()-1).equals("**")){
				/* do nothing */
			} else {
				cmp.add(c);
			}
		}
		if(logger.isDebugEnabled()){
			logger.trace("\"" + path + "\" separeted to: " + start + File.separator + cmp);
		}

		List<File> list = new ArrayList<File>();
		fileSet(list, start, cmp.toArray(new String[cmp.size()]), 0);
		return list;
	}

	// ======================================================================
	// Retrieve File Set
	// ======================================================================
	/**
	 * Retrieve "foo\bar\**\*.js" style file set of base directory.
	 *
	 * @param list container to add match file
	 * @param dir directory
	 * @param components split path components
	 * @param index index of components
	 */
	private static void fileSet(List<File> list, File dir, String[] components, int index){
		assert(dir.isDirectory());
		String name = components[index];
		boolean last = (index+1 == components.length);

		// directory wildcard specified
		if(name.equals("**")){
			if(index+1 < components.length){
				fileSet(list, dir, components, index+1);
			}

			// retrieve subdirectories
			File[] subdirs = dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) { return f.isDirectory(); }
			});

			// recursive execute to subdirectories
			for(File d: subdirs){
				fileSet(list, d, components, index);
			}
			return;
		}

		// convert wildcard to regex pattern
		StringBuilder regex = new StringBuilder();
		StringTokenizer tk = new StringTokenizer(name, "*", true);
		while(tk.hasMoreTokens()){
			String e = tk.nextToken();
			if(e.equals("*")){
				regex.append(".*");
			} else {
				regex.append(Pattern.quote(e));
			}
		}

		// retrieve match files or recursive call if directory
		Pattern pattern = Pattern.compile(regex.toString());
		File[] files = dir.listFiles();
		if(files != null){
			for(File f: files){
				if(pattern.matcher(f.getName()).matches()){
					if(!last && f.isDirectory()){
						fileSet(list, f, components, index+1);
					} else if(last && f.isFile()){
						list.add(f);
					}
				}
			}
		}
		return;
	}

	// ======================================================================
	// Close Stream
	// ======================================================================
	/**
	 * Close specified stream quietly. Only error level log will be output if
	 * fail to close stream.
	 *
	 * @param o stream to close, or nothing to do in case null
	 */
	public static void close(Closeable o){
		if(o != null){
			try {
				o.close();
			} catch(IOException ex){
				logger.error("fail to close stream", ex);
			}
		}
		return;
	}

}
