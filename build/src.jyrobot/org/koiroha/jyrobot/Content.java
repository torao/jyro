/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyrobot;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;

import org.koiroha.xml.Xml;
import org.koiroha.xml.parser.HTMLDocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

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
	// HTML/XML Document
	// ======================================================================
	/**
	 * 内容を XML または HTML として解析した結果です。
	 */
	private Document doc = null;

	// ======================================================================
	// HTML/XML Document Charset
	// ======================================================================
	/**
	 * この内容を HTML/XML ドキュメントとして解析した時の文字セットです。
	 */
	private Charset docCharset = null;

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
	public URI getURI(){
		return uri;
	}

	// ======================================================================
	// Refer XML Document
	// ======================================================================
	/**
	 * この内容を XML ドキュメントとして参照します。
	 *
	 * @return URI
	 */
	public Document getDocument(){
		if(doc == null){

			// デフォルト文字セットの参照
			String type = response.getHeader("Content-Type");
			Charset def = Xml.getCharset(type);
			if(def == null){
				def = Charset.forName("UTF-8");
			}

			byte[] content = response.getContent();
			HTMLDocumentBuilderFactory factory = new HTMLDocumentBuilderFactory();
			factory.setNamespaceAware(false);
			factory.setXIncludeAware(false);
			try {
				InputSource is = factory.guessInputSource(new ByteArrayInputStream(content), def.name(), content.length);
				docCharset = Charset.forName(is.getEncoding());
				DocumentBuilder builder = factory.newDocumentBuilder();
				doc = builder.parse(is);
			} catch(Exception ex){
				throw new IllegalStateException(ex);
			}
		}
		return doc;
	}

	// ======================================================================
	// Refer XML Document Charset
	// ======================================================================
	/**
	 * この内容を XML/HTML として解析した時に適用した文字セットを参照します。
	 *
	 * @return document character set
	 */
	public Charset getDocumentCharset() {
		if(docCharset == null){
			getDocument();
		}
		return docCharset;
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

		// ==================================================================
		// Refer Content-Type
		// ==================================================================
		/**
		 * このメッセージの Content-Type を参照します。Content-Type が設定さ
		 * れていない場合は null を返します。返値はすべて小文字となり
		 * サブタイプは含まれません。
		 *
		 * @return Content-Type value
		 */
		public String getContentType(){
			String contentType = getHeader("Content-Type");
			if(contentType == null){
				return null;
			}
			int sep = contentType.indexOf(';');
			if(sep >= 0){
				contentType = contentType.substring(0, sep);
			}
			return contentType.trim().toLowerCase();
		}

		// ==================================================================
		// Set Content
		// ==================================================================
		/**
		 * Set content of this message.
		 * @param content binary content
		 */
		public void setContent(byte[] content){
			this.content = content;
			return;
		}

		// ==================================================================
		// Retrieve Content
		// ==================================================================
		/**
		 * Retrieve content of this message.
		 * @return binary content
		 */
		public byte[] getContent(){
			return content;
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

		// ==================================================================
		// Method
		// ==================================================================
		/**
		 * Retrieve request method.
		 */
		public String getMethod() {
			return method;
		}

		// ==================================================================
		//
		// ==================================================================
		/**
		 * Retrieve URI to request.
		 */
		public URI getUri() {
			return uri;
		}

		// ==================================================================
		//
		// ==================================================================
		/**
		 * Retrieve HTTP Version.
		 */
		public String getVersion() {
			return version;
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

		// ==================================================================
		// Retrieve HTTP Version
		// ==================================================================
		/**
		 */
		public String getVersion() {
			return version;
		}

		// ==================================================================
		// Retrieve Response Code
		// ==================================================================
		/**
		 */
		public int getCode() {
			return code;
		}

		// ==================================================================
		// Retrieve Response Phrase
		// ==================================================================
		/**
		 */
		public String getPhrase() {
			return phrase;
		}

	}

}
