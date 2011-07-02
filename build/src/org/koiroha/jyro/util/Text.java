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

import java.util.Map;
import java.util.regex.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Text: Text Utility Class
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Utility class for text.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
public final class Text {

	// ======================================================================
	// Placeholder Pattern
	// ======================================================================
	/**
	 * Placeholder pattern to format string.
	 */
	private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([\\w\\.]+)(:[^\\}]*)\\}");

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * The constructor is hidden in this class.
	 */
	private Text() {
		return;
	}

	// ======================================================================
	// Format String
	// ======================================================================
	/**
	 * Replace ${foo.bar} with specified parameter map. "$$" can escape
	 * single dollar sign.
	 * ${name:default} or $name
	 * 
	 * @param fmt format
	 * @param param format parameters
	 * @return formatted string
	 */
	public static String format(String fmt, Map<String,String> param){
		StringBuffer buffer = new StringBuffer();
		Matcher matcher = PLACEHOLDER.matcher(fmt);
		int begin = 0;
		while(matcher.find(begin)){
			buffer.append(fmt, begin, matcher.start());
			String name = matcher.group(1);
			String def = matcher.group(2);
			if(param.containsKey(name)){
				buffer.append(param.get(name));
			} else if(def != null && def.length() != 0){
				buffer.append(def.substring(1));
			} else {
				buffer.append(matcher.group(0));
			}
			begin = matcher.end();
		}

		// append left characters
		if(begin != fmt.length()){
			buffer.append(fmt, begin, fmt.length());
		}
		return buffer.toString();
	}

}
