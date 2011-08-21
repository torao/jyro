/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.workers.crawler;

import java.io.Serializable;
import java.net.URI;
import java.util.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Content:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/21 Java SE 6
 */
public class Content implements Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// URI
	// ======================================================================
	/**
	 * URI of this content.
	 */
	public final URI uri;

	// ======================================================================
	// Resuest
	// ======================================================================
	/**
	 * Request of this content.
	 */
	public final Request request;

	// ======================================================================
	// Response
	// ======================================================================
	/**
	 * Response of this content.
	 */
	public final Response response;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 */
	public Content(URI uri, Request request, Response response){
		this.uri = uri;
		this.request = request;
		this.response = response;
		return;
	}

	// ======================================================================
	// Refer URI
	// ======================================================================
	/**
	 * Refer URI of this content.
	 *
	 * @return URI
	 */
	public URI getUri(){
		return uri;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Message:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 */
	public static abstract class Message implements Serializable {

		// ==================================================================
		// Serial Version
		// ==================================================================
		/**
		 * Serial version of this class.
		 */
		private static final long serialVersionUID = 1L;

		// ==================================================================
		// Header Field
		// ==================================================================
		/**
		 * Name: Value style header field.
		 */
		private final List<String[]> headers = new ArrayList<String[]>();

		// ==================================================================
		// Header Field
		// ==================================================================
		/**
		 * Name: Value style header field.
		 */
		private byte[] content;

		// ==================================================================
		// Header Field
		// ==================================================================
		/**
		 * Name: Value style header field.
		 */
		public void addHeader(String name, String value){
			headers.add(new String[]{ name, value });
			return;
		}

		// ==================================================================
		// Refer Header
		// ==================================================================
		/**
		 * Retrieve header value associate with specified name. If multiple
		 * field defined, the first one returns. And if no header defined,
		 * null will return.
		 *
		 * @param name header name
		 * @return header value
		 */
		public String getHeader(String name){
			for(String[] field: headers){
				if(field[0].equalsIgnoreCase(name)){
					return field[1];
				}
			}
			return null;
		}

		// ==================================================================
		// Refer Header
		// ==================================================================
		/**
		 * Refer header value. If no header defined, empty iterable returns.
		 *
		 * @param name header name
		 * @return header values
		 */
		public Iterable<String> getHeaders(String name){
			List<String> values = new ArrayList<String>();
			for(String[] field: headers){
				if(field[0].equalsIgnoreCase(name)){
					values.add(field[1]);
				}
			}
			return values;
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Message:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 */
	public static class Request extends Message {

		// ==================================================================
		// Serial Version
		// ==================================================================
		/**
		 * Serial version of this class.
		 */
		private static final long serialVersionUID = 1L;

		// ==================================================================
		// Request Method
		// ==================================================================
		/**
		 * Name: Value style header field.
		 */
		private final String method;

		// ==================================================================
		// Request URI
		// ==================================================================
		/**
		 * Name: Value style header field.
		 */
		private final URI uri;

		// ==================================================================
		// Request URI
		// ==================================================================
		/**
		 * Name: Value style header field.
		 */
		private final String version;

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 */
		public Request(String method, URI uri, String version){
			this.method = method;
			this.uri = uri;
			this.version = version;
			return;
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Message:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 */
	public static class Response extends Message {

		// ==================================================================
		// Serial Version
		// ==================================================================
		/**
		 * Serial version of this class.
		 */
		private static final long serialVersionUID = 1L;

		// ==================================================================
		// Response URI
		// ==================================================================
		/**
		 * Name: Value style header field.
		 */
		private final String version;

		// ==================================================================
		// Response Code
		// ==================================================================
		/**
		 * Name: Value style header field.
		 */
		private final int code;

		// ==================================================================
		// Response Phrase
		// ==================================================================
		/**
		 * Name: Value style header field.
		 */
		private final String phrase;

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 */
		public Response(String version, int code, String phrase){
			this.version = version;
			this.code = code;
			this.phrase = phrase;
			return;
		}

	}

}
