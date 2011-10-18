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

import java.io.Serializable;
import java.nio.CharBuffer;
import java.text.*;
import java.util.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Message: メッセージクラス
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * HTTP メッセージ (リクエスト/レスポンス) を表すクラスです。
 *
 * @version
 * @author torao
 * @since 2011/09/17 jyro 1.0
 */
public class Message implements Serializable {

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
	private static final Logger logger = Logger.getLogger(Message.class);

	// ======================================================================
	// HTTP Date Format
	// ======================================================================
	/**
	 * RFC 2616 3.3.1 Full Date で定義されている日付型を解析するためのフォーマットです。
	 */
	private static final String[] DATE_FORMAT = {
		"EEE, dd MMM yyyy HH:mm:ss Z",	// RFC 822, updated by RFC 1123
		"EEEE, dd-MMM-yy HH:mm:ss Z",	// RFC 850, obsoleted by RFC 1036
		"EEE MMM dd hh:MM:ss yyyy",		// ANSI C's asctime() format
	};

	// ======================================================================
	// GMT
	// ======================================================================
	/**
	 * GMT を表すタイムゾーンです。
	 */
	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	// ======================================================================
	// ヘッダ
	// ======================================================================
	/**
	 * このメッセージのヘッダです。
	 */
	public final HeaderContainer header = new HeaderContainer();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 */
	public Message() {
		return;
	}

	// ======================================================================
	// Refer Content-Type
	// ======================================================================
	/**
	 * このメッセージの Content-Type を参照します。
	 * 返値は "maintype/subtype" 形式で属性は付きません。
	 * このメッセージの Content-Type が不明な場合は null を返します。
	 *
	 * @return Content-Type of this message
	 */
	public String getContentType(){
		Header h = header.getHeader("Content-Type");
		if(h != null){
			return h.getMainValue();
		}
		return null;
	}

	// ======================================================================
	// Read Next Token
	// ======================================================================
	/**
	 * 指定された文字列バッファから次の ';' までの文字列を取得します。
	 * このメソッドは引用記号で囲まれた文字列をそのまま残します。
	 *
	 * @param in character buffer
	 * @return Content-Type of this message
	 */
	private static String next(CharBuffer in){
		StringBuilder buffer = new StringBuilder();
		while(in.hasRemaining()){
			char ch = in.get();
			if(ch == ';'){
				break;
			}
			buffer.append(ch);

			if(isQuote(ch)){
				char quote = ch;
				ch = '\0';
				while(in.hasRemaining()){
					ch = in.get();
					buffer.append(ch);
					if(ch == quote){
						break;
					}
				}
			}
		}
		String value = buffer.toString().trim();
		return (! in.hasRemaining() && value.length() == 0)? null: value;
	}

	// ======================================================================
	// Strip Quote
	// ======================================================================
	/**
	 * 指定された文字列の前後の引用記号を削除します。
	 *
	 * @param value string to strip heading and trailing quote
	 * @return Content-Type of this message
	 */
	private static String stripQuote(String value){
		if(value.length() > 1){
			char head = value.charAt(0);
			char tail = value.charAt(value.length()-1);
			if(isQuote(head) && head == tail){
				return value.substring(1, value.length()-1);
			}
		}
		return value;
	}

