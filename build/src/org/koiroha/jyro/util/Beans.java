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

import java.beans.*;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.koiroha.jyro.JyroException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// :
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public final class Beans {
	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(Beans.class);

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 */
	private Beans() {
		return;
	}

	// ======================================================================
	// Create New Instance
	// ======================================================================
	/**
	 * Create new instance.
	 *
	 * @param loader class loader
	 * @param className class name
	 * @throws JyroException fail to create instance
	 */
	public static Object newInstance(ClassLoader loader, String className) throws JyroException{
		try {
			Class<?> clazz = Class.forName(className, true, loader);
			return clazz.newInstance();
		} catch(Exception ex){
			throw new JyroException("", ex);
		}
	}

	// ======================================================================
	// Set Bean Property
	// ======================================================================
	/**
	 * Set bean property.
	 *
	 * @param bean object to set property
	 * @param name property name
	 * @param value property value
	 * @throws JyroException if attribute is not a valid number
	 */
	public static void setProperty(Object bean, String name, String value) throws JyroException{
		Class<?> clazz = bean.getClass();
		try {

			// lookup setter method for property
			BeanInfo info = Introspector.getBeanInfo(clazz, Object.class);
			PropertyDescriptor[] desc = info.getPropertyDescriptors();
			Method method = null;
			for(int i=0; desc!=null && i<desc.length; i++){
				if(desc[i].getName().equals(name)){
					method = desc[i].getWriteMethod();
					break;
				}
			}

			// if setter is not defined
			if(method == null){
				throw new NoSuchMethodException(getSetterName(name) + "() is not defined: " + clazz.getName());
			}

			Object v = translate(value, method.getParameterTypes()[0]);
			method.invoke(bean, v);
			logger.debug(clazz.getName() + "." + getSetterName(name) + "(" + v + ")");
		} catch(Exception ex){
			throw new JyroException(clazz.getName() + "." + getSetterName(name) + "(" + value + ")", ex);
		}
		return;
	}

	// ======================================================================
	// Set Bean Property
	// ======================================================================
	/**
	 * Set bean property.
	 *
	 * @param bean object to set property
	 * @param name property name
	 * @param value property value
	 * @throws JyroException if attribute is not a valid number
	 */
	public static Object translate(String value, Class<?> type) throws JyroException{

		// primitive type conversion
		if(type.isPrimitive()){
			if(boolean.class == type){
				return Boolean.valueOf(value);
			}
			if(short.class == type){
				return Short.valueOf(value);
			}
			if(int.class == type){
				return Integer.valueOf(value);
			}
			if(long.class == type){
				return Long.valueOf(value);
			}
			if(float.class == type){
				return Float.valueOf(value);
			}
			if(double.class == type){
				return Double.valueOf(value);
			}
			throw new IllegalArgumentException("unexpected primitive type: " + type.getCanonicalName());
		}

		// standard object types (final needed)
		if(String.class == type){
			return value;
		}
		throw new JyroException("unsupported conversion type: " + type.getCanonicalName() + ": " + value);
	}

	// ======================================================================
	// Refer Setter Name
	// ======================================================================
	/**
	 * Get setter method name for specified property name.
	 *
	 * @param name property name
	 * @return setter method name
	 */
	private static String getSetterName(String name){
		if(name.length() == 0){
			return "set";
		}
		return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

}
