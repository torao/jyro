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
import java.net.URL;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.log4j.Logger;
import org.koiroha.xml.DefaultNamespaceContext;
import org.w3c.dom.*;
import org.xml.sax.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// XmlBeanWrapper: XML-Bean Wrapper
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Treat XML document as bean
 * <p>
 * @author torao
 * @since 2011/07/09 Java SE 6
 */
public class XmlBeanAdapter {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(XmlBeanAdapter.class);

	// ======================================================================
	// Node
	// ======================================================================
	/**
	 * Node of this instance.
	 */
	private final Node node;

	// ======================================================================
	// XPath
	// ======================================================================
	/**
	 * XPath
	 */
	private final XPath xpath;

	// ======================================================================
	// Namespace Context
	// ======================================================================
	/**
	 * XML namespace context.
	 */
	private final DefaultNamespaceContext ns = new DefaultNamespaceContext();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param url xml document url
	 * @throws IOException if fail to read stream
	 * @throws SAXException if not well-formed xml
	 */
	public XmlBeanAdapter(URL url) throws IOException, SAXException{
		this(parse(url));
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param node node of this instance
	 */
	public XmlBeanAdapter(Node node) {
		this.node = node;
		this.xpath = XPathFactory.newInstance().newXPath();
		this.xpath.setNamespaceContext(ns);
		return;
	}

	// ======================================================================
	// Associate Namespace with Prefix
	// ======================================================================
	/**
	 * Set namespace url to prefix.
	 *
	 * @param prefix preix
	 * @param uri namespace uri
	 */
	public void setNamespaceURI(String prefix, String uri){
		ns.setNamespaceURI(prefix, uri);
	}

	// ======================================================================
	// Retrieve Node
	// ======================================================================
	/**
	 * Retrieve node for specified xpath expression.
	 *
	 * @param node node of base position
	 * @param expr xpath expression
	 * @return node
	 */
	protected Node node(Node node, String expr){
		try {
			return (Node)xpath.evaluate(expr, node, XPathConstants.NODE);
		} catch(XPathException ex){
			throw new IllegalStateException("invalid xpath expression (this maybe bug): " + expr, ex);
		}
	}

	// ======================================================================
	// Retrieve Node
	// ======================================================================
	/**
	 * Retrieve node for specified xpath expression.
	 *
	 * @param expr xpath expression
	 * @return node
	 */
	protected Node node(String expr){
		return node(node, expr);
	}

	// ======================================================================
	// Retrieve Nodeset
	// ======================================================================
	/**
	 * Retrieve nodeset for specified xpath expression.
	 *
	 * @param expr xpath expression
	 * @return iterable of nodes
	 */
	protected Iterable<Node> nodeset(String expr){
		List<Node> list = new ArrayList<Node>();
		try {
			NodeList nl = (NodeList)xpath.evaluate(expr, node, XPathConstants.NODESET);
			for(int i=0; i<nl.getLength(); i++){
				list.add(nl.item(i));
			}
		} catch(XPathException ex){
			throw new IllegalStateException("invalid xpath expression (this maybe bug): " + expr, ex);
		}
		return list;
	}

	// ======================================================================
	// Retrieve Nodeset
	// ======================================================================
	/**
	 * Retrieve nodeset for specified xpath expression.
	 *
	 * @param expr xpath expression
	 * @return iterable of nodes
	 */
	protected Iterable<Node> nodeset(Node node, String expr){
		List<Node> list = new ArrayList<Node>();
		try {
			NodeList nl = (NodeList)xpath.evaluate(expr, node, XPathConstants.NODESET);
			for(int i=0; i<nl.getLength(); i++){
				list.add(nl.item(i));
			}
		} catch(XPathException ex){
			throw new IllegalStateException("invalid xpath expression (this maybe bug): " + expr, ex);
		}
		return list;
	}

	// ======================================================================
	// Retrieve Element
	// ======================================================================
	/**
	 * Retrieve element for specified xpath expression.
	 *
	 * @param node base node
	 * @param expr xpath expression
	 * @return element
	 */
	protected Element elem(Node node, String expr){
		return (Element)node(node, expr);
	}

	// ======================================================================
	// Retrieve Element
	// ======================================================================
	/**
	 * Retrieve element for specified xpath expression.
	 *
	 * @param expr xpath expression
	 * @return element
	 */
	protected Element elem(String expr){
		return (Element)node(expr);
	}

	// ======================================================================
	// Retrieve Nodeset
	// ======================================================================
	/**
	 * Retrieve nodeset for specified element.
	 *
	 * @param expr xpath expression
	 * @return iterable of elements
	 */
	protected Iterable<Element> elemset(String expr){
		return elemset(node, expr);
	}

	// ======================================================================
	// Retrieve Nodeset
	// ======================================================================
	/**
	 * Retrieve nodeset for specified element.
	 *
	 * @param expr xpath expression
	 * @return iterable of elements
	 */
	protected Iterable<Element> elemset(Node node, String expr){
		List<Element> list = new ArrayList<Element>();
		for(Node n: nodeset(node, expr)){
			list.add((Element)n);
		}
		return list;
	}

	// ======================================================================
	// Retrieve String
	// ======================================================================
	/**
	 * Retrieve text string value for specified node.
	 *
	 * @param expr xpath expression
	 * @param def default value
	 * @return text string
	 */
	protected String string(String expr, String def){
		Node node = node(expr);
		if(node == null){
			return def;
		}
		String value = node.getTextContent();
		logger.trace(expr + "=" + value);
		return value;
	}

	// ======================================================================
	// Retrieve String
	// ======================================================================
	/**
	 * Retrieve text string value for specified node. The null will return
	 * when node not found.
	 *
	 * @param expr xpath expression
	 * @return text string
	 */
	protected String string(String expr){
		return string(expr, null);
	}

	// ======================================================================
	// Retrieve Boolean
	// ======================================================================
	/**
	 * Retrieve boolean value for specified node.
	 *
	 * @param node node
	 * @param expr xpath expression
	 * @return boolean value
	 */
	protected boolean bool(Node node, String expr){
		try {
			return (Boolean)xpath.evaluate(expr, node, XPathConstants.BOOLEAN);
		} catch(XPathException ex){
			throw new IllegalStateException("invalid xpath expression (this maybe bug): " + expr, ex);
		}
	}

	// ======================================================================
	// Parse XML Document
	// ======================================================================
	/**
	 * Parse XML document from the stream for specified url and returns DOM.
	 *
	 * @param url URL of xml document
	 * @throws IOException if fail to read stream
	 * @throws SAXException if not well-formed xml
	 * @return xml document
	 */
	public static Document parse(URL url) throws IOException, SAXException {
		InputSource is = new InputSource(url.openStream());
		is.setSystemId(url.toString());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(is);
		} catch(ParserConfigurationException ex){
			throw new IllegalStateException(ex);
		}
	}

//	// ======================================================================
//	// Retrieve String
//	// ======================================================================
//	/**
//	 * Retrieve text string value for specified node. The null will return
//	 * when node not found.
//	 *
//	 * @param expr xpath expression
//	 * @param type return type
//	 * @return text string
//	 */
//	private Object get(String expr, Class<?> type){
//		Node node = node(expr);
//		if(node == null){
//			return null;
//		}
//
//		if(type.equals(String.class)){
//			return node.getTextContent();
//		}
//		if(type.equals(byte.class)){
//			return Byte.parseByte(node.getTextContent());
//		}
//		if(type.equals(short.class)){
//			return Short.parseShort(node.getTextContent());
//		}
//		if(type.equals(int.class)){
//			return Integer.parseInt(node.getTextContent());
//		}
//		if(type.equals(long.class)){
//			return Long.parseLong(node.getTextContent());
//		}
//		if(type.equals(float.class)){
//			return Float.parseFloat(node.getTextContent());
//		}
//		if(type.equals(double.class)){
//			return Double.parseDouble(node.getTextContent());
//		}
//
//		logger.warn("unsupported conversion type: " + type.getName());
//		return null;
//	}
//
//	// ======================================================================
//	// Retrieve Nodeset
//	// ======================================================================
//	/**
//	 * Retrieve nodeset for specified element.
//	 *
//	 * @param clazz return type
//	 * @return iterable of elements
//	 */
//	public <T> T wrap(Class<T> clazz){
//		return null;
//	}
//
//	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	// @XPathBinding:
//	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/**
//	 * Retrieve nodeset for specified element.
//	 *
//	 * @param expr xpath expression
//	 * @param elem base node
//	 * @return iterable of elements
//	 */
//	@Target(ElementType.METHOD)
//	@Retention(RetentionPolicy.RUNTIME)
//	public @interface XPathBinding {
//		String value();
//	}
//
//	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	//
//	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/**
//	 * Retrieve nodeset for specified element.
//	 *
//	 * @param expr xpath expression
//	 * @param elem base node
//	 * @return iterable of elements
//	 */
//	private class Invoker implements InvocationHandler {
//
//		// ==================================================================
//		//
//		// ==================================================================
//		/**
//		 *
//		 * @param proxy proxy instance
//		 * @param method called method
//		 * @param args method arguments
//		 * @return method result
//		 * @throws Throwable if fail to call method
//		 */
//		@Override
//		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//
//			XPathBinding binding = method.getAnnotation(XPathBinding.class);
//			if(binding != null){
//				String xpath = binding.value();
//				Class<?> type = method.getReturnType();
//				return get(xpath, type);
//			}
//
//			logger.warn("unbound xpath method: " + method.toGenericString());
//			return null;
//		}
//	}

}
