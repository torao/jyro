/* **************************************************************************
 * Copyright (C) 2008 BJoRFUAN. All Right Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * BSD License, and comes with NO WARRANTY.
 *
 *                                                 torao <torao@bjorfuan.com>
 *                                                       http://www.moyo.biz/
 * $Id:$
*/
package org.koiroha.jyro.webapp;

import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

import org.koiroha.jyro.impl.*;
import org.koiroha.jyro.jmx.JyroMXBean;
import org.w3c.dom.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Snapshot: Snapshot Utility
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Utility class to build snapshot XML of {@link JyroImpl}.
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/04 Java SE 6
 */
public final class Snapshot {

	// ======================================================================
	// Document
	// ======================================================================
	/**
	 * The XML document that this instance building.
	 */
	private Document doc = null;

	// ======================================================================
	// Number Format
	// ======================================================================
	/**
	 */
	private final NumberFormat nf;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * The default constructor is hidden in class.
	 *
	 * @param locale user locale
	 */
	public Snapshot(Locale locale) {
		this.nf = NumberFormat.getNumberInstance(locale);
		return;
	}

	// ======================================================================
	// Make Snapshot
	// ======================================================================
	/**
	 * Make snapshot for specified jyro instance.
	 *
	 * @param jyro jyro instance
	 * @return snapshot xml document
	 */
	public synchronized Document makeSnapshot(JyroMXBean jyro){

		// create new document
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.doc = builder.newDocument();
		} catch(ParserConfigurationException ex){
			throw new IllegalStateException(ex);
		}

		// append root
		Element root = doc.createElement("jyro");
		root.appendChild(createProperty("location", jyro.getDirectory()));
		doc.appendChild(root);

		// append cores
		for(CoreImpl core: jyro.getCores()){
			root.appendChild(createCore(core));
		}
		return this.doc;
	}

	// ======================================================================
	// Create JyroCore Element
	// ======================================================================
	/**
	 * Create and return JyroCore element.
	 *
	 * @param core jyro core
	 * @return core element
	 */
	private Element createCore(CoreImpl core){
		Element elem = doc.createElement("core");
		elem.appendChild(createProperty("name", core.getName()));
		elem.appendChild(createUptimeProperty("uptime", core.getUptime()));
		for(org.koiroha.jyro.impl.NodeImpl node: core.getNodes()){
			elem.appendChild(createNode(node));
		}
		return elem;
	}

	// ======================================================================
	// Create Node Element
	// ======================================================================
	/**
	 * Create and return Node element.
	 *
	 * @param node Node instance
	 * @return node element
	 */
	private Element createNode(org.koiroha.jyro.impl.NodeImpl node){
		Element elem = doc.createElement("node");
		elem.appendChild(createProperty("id", node.getId()));
		elem.appendChild(createProperty("minimumWorkers", node.getMinimumWorkers()));
		elem.appendChild(createProperty("maximumWorkers", node.getMaximumWorkers()));
		elem.appendChild(createProperty("activeWorkers", node.getActiveWorkers()));
		elem.appendChild(createProperty("stackSize", node.getStackSize()));
		elem.appendChild(createProperty("waitingJobs", node.getWaitingJobs()));
		elem.appendChild(createProperty("loadAverage", node.getLoadAverage()));
		elem.appendChild(createProperty("totalJobCount", node.getTotalJobCount()));
		elem.appendChild(createProperty("totalJobTime", node.getTotalJobTime()));
		return elem;
	}

	// ======================================================================
	// Create Property Element
	// ======================================================================
	/**
	 * Create and return property element.
	 *
	 * @param name property name
	 * @param num property value
	 * @return property element
	 */
	private Element createProperty(String name, double... num){
		StringBuilder pf = new StringBuilder();
		StringBuilder hf = new StringBuilder();
		for (double value : num) {
			if(pf.length() != 0){
				pf.append(' ');
				hf.append(' ');
			}
			pf.append(value);
			hf.append(nf.format(value));
		}
		return createProperty(name, pf.toString(), hf.toString());
	}

	// ======================================================================
	// Create Property Element
	// ======================================================================
	/**
	 * Create and return property element.
	 *
	 * @param name property name
	 * @param num property value
	 * @return property element
	 */
	private Element createProperty(String name, long num){
		return createProperty(name, String.valueOf(num), nf.format(num));
	}

	// ======================================================================
	// Create Property Element
	// ======================================================================
	/**
	 * Create and return property element.
	 *
	 * @param name property name
	 * @param num property value
	 * @return property element
	 */
	private Element createUptimeProperty(String name, long num){
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		return createProperty(name, String.valueOf(num), df.format(new Date(num)));
	}

	// ======================================================================
	// Create Property Element
	// ======================================================================
	/**
	 * Create and return property element.
	 *
	 * @param name property name
	 * @param value property value
	 * @return property element
	 */
	private Element createProperty(String name, String value){
		return createProperty(name, String.valueOf(value), value);
	}

	// ======================================================================
	// Create Property Element
	// ======================================================================
	/**
	 * Create and return property element.
	 *
	 * @param name property name
	 * @param value property value
	 * @param text human readable text for value
	 * @return property element
	 */
	private Element createProperty(String name, String value, String text){
		Element elem = doc.createElement("property");
		elem.setAttribute("name", name);
		elem.setAttribute("value", value);
		elem.appendChild(doc.createTextNode(text));
		return elem;
	}

}
