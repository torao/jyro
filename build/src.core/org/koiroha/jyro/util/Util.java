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
import java.lang.management.*;
import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Util: ユーティリティクラス
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * パッケージ内のユーティリティクラスです。
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/28 Java SE 6
 */
public final class Util {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Util.class);

	// ======================================================================
	// Default Port
	// ======================================================================
	/**
	 * URL スキームに対するデフォルトポートです。
	 */
	private static final Map<String,Integer> DEFAULT_PORT = new HashMap<String,Integer>();

	// ======================================================================
	// Hyper-Text Reference XPath
	// ======================================================================
	/**
	 * HTML 上で別のコンテンツへのリンクを抽出するための XPath です。
	 */
	public static final Set<String> HREF_XPATH;

	// ======================================================================
	// Hyper-Text Reference XPath
	// ======================================================================
	/**
	 * HTML 上で別のコンテンツへのリンクを抽出するための XPath です。
	 */
	private static final RuntimeMXBean RUNTIME = ManagementFactory.getRuntimeMXBean();

	// ======================================================================
	// デフォルトポート
	// ======================================================================
	/**
	 * URL スキームに対するデフォルトポートです。
	 */
	static {
		ResourceBundle res = ResourceBundle.getBundle("org.koiroha.jyro.jyro");

		// デフォルトポートの参照
		final String defPortPrefix = "defaultPort.";
		Enumeration<String> en = res.getKeys();
		while(en.hasMoreElements()){
			String key = en.nextElement();
			if(key.startsWith(defPortPrefix)){
				String value = res.getString(key);
				DEFAULT_PORT.put(key.substring(defPortPrefix.length()), Integer.parseInt(value));
			}
		}

		// XPath リストを参照
		String[] xpath = res.getString("href.xpath").split("\\s*,\\s");
		HREF_XPATH = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(xpath)));
	}

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * コンストラクタはクラス内に隠蔽されています。
	 */
	private Util() {
		return;
	}

	// ======================================================================
	// Refer Default Port
	// ======================================================================
	/**
	 * URL スキームに対するデフォルトのポートを参照します。
	 *
	 * @param scheme URL scheme
	 * @return default port
	 */
	public static int getDefaultPort(String scheme){
		scheme = scheme.toLowerCase();
		Integer port = DEFAULT_PORT.get(scheme);
		if(port == null){
			return -1;
		}
		return port;
	}

	// ======================================================================
	// Evaluate Default Port
	// ======================================================================
	/**
	 * 指定されたポート番号が URL スキームに対するデフォルトのポートかを判定
	 * します。
	 *
	 * @param scheme URL scheme
	 * @param port port number
	 * @return true if specified port number is default port for scheme
	 */
	public static boolean isDefaultPort(String scheme, int port){
		scheme = scheme.toLowerCase();
		Integer p = DEFAULT_PORT.get(scheme);
		if(p == null){
			return false;
		}
		return p.intValue() == port;
	}

	// ======================================================================
	// Close Objects
	// ======================================================================
	/**
	 * 入出力ストリームやデータベースリソースを例外なしでクローズするための
	 * ユーティリティ機能です。
	 *
	 * @param objs the objects array to close
	 */
	public static void close(Object... objs){
		for(Object obj: objs){
			if(obj == null){
				continue;
			}

			// 入出力ストリームの場合
			if(obj instanceof Closeable){
				try {
					((Closeable)obj).close();
				} catch(IOException ex){
					logger.warn("fail to close object: " + obj, ex);
				}
				continue;
			}

			if(obj instanceof Connection){
				try {
					((Connection)obj).close();
				} catch(SQLException ex){
					logger.warn("fail to close connection: " + obj, ex);
				}
				continue;
			}

			if(obj instanceof Statement){
				try {
					((Statement)obj).close();
				} catch(SQLException ex){
					logger.warn("fail to close statement: " + obj, ex);
				}
				continue;
			}

			if(obj instanceof ResultSet){
				try {
					((ResultSet)obj).close();
				} catch(SQLException ex){
					logger.warn("fail to close result set: " + obj, ex);
				}
				continue;
			}

			throw new IllegalStateException("unsupported closeable object: " + obj);
		}
		return;
	}

	// ======================================================================
	// 起動時間の参照
	// ======================================================================
	/**
	 * 起動時間をミリ秒単位で参照します。
	 *
	 * @return 起動時間
	 */
	public static long getUptime(){
		return RUNTIME.getUptime();
	}

	// ======================================================================
	// Wrap Logging Connection
	// ======================================================================
	/**
	 * 指定されたデータベース接続を SQL ログ出力でラップします。
	 *
	 * @param con ラップするデータベース接続
	 * @return SQL ログを出力するデータベース接続
	 */
	public static Connection wrap(Connection con){
		return (Connection)Proxy.newProxyInstance(
			Util.class.getClassLoader(),
			new Class<?>[]{ Connection.class },
			new IH(con));
	}

	public static InputStream wrap(final ByteBuffer buf){
		return new InputStream(){
			@Override
			public synchronized int read() throws IOException {
				if (! buf.hasRemaining()) {
						return -1;
				}
				return buf.get();
			}
			@Override
			public synchronized int read(byte[] bytes, int off, int len) throws IOException {
				len = Math.min(len, buf.remaining());
				buf.get(bytes, off, len);
				return len;
			}
		};
	}

	// ======================================================================
	// Name Conversion from YAML to Property Name
	// ======================================================================
	/**
	 * 指定された名前を Java Beans のプロパティ名形式に変換します。
	 * このメソッドは "foo_bar_xyz" に対して "fooBarXyz" を返します。
	 *
	 * @param name yaml name
	 * @return converted property name
	 */
	public static String yaml2propertyName(String name){
		StringBuilder buffer = new StringBuilder();
		String[] names = name.split("_+");
		buffer.append(names[0].toLowerCase());
		for(int i=1; i<names.length; i++){
			buffer.append(Character.toUpperCase(names[i].charAt(0)));
			buffer.append(names[i].substring(1).toLowerCase());
		}
		return buffer.toString();
	}

	private static class IH implements InvocationHandler {

		// ==================================================================
		//
		// ==================================================================
		/**
		 */
		private final Object value;

		// ==================================================================
		//
		// ==================================================================
		/**
		 */
		private final String sql;

		// ==================================================================
		//
		// ==================================================================
		/**
		 */
		private final Map<String,Object> params = new HashMap<String,Object>();

		// ==================================================================
		//
		// ==================================================================
		/**
		 */
		public IH(Connection con){
			this.value = con;
			this.sql = null;
			logger.debug("BEGIN");
			return;
		}

		// ==================================================================
		//
		// ==================================================================
		/**
		 */
		public IH(Statement stmt){
			this.value = stmt;
			this.sql = null;
			return;
		}

		// ==================================================================
		//
		// ==================================================================
		/**
		 */
		public IH(PreparedStatement stmt, String sql){
			this.value = stmt;
			this.sql = sql;
			return;
		}

		// ==================================================================
		//
		// ==================================================================
		/**
		 */
		public IH(ResultSet rs){
			this.value = rs;
			this.sql = null;
			return;
		}

		// ==================================================================
		//
		// ==================================================================
		/**
		 * @param proxy
		 * @param method
		 * @param args
		 * @return
		 * @throws Throwable
		 */
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if(method.getDeclaringClass().equals(Connection.class)){
				assert(value instanceof Connection);
				if(method.getName().equals("commit")){
					logger.debug("COMMIT");
				} else if(method.getName().equals("rollback")){
					logger.debug("ROLLBACK");
				}
				Object result = method.invoke(value, args);
				if(result instanceof CallableStatement){
					result = Proxy.newProxyInstance(Util.class.getClassLoader(),
						new Class<?>[]{ CallableStatement.class }, new IH((CallableStatement)result, (String)args[0]));
				} else if(result instanceof PreparedStatement){
					result = Proxy.newProxyInstance(Util.class.getClassLoader(),
						new Class<?>[]{ PreparedStatement.class }, new IH((PreparedStatement)result, (String)args[0]));
				} else if(result instanceof Statement){
					result = Proxy.newProxyInstance(Util.class.getClassLoader(),
						new Class<?>[]{ Statement.class }, new IH((Statement)result));
				}
				return result;
			}

			if(method.getDeclaringClass().equals(PreparedStatement.class)){
				assert(value instanceof PreparedStatement);
				if(method.getName().equals("execute") || method.getName().equals("executeQuery") || method.getName().equals("executeUpdate")){
					logger.debug(this.sql + "; " + params);
				} else if(method.getName().startsWith("set") && args.length > 0 && args[0] instanceof Integer){
					params.put(args[0].toString(), args[1]);
				}
				Object result = method.invoke(value, args);
				if(result instanceof ResultSet){
					result = Proxy.newProxyInstance(Util.class.getClassLoader(),
						new Class<?>[]{ ResultSet.class }, new IH((ResultSet)result));
				}
				return result;
			}

			if(method.getDeclaringClass().equals(Statement.class)){
				assert(value instanceof Statement);
				if(method.getName().equals("execute") || method.getName().equals("executeQuery") || method.getName().equals("executeUpdate")){
					logger.debug(args[0]);
				}
				Object result = method.invoke(value, args);
				if(result instanceof ResultSet){
					result = Proxy.newProxyInstance(Util.class.getClassLoader(),
						new Class<?>[]{ ResultSet.class }, new IH((ResultSet)result));
				}
				return result;
			}

			if(method.getDeclaringClass().equals(ResultSet.class)){
				assert(value instanceof ResultSet);
				if(method.getName().equals("updateRow")){
					ResultSet rs = (ResultSet)value;
					ResultSetMetaData meta = rs.getMetaData();
					logger.debug("UPDATE " + meta.getTableName(1) + " " + params + " WHERE id=" + rs.getLong("id"));
				} else if(method.getName().equals("insertRow")){
					ResultSet rs = (ResultSet)value;
					ResultSetMetaData meta = rs.getMetaData();
					logger.debug("INSERT " + meta.getTableName(1) + " " + params + " WHERE id=" + rs.getLong("id"));
				} else if(method.getName().startsWith("update")){
					params.put(args[0].toString(), args[1]);
				}
				Object result = method.invoke(value, args);
				return result;
			}
			return method.invoke(value, args);
		}

	}

}
