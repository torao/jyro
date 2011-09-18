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
			for(Header h: getAll()){
				if(h.nameMatches(name)){
					return h.getValue();
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
			for(Header h: getAll()){
				if(h.nameMatches(name)){
					values.add(h.getValue());
				}
			}
			return Collections.unmodifiableCollection(values);
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
