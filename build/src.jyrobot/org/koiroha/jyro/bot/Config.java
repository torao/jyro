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
import java.util.*;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Yaml: Yaml アクセス用ユーティリティ
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Yaml で定義された設定にアクセスするためのユーティリティクラスです。
 *
 * @version
 * @author torao
 * @since 2011/10/20 jyro 1.0
 */
public class Config {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(Config.class);

	// ======================================================================
	// Prefix
	// ======================================================================
	/**
	 * Prefix of this configuration.
	 */
	private final String prefix;

	// ======================================================================
	// Yaml Structured Definition
	// ======================================================================
	/**
	 * この設定の定義内容です。
	 */
	private final Map<String,Object> config;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * 指定された定義を使用します。
	 *
	 * @param prefix prefix of this configuration
	 * @param yaml YAML definition
	 */
	private Config(String prefix, Map<String,Object> yaml) {
		this.prefix = prefix;
		this.config = yaml;
		return;
	}

	// ======================================================================
	// Refer Pathname
	// ======================================================================
	/**
	 * 指定された項目名に対する人が読むためのパス名を参照します。
	 *
	 * @param names configuration item name
	 * @return human-readable path name for specified configuration name
	 */
	public String getPathname(Object... names) {
		StringBuilder buffer = new StringBuilder(prefix);
		for(Object name: names){
			buffer.append(name).append('/');
		}
		buffer.deleteCharAt(buffer.length()-1);
		return buffer.toString();
	}

	// ======================================================================
	// Retrieve String
	// ======================================================================
	/**
	 * 指定されたパスの文字列値を参照します。
	 *
	 * @param names name for Map, or index for List
	 * @return specified string, or null if not found
	 */
	public String getString(Object... names){
		Object value = getObject(names);
		if(value == null){
			return null;
		}
		return value.toString();
	}

	// ======================================================================
	// Retrieve Long
	// ======================================================================
	/**
	 * 指定されたパスの long 値を参照します。
	 *
	 * @param names name for Map, or index for List
	 * @return specified long, or -1 if not found
	 */
	public long getLong(Object... names){
		Object value = getObject(names);
		if(value == null || !(value instanceof Number)){
			return -1;
		}
		return ((Number)value).longValue();
	}

	// ======================================================================
	// Retrieve Int
	// ======================================================================
	/**
	 * 指定されたパスの long 値を参照します。
	 *
	 * @param names name for Map, or index for List
	 * @return specified int value, or -1 if not found
	 */
	public int getInt(Object... names){
		return (int)getLong(names);
	}

	// ======================================================================
	// Retrieve Boolean
	// ======================================================================
	/**
	 * 指定されたパスの boolean 値を参照します。
	 *
	 * @param names name for Map, or index for List
	 * @return specified int value, or false if not found
	 */
	public boolean getBoolean(Object... names){
		String value = getString(names);
		if(value == null){
			return false;
		}
		if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("on")){
			return true;
		}
		return false;
	}

	// ======================================================================
	// Retrieve Map
	// ======================================================================
	/**
	 * 指定されたパスの Map 値を参照します。
	 *
	 * @param names name for Map, or index for List
	 * @return specified map, or null if not found
	 */
	public Map<String,Object> getMap(Object... names){
		Object value = getObject(names);
		if(value == null || !(value instanceof Map<?,?>)){
			return null;
		}
		@SuppressWarnings("unchecked")
		Map<String,Object> map = (Map<String,Object>)value;
		return map;
	}

	// ======================================================================
	// Retrieve Map
	// ======================================================================
	/**
	 * 指定されたパスの Map 値を参照します。
	 *
	 * @param names name for Map, or index for List
	 * @return specified map, or null if not found
	 */
	public Iterable<Map<String,Object>> getMaps(Object... names){
		List<Object> list = getList(names);
		List<Map<String,Object>> maps = new ArrayList<Map<String,Object>>();
		for(Object value: list){
			if(value == null || !(value instanceof Map<?,?>)){
				return null;
			}
			@SuppressWarnings("unchecked")
			Map<String,Object> map = (Map<String,Object>)value;
			maps.add(map);
		}
		return maps;
	}

	// ======================================================================
	// Retrieve List
	// ======================================================================
	/**
	 * 指定されたパスの List 値を参照します。
	 *
	 * @param names name for Map, or index for List
	 * @return specified list, or null if not found
	 */
	public List<Object> getList(Object... names){
		Object value = getObject(names);
		if(value == null || !(value instanceof List<?>)){
			return null;
		}
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>)value;
		return list;
	}

	// ======================================================================
	// Retrieve Sub-Configuration
	// ======================================================================
	/**
	 * 指定さたパスの Map を新しいコンフィギュレーションとして参照します。
	 *
	 * @param names name for Map, or index for List
	 * @return subconfiguration, or empty configuration if specified name not defined
	 */
	public Config getSubconfig(Object... names){
		Map<String,Object> map = getMap(names);
		if(map == null){
			map = new HashMap<String,Object>();
		}
		return new Config(getPathname(names) + "/", map);
	}

	// ======================================================================
	// Retrieve Sub-Configurations
	// ======================================================================
	/**
	 * 指定されたパス直下の Map 配列を新しいコンフィギュレーションとして参照します。
	 *
	 * @param names name for List of configuration Map
	 * @return subconfigurations, or empty configuration if specified name not defined
	 */
	public Iterable<Config> getSubconfigs(Object... names){
		List<Config> list = new ArrayList<Config>();
		for(Map<String,Object> map: getMaps(names)){
			list.add(new Config(getPathname(names) + "/", map));
		}
		return list;
	}

	// ======================================================================
	// Literalize Instance
	// ======================================================================
	/**
	 * このインスタンスを文字列化します。
	 *
	 * @return string of this instance
	 */
	@Override
	public String toString() {
		return getPathname();
	}

	// ======================================================================
	// Create Instance
	// ======================================================================
	/**
	 * 指定された YAML 形式の入力ストリームからインスタンスを生成します。
	 *
	 * @param in input stream that has yaml representation
	 * @return configuration object
	 * @throws IOException
	 */
	public static Config newInstance(InputStream in) throws IOException {
		logger.debug("newInstance(in)");
		Yaml yaml = new Yaml();
		@SuppressWarnings("unchecked")
		Map<String,Object> config = (Map<String,Object>)yaml.load(in);
		return new Config("/", config);
	}

	// ======================================================================
	// Retrieve Object
	// ======================================================================
	/**
	 * 指定された名前またはインデックスのオブジェクトを参照します。
	 *
	 * @param names name for Map, or index for List
	 * @return specified object
	 */
	public Object getObject(Object... names){
		Object value = config;
		for(int i=0; i<names.length; i++){
			if(value instanceof Map<?,?> && names[i] instanceof String){
				value = ((Map<?,?>)value).get(names[i]);
			} else if(value instanceof List<?> && names[i] instanceof Number){
				value = ((List<?>)value).get(((Number)names[i]).intValue());
			} else {
				logger.warn(getPathname(names) + " not found in configuration");
				return null;
			}
		}
		logger.debug(getPathname(names) + "=" + value);
		return value;
	}

}
