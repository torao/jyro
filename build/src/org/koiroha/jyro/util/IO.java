/* **************************************************************************
 * Copyright (C) 2008 BJoRFUAN. All Right Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * BSD License, and comes with NO WARRANTY.
 *
 *                                                 torao <torao@bjorfuan.com>
 *                                                       http://www.moyo.biz/
 * $Id:$
*/
package org.koiroha.jyro.util;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// IO:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
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
	// Retrieve File Set
	// ======================================================================
	/**
	 * Retrieve "foo\bar\**\*.js" style file set of base directory.
	 *
	 * @param base base directory
	 * @param path path that may contains wildcard
	 * @return iterable of file
	 */
	public static Iterable<File> fileSet(File base, String path) throws IOException{
		URI b = base.toURI();
		URI uri = b.resolve(path).normalize();
		path = uri.getPath().replace('/', File.separatorChar);

		// split path to components
		StringTokenizer tk = new StringTokenizer(path, File.separator);
		String[] cmp = new String[tk.countTokens()];
		for(int i=0; tk.hasMoreTokens(); i++){
			cmp[i] = tk.nextToken();
		}

		List<File> list = new ArrayList<File>();
		for(File root: File.listRoots()){
			if(root.toString().equals(File.separator)				// unix style "/"
			|| root.toString().equals(cmp[0] + File.separator))		// windows style "C:\"
			{
				fileSet(list, root, cmp, 1);
			}
		}
		return list;
	}

	// ======================================================================
	// Retrieve File Set
	// ======================================================================
	/**
	 * Retrieve "foo\bar\**\*.js" style file set of base directory.
	 *
	 * @param base base directory
	 * @param path path that may contains wildcard
	 * @return iterable of file
	 */
	private static void fileSet(List<File> list, File dir, String[] components, int index){
		assert(dir.isDirectory());
		String name = components[index];
		boolean last = (index+1 == components.length);

		if(name.equals("**")){
			File[] subdirs = dir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) { return f.isDirectory(); }
			});
			for(File d: subdirs){
				fileSet(list, d, components, index);
			}
			for(File d: subdirs){
				fileSet(list, d, components, index+1);
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
		for(File f: dir.listFiles()){
			if(pattern.matcher(f.getName()).matches()){
				if(!last && f.isDirectory()){
					fileSet(list, f, components, index+1);
				} else if(last && f.isFile()){
					list.add(f);
				}
			}
		}
		return;
	}

	// ======================================================================
	// Close Stream
	// ======================================================================
	/**
	 * Close specified stream quietly.
	 *
	 * @param o stream to close
	 */
	public static void close(Closeable o){
		if(o != null){
			try {
				o.close();
			} catch(IOException ex){
				logger.warn("fail to close stream", ex);
			}
		}
		return;
	}

}
