/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.bot;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.NumberFormat;

import javax.xml.parsers.DocumentBuilder;

import org.apache.log4j.Logger;
import org.koiroha.jyro.util.Util;
import org.koiroha.xml.Xml;
import org.koiroha.xml.parser.HTMLDocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Response: レスポンス
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * レスポンスを表すクラスです。
 *
 * @version
 * @author torao
 * @since 2011/09/15 jyro 1.0
 */
public class Response extends Message{

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Response.class);

	// ======================================================================
	// Request
	// ======================================================================
	/**
	 * このレスポンスを生成したリクエストです。
	 */
	private final Request request;

	// ======================================================================
	// URL Connection
	// ======================================================================
	/**
	 * このレスポンスの URL 接続です。
	 */
	private final URLConnection con;

	// ======================================================================
	// Response Code
	// ======================================================================
	/**
	 * レスポンスコードです。
	 */
	private final int code;

	// ======================================================================
	// Response Message
	// ======================================================================
	/**
	 * レスポンスメッセージです。
	 */
	private final String message;

	// ======================================================================
	// Response Message
	// ======================================================================
	/**
	 * レスポンス内容です。遅延構築が行われます。
	 */
	private ByteBuffer content = null;

	// ======================================================================
	// Document
	// ======================================================================
	/**
	 * HTML/XML形式のドキュメントです。遅延構築が行われます。
	 */
	private Document document = null;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * 指定された URL 接続から構築を行います。
	 *
	 * @param request request object that create this instance
	 * @param con URL connection
	 */
	Response(Request request, URLConnection con) {
		this.request = request;
		this.con = con;

		// レスポンスコードとメッセージを参照
		int code = -1;
		String message = null;
		if(con instanceof HttpURLConnection){
			HttpURLConnection hcon = (HttpURLConnection)con;
			try {
				code = hcon.getResponseCode();
				message = hcon.getResponseMessage();
			} catch(IOException ex){/* */}
		}
		this.code = code;
		this.message = message;

		// レスポンスヘッダの取り込み
		for(int i=0; true; i++){
			String key = con.getHeaderFieldKey(i);
			String value = con.getHeaderField(i);
			if(key == null || value == null){
				break;
			}
			this.header.add(key, value);
		}

		return;
	}

	// ======================================================================
	// Refer Response Code
	// ======================================================================
	/**
	 * HTTP レスポンスコードを参照します。
	 * このレスポンスが未接続または HTTP 以外のプロトコルの場合は負の値を返します。
	 *
	 * @return HTTP response code
	 */
	public int getCode(){
		return code;
	}

	// ======================================================================
	// Refer Response Message
	// ======================================================================
	/**
	 * HTTP レスポンスメッセージを参照します。
	 * このレスポンスが未接続または HTTP 以外のプロトコルの場合は null を返します。
	 *
	 * @return HTTP response message
	 */
	public String getMessage(){
		return message;
	}

	// ======================================================================
	// Refer Request
	// ======================================================================
	/**
	 * このレスポンスを生成したリクエストを参照します。
	 *
	 * @return request request
	 */
	public Request getRequest() {
		return request;
	}

	// ======================================================================
	// Refer Response Content
	// ======================================================================
	/**
	 * このレスポンスの内容を参照します。
	 *
	 * @return request request
	 * @throws IOException if fail to read content
	 */
	public ByteBuffer getContent() throws IOException{
		if(content == null){

			// コンテンツの取得制限サイズを参照
			int maxLength = request.getSession().getUserAgent().getMaxContentLength();

			// コンテンツの入力ストリームを参照
			InputStream in = null;
			try {
				in = con.getInputStream();
			} catch(IOException ex){
				if(con instanceof HttpURLConnection){
					HttpURLConnection hcon = (HttpURLConnection)con;
					in = hcon.getErrorStream();
				} else {
					throw ex;
				}
			}

			// コンテンツの読み込み
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int length = 0;
			while(length < maxLength){
				int len = in.read(buffer, 0, Math.min(buffer.length, maxLength - length));
				if(len < 0){
					break;
				}
				baos.write(buffer, 0, len);
				request.getSession().getStat().increaseInputBytes(len);
			}
			byte[] bin = baos.toByteArray();
			content = ByteBuffer.wrap(bin).asReadOnlyBuffer();

			// 読み込み結果をログ出力
			NumberFormat nf = NumberFormat.getNumberInstance();
			logger.debug("retrieve content: " + nf.format(bin.length) + " bytes; " + request);
		}
		return content;
	}

	// ======================================================================
	// Refer Response Content
	// ======================================================================
	/**
	 * このレスポンスの内容を参照します。
	 *
	 * @return request request
	 * @throws IOException if fail to read content
	 */
	public Document getDocument() throws IOException{
		if(document == null){

			// レスポンス内容を入力ストリームとして参照
			ByteBuffer buf = getContent();
			InputStream in = Util.wrap(buf);

			// ヘッダから文字セットを参照
			String charset = "UTF-8";
			Charset cs = Xml.getCharset(header.get("Content-Type"));
			if(cs != null){
				charset = cs.name();
			}

			// HTML/XML の解析
			try {
				HTMLDocumentBuilderFactory factory = new HTMLDocumentBuilderFactory();
				DocumentBuilder builder = factory.newDocumentBuilder();
				InputSource is = factory.guessInputSource(in, charset, buf.capacity());
				document = builder.parse(is);
			} catch(Exception ex){
				// NOTE: HTMLDocumentBuilder is not throw any exception
				throw new IllegalStateException(ex);
			}
		}
		return document;
	}

}
