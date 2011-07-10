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

import java.io.IOException;
import java.util.*;
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
	private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([\\w\\.]+)(:[^\\}]*)?\\}");

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
	public static String format(String fmt, Properties param){
		@SuppressWarnings("unchecked")
		Map<String,String> map = (Map<String,String>)((Map<?,?>)param);
		return format(fmt, map);
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
			} else if(def != null){
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

	// ======================================================================
	// Literize JavaScript String
	// ======================================================================
	/**
	 * Literize specified text as JavaScript string. If null pass as text,
	 * string "null" will append to out.
	 *
	 * @param text text
	 * @return instance of out parameter
	 */
	public static String literize(String text){
		StringBuilder buffer = new StringBuilder();
		try {
			literize(buffer, text);
		} catch(IOException ex){
			throw new IllegalStateException("unexpected exception; this probably bug!: " + text, ex);
		}
		return buffer.toString();
	}

	// ======================================================================
	// Literize JavaScript String
	// ======================================================================
	/**
	 * Literize specified text as JavaScript string. If null pass as text,
	 * string "null" will append to out.
	 *
	 * @param out appendable to output
	 * @param text text
	 * @return instance of out parameter
	 * @throws IOException if fail to output
	 */
	public static Appendable literize(Appendable out, String text) throws IOException {

		// append null if null specified
		if(text == null){
			out.append("null");
			return out;
		}

		out.append('\"');
		for(int i=0; i<text.length(); i++){
			char ch = text.charAt(i);
			switch(ch){
			case '\b':		out.append("\\b");		break;
			case '\f':		out.append("\\f");		break;
			case '\n':		out.append("\\n");		break;
			case '\r':		out.append("\\r");		break;
			case '\t':		out.append("\\t");		break;
			case '\u000B':	out.append("\\v");		break;
			case '\"':		out.append("\\\"");		break;
			case '\'':		out.append("\\\'");		break;
			case '\\':		out.append("\\\\");		break;
			default:
				if(Character.isDefined(ch) && !Character.isISOControl(ch)){
					out.append(ch);
				} else {
					out.append("\\u");
					out.append(String.format("%04X", (int)ch));
				}
				break;
			}
		}
		out.append('\"');
		return out;
	}

}
