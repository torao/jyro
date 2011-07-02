/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro;

import java.io.*;
import java.util.*;

import javax.xml.xpath.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.util.Text;
import org.koiroha.xml.DefaultNamespaceContext;
import org.w3c.dom.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroFactory: Jyro Factory
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * The factory class to build Jyro instance from configuration xml.
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/03 Java SE 6
 */
public class JyroFactory {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JyroFactory.class);

	// ======================================================================
	// XML Namespace
	// ======================================================================
	/**
	 * XML Namespace of Jyro configuration xml.
	 */
	public static final String XMLNS10 = "http://www.koiroha.org/xmlns/jyro/configuration_1.0";

	// ======================================================================
	// Root Node
	// ======================================================================
	/**
	 * Root element of xml configuration.
	 */
	private final Element root;

	// ======================================================================
	// XPath
	// ======================================================================
	/**
	 * XPath to parse xml.
	 */
	private final XPath xpath;

	// ======================================================================
	// Properties Map
	// ======================================================================
	/**
	 * Properties map defined in configuration.
	 */
	private final Map<String,String> properties = new HashMap<String,String>();

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * The constructor is hidden in this class.
	 * 
	 * @param root root element of xml configuration
	 * @param prop default properties
	 */
	private JyroFactory(Element root, Properties prop) {
		DefaultNamespaceContext nc = new DefaultNamespaceContext();
		nc.setNamespaceURI("j", XMLNS10);
		this.root = root;
		XPathFactory factory = XPathFactory.newInstance();
		this.xpath = factory.newXPath();
		this.xpath.setNamespaceContext(nc);

		// retrieve all system properties
		for(Map.Entry<Object,Object> e: System.getProperties().entrySet()){
			properties.put(e.getKey().toString(), e.getValue().toString());
		}

		// retrieve all specified properties
		for(Map.Entry<Object,Object> e: prop.entrySet()){
			properties.put(e.getKey().toString(), e.getValue().toString());
		}
		return;
	}

	// ======================================================================
	// Parse Configuration
	// ======================================================================
	/**
	 * Parse xml configuration specified in constructor.
	 * 
	 * @return Jyro instance
	 * @throws XPathException invalid xpath (bug?)
	 */
	private Jyro parse() throws XPathException {

		// parse all properties in configuration
		NodeList nl = (NodeList)xpath.evaluate("j:jyro/j:property", root, XPathConstants.NODESET);
		for(int i=0; i<nl.getLength(); i++){
			Element elem = (Element)nl.item(i);
			String name = elem.getAttribute("name");
			String value = f(elem.getAttribute("value"));
			properties.put(name, value);
		}

		// build all nodes
		nl = (NodeList)xpath.evaluate("j:jyro/j:node", root, XPathConstants.NODESET);
		for(int i=0; i<nl.getLength(); i++){
			Element elem = (Element)nl.item(i);
			String task = f(elem.getAttribute("task"));
		}
		return;
	}

	// ======================================================================
	// Format String
	// ======================================================================
	/**
	 * Format specified string value with variable.
	 * 
	 * @param value string to format
	 * @return formatted string
	 */
	private String f(String value){
		return Text.format(value, properties);
	}

	// ======================================================================
	// Create Instance
	// ======================================================================
	/**
	 * Create Jyro instance from specified stream.
	 * 
	 * @param in input stream of configuration xml
	 * @return Jyro instance
	 * @throws IOException if fail to read
	 */
	public static Jyro createInstance(InputStream in) throws JyroException {
		
	}

	// ======================================================================
	// Create Instance
	// ======================================================================
	/**
	 * Create Jyro instance from specified DOM node.
	 * 
	 * @param node xml node of configuration
	 * @return Jyro instance
	 * @throws JyroException if fail to build instance
	 */
	public static Jyro createInstance(org.w3c.dom.Node node) throws JyroException {
		try {
			
		} catch(XPathException ex){
			logger.error("unexpected exception, this maybe bug; " + ex);
			throw new IllegalStateException(ex);
		}
		return null;
	}

	// ======================================================================
	// Create Instance
	// ======================================================================
	/**
	 * Create Jyro instance from specified DOM element with properties.
	 * 
	 * @param config xml element of configuration
	 * @param prop default properties
	 * @return Jyro instance
	 * @throws JyroException if fail to build instance
	 */
	public static Jyro createInstance(Element config, Properties prop) throws JyroException {
		try {
			
		} catch(XPathException ex){
			logger.error("unexpected exception, this maybe bug; " + ex);
			throw new IllegalStateException(ex);
		}
		return null;
	}

}
