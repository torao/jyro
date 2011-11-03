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

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Response: レスポンス
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
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
	 * レスポンス内容です。
	 */
	private ByteBuffer content = null;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
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
			int maxLength = request.getSession().getJyrobot().getUserAgent().getMaxContentLength();
			InputStream in = con.getInputStream();
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int length = 0;
			while(length < maxLength){
				int len = in.read(buffer, 0, Math.min(buffer.length, maxLength - length));
				if(len < 0){
					break;
				}
				baos.write(buffer, 0, len);
				request.getSession().increaseTotalRetrieval(len);
			}
			content = ByteBuffer.wrap(baos.toByteArray()).asReadOnlyBuffer();
		}
		return content;
	}

}