	// ======================================================================
	// Evaluate Quote Character
	// ======================================================================
	/**
	 * 指定された文字が引用記号かどうかを判断します。
	 *
	 * @param ch quote character
	 * @return true if specified character is quote symbol
	 */
	private static boolean isQuote(char ch){
		return (ch == '\"' || ch == '\'');
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// HeaderContainer: ヘッダコンテナ
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * メッセージヘッダを保持するためのコンテナクラスです。
	 */
	public static class HeaderContainer implements Serializable {

		// ==================================================================
		// Serial Version
		// ==================================================================
		/**
		 * Serial version of this class.
		 */
		private static final long serialVersionUID = 1L;


		// ======================================================================
		// Header
		// ======================================================================
		/**
		 * このメッセージのヘッダです。
		 */
		private final List<Header> header = new ArrayList<Header>();

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * コンストラクタはクラス内に隠蔽されています。
		 */
		private HeaderContainer(){
			return;
		}


		// ======================================================================
		// Refer All Message Header
		// ======================================================================
		/**
		 * このメッセージに設定されているすべてのヘッダを参照します。
		 *
		 * @return all headers of this message
		 */
		public Iterable<Header> getAll(){
			List<Header> header = new ArrayList<Header>();
			synchronized(header){
				header.addAll(this.header);
			}
			return Collections.unmodifiableCollection(header);
		}

		// ======================================================================
		// Refer Message Header Value
		// ======================================================================
		/**
		 * 指定された名前のメッセージヘッダを参照します。
		 * 名前に該当するヘッダが複数定義されている場合はどの値が返されるかは不定です。
		 * 名前に対する値が定義されていない場合は null を返します。
		 *
		 * @param name header name
		 * @return header value, or null if the header is not presence
		 */
		public String get(String name){
			Header h = getHeader(name);
			if(h != null){
				return h.getValue();
			}
			return null;
		}

		// ======================================================================
		// Refer Message Header
		// ======================================================================
		/**
		 * 指定された名前のメッセージヘッダを参照します。
		 * 名前に該当するヘッダが複数定義されている場合はどの値が返されるかは不定です。
		 * 名前に対する値が定義されていない場合は null を返します。
		 *
		 * @param name header name
		 * @return header value, or null if the header is not presence
		 */
		public Header getHeader(String name){
			for(Header h: getAll()){
				if(h.nameMatches(name)){
					return h;
				}
			}
			return null;
		}

		// ======================================================================
		// Refer Message Header Values
		// ======================================================================
		/**
		 * 指定された名前のメッセージヘッダをすべて参照します。
		 * 名前に該当するヘッダが定義されていない場合は空の列挙を返します。
		 *
		 * @param name header name
		 * @return iteration of all values for specified header name
		 */
		public Iterable<String> getAll(String name){
			List<String> values = new ArrayList<String>();
			for(Header h: getAllHeader(name)){
				values.add(h.getValue());
			}
			return Collections.unmodifiableCollection(values);
		}

		// ======================================================================
		// Refer Message Header
		// ======================================================================
		/**
		 * 指定された名前のメッセージヘッダをすべて参照します。
		 * 名前に該当するヘッダが定義されていない場合は空の列挙を返します。
		 *
		 * @param name header name
		 * @return iteration of all fields for specified header name
		 */
		public Iterable<Header> getAllHeader(String name){
			List<Header> fields = new ArrayList<Header>();
			for(Header h: getAll()){
				if(h.nameMatches(name)){
					fields.add(h);
				}
			}
			return Collections.unmodifiableCollection(fields);
		}

		// ======================================================================
		// Set Message Header
		// ======================================================================
		/**
		 * 指定されたメッセージヘッダを設定します。
		 * 既に同じ名前のヘッダが存在する場合は上書きされます。
		 * 複数存在する場合は新しく設定されたもの以外は削除されます。
		 *
		 * @param name header name
		 * @param value header value
		 */
		public void set(String name, String value){
			set(new Header(name, value));
			return;
		}

		// ======================================================================
		// Set Message Header
		// ======================================================================
		/**
		 * 指定されたメッセージヘッダを設定します。
		 * 既に同じ名前のヘッダが存在する場合は上書きされます。
		 * 複数存在する場合は新しく設定されたもの以外は削除されます。
		 *
		 * @param h header to add
		 */
		public void set(Header h){

			// 最初に出現するヘッダを置き換え、それ以降は削除
			boolean exists = false;
			synchronized(header){
				for(int i=0; i<header.size(); i++){
					if(header.get(i).nameMatches(h.getName())){
						if(! exists){
							header.set(i, h);
							exists = true;
						} else {
							header.remove(i);
							i --;
						}
					}
				}

				// 存在しなかった場合は追加
				if(! exists){
					header.add(h);
				}
			}
			return;
		}

		// ======================================================================
		// Add Message Header
		// ======================================================================
		/**
		 * 指定されたメッセージトヘッダを追加ます。
		 * 既に設定されている同じ名前のヘッダは置き換えられません。
		 *
		 * @param name header name
		 * @param value header value
		 */
		public void add(String name, String value){
			add(new Header(name, value));
			return;
		}

		// ======================================================================
		// Add Message Header
		// ======================================================================
		/**
		 * 指定されたメッセージトヘッダを追加ます。
		 * 既に設定されている同じ名前のヘッダは置き換えられません。
		 *
		 * @param h header to add
		 */
		public void add(Header h){
			synchronized(header){
				header.add(h);
			}
			return;
		}

		// ======================================================================
		// Remove Message Header
		// ======================================================================
		/**
		 * 指定されたメッセージヘッダを削除します。
		 * {@code name} と同じ名前のヘッダが全て削除されます。
		 *
		 * @param name header name
		 * @return header values that removed
		 */
		public Iterable<String> remove(String name){
			List<String> values = new ArrayList<String>();
			synchronized(header){
				for(int i=0; i<header.size(); i++){
					if(header.get(i).nameMatches(name)){
						values.add(header.get(i).getValue());
						header.remove(i);
						i --;
					}
				}
			}
			return Collections.unmodifiableCollection(values);
		}

		// ======================================================================
		// Remove All Header
		// ======================================================================
		/**
		 * このメッセージのすべてのヘッダを削除します。
		 */
		public void clear(){
			synchronized(header){
				header.clear();
			}
			return;
		}

		// ======================================================================
		// Refer Integer Header Value
		// ======================================================================
		/**
		 * 指定された名前のヘッダ値を int 型で参照します。
		 * 名前に該当する値が設定されていない場合は負の値を返します。
		 *
		 * @param name header name that has integer value
		 * @return header value as integer
		 */
		protected int getInt(String name){
			String value = get(name);
			if(value != null){
				try {
					return Integer.parseInt(value);
				} catch(NumberFormatException ex){
					logger.debug("unexpected int format: " + name + "=" + value);
				}
			}
			return -1;
		}

		// ======================================================================
		// Refer Long Header Value
		// ======================================================================
		/**
		 * 指定された名前のヘッダ値を long 型で参照します。
		 * 名前に該当する値が設定されていない場合は負の値を返します。
		 *
		 * @param name header name that has long value
		 * @return header value as long
		 */
		protected long getLong(String name){
			String value = get(name);
			if(value != null){
				try {
					return Long.parseLong(value);
				} catch(NumberFormatException ex){
					logger.debug("unexpected long format: " + name + "=" + value);
				}
			}
			return -1;
		}

		// ======================================================================
		// Refer Date Header Value
		// ======================================================================
		/**
		 * 指定された名前のヘッダ値を Date 型で参照します。
		 * 名前に該当する値が設定されていない場合は null を返します。
		 *
		 * @param name header name that has long value
		 * @return header value as long
		 */
		protected Date getDate(String name){
			String value = get(name);
			if(value != null){
				for(String fmt: DATE_FORMAT){
					try {
						DateFormat df = new SimpleDateFormat(fmt, Locale.US);
						df.setTimeZone(GMT);
						return df.parse(value);
					} catch(ParseException ex){/* */}
				}
				logger.debug("unexpected date format: " + name + "=" + value);
			}
			return null;
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Header: メッセージヘッダ
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * メッセージヘッダを保持するためのクラスです。
	 */
	public static class Header implements Serializable {

		// ==================================================================
		// Serial Version
		// ==================================================================
		/**
		 * Serial version of this class.
		 */
		private static final long serialVersionUID = 1L;

		// ==================================================================
		// Header Name
		// ==================================================================
		/**
		 * このヘッダの名前です。
		 */
		private final String name;

		// ==================================================================
		// Header Value
		// ==================================================================
		/**
		 * このヘッダの値です。
		 */
		private final String value;

		// ==================================================================
		// Parsed Value
		// ==================================================================
		/**
		 * main value and attribute values
		 */
		private transient Map<String,String> parsedValue = null;

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * ヘッダの名前と値を指定して構築を行います。
		 *
		 * @param name header name
		 * @param value header value
		 */
		public Header(String name, String value){
			this.name = name;
			this.value = value;
			return;
		}

		// ==================================================================
		// Refer Name
		// ==================================================================
		/**
		 * このヘッダの名前を参照します。
		 *
		 * @return header name
		 */
		public String getName() {
			return name;
		}

		// ==================================================================
		// Refer value
		// ==================================================================
		/**
		 * このヘッダの値を参照します。
		 *
		 * @return header value
		 */
		public String getValue() {
			return value;
		}

		// ==================================================================
		// Refer Main Value
		// ==================================================================
		/**
		 * このヘッダフィールドのメイン値を参照します。
		 *
		 * @return メイン値
		 */
		public String getMainValue(){
			Map<String,String> map = getParsedValue();
			return map.get(null);
		}

		// ==================================================================
		// Refer Attribute Value
		// ==================================================================
		/**
		 * このヘッダ値の属性値を参照します。
		 * 属性名に該当する値が設定されていない場合は null を返します。
		 *
		 * @param name attribute name
		 * @return attribute value of this field
		 */
		public String getAttribute(String name){
			Map<String,String> map = getParsedValue();
			return map.get(name.toLowerCase());
		}

		// ==================================================================
		// Parse Value
		// ==================================================================
		/**
		 * このヘッダの値をメイン値、属性値に解析して返します。
		 *
		 * @return parsed value of this header field
		 */
		private Map<String,String> getParsedValue(){
			if(parsedValue == null){
				Map<String,String> map = new HashMap<String, String>();
				CharBuffer buffer = CharBuffer.wrap(getValue());

				// メイン値の参照
				String mainValue = next(buffer);
				map.put(null, mainValue);

				// 属性値の参照
				while(true){
					String attr = next(buffer);
					if(attr == null){
						break;
					}
					int sep = attr.indexOf('=');
					if(sep >= 0){
						String name = attr.substring(0, sep).trim();
						String value = stripQuote(attr.substring(sep+1).trim());
						map.put(name.toLowerCase(), value);
					} else {
						map.put(attr.toLowerCase(), "");
					}
				}

				parsedValue = map;
			}
			return parsedValue;
		}

		// ==================================================================
		// Evaluate Name Matches
		// ==================================================================
		/**
		 * 指定されたヘッダ名とこのヘッダの名前が等しいかを判定します。
		 * 判定は大文字と小文字を区別しません。
		 *
		 * @param name header name to evaluate equality
		 * @return true if name matches
		 */
		private boolean nameMatches(String name) {
			return name.equalsIgnoreCase(this.name);
		}

	}

}
