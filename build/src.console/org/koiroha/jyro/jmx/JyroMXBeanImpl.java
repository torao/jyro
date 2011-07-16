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
package org.koiroha.jyro.jmx;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.Iterator;

import javax.management.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroMXBeanImpl:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/16 Java SE 6
 */
public class JyroMXBeanImpl implements JyroMXBean, Serializable {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version UID of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JyroMXBeanImpl.class);

	// ======================================================================
	// Jyro Instance
	// ======================================================================
	/**
	 * Jyro instance to watch on JMX.
	 */
	private final Jyro jyro;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param jyro Jyro instance
	 */
	public JyroMXBeanImpl(Jyro jyro) {
		this.jyro = jyro;
		return;
	}

	// ======================================================================
	// Refer Name
	// ======================================================================
	/**
	 * Refer human-readable name of instance.
	 *
	 * @return name
	 */
	@Override
	public String getName(){
		return jyro.getName();
	}

	// ======================================================================
	// Retrieve Home Directory
	// ======================================================================
	/**
	 * Retrieve home directory of jyro instance.
	 *
	 * @return home directory
	 */
	@Override
	public String getDirectory(){
		return jyro.getDirectory().getAbsolutePath();
	}

	// ======================================================================
	// Retrieve Core Count
	// ======================================================================
	/**
	 * Retrieve core count of jyro instance.
	 *
	 * @return core count
	 */
	@Override
	public int getCoreCount(){
		int count = 0;
		Iterator<?> it = jyro.getCores().iterator();
		while(it.hasNext()){
			it.next();
			count ++;
		}
		return count;
	}

	// ======================================================================
	// Refer Active Workers
	// ======================================================================
	/**
	 * Refer active workers of all nodes.
	 *
	 * @return active workers
	*/
	@Override
	public int getActiveWorkers(){
		int count = 0;
		for(JyroCore core: jyro.getCores()){
			for(Node node: core.getNodes()){
				count += node.getActiveWorkers();
			}
		}
		return count;
	}

	// ======================================================================
	// Reload Cores
	// ======================================================================
	/**
	 * Reload all cores.
	*/
	@Override
	public void startup() throws JyroException{
		logger.debug("starting up...");
		jyro.startup();
		return;
	}

	// ======================================================================
	// Reload Cores
	// ======================================================================
	/**
	 * Reload all cores.
	*/
	@Override
	public void shutdown() throws JyroException{
		logger.debug("shutting down...");
		jyro.shutdown();
		return;
	}

	// ======================================================================
	// Reload Configuration
	// ======================================================================
	/**
	 * Reload jyro configuration via JMX.
	 */
	@Override
	public void reload() {
		logger.debug("reloading configurations...");
		return;
	}

	// ======================================================================
	// Register MXBean
	// ======================================================================
	/**
	 * Register MXBean for specified Jyro instance.
	 *
	 * @param jyro the instance to management
	 * @throws InstanceAlreadyExistsException other instance that has same name already exists
	 * @throws MBeanRegistrationException fail to regist
	 * @throws NotCompliantMBeanException
	 * @throws MalformedObjectNameException
	 */
	public static void register(Jyro jyro) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {

		// register MXBean for Jyro instance
		String name = String.format("org.koiroha.jyro:name=%s", jyro.getName());
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		JyroMXBean bean = new JyroMXBeanImpl(jyro);
		server.registerMBean(bean, new ObjectName(name));
		logger.debug("register MXBean: " + name);

		// register each core instance
		for(JyroCore core: jyro.getCores()){
			String cname = name + ",core=" + core.getName();
			CoreMXBean cbean = new CoreMXBeanImpl(core);
			server.registerMBean(cbean, new ObjectName(cname));

			// register each node instance
			for(Node node: core.getNodes()){
				String nname = cname + ",node=" + node.getId();
				NodeMXBean nbean = new NodeMXBeanImpl(node);
				server.registerMBean(nbean, new ObjectName(nname));
			}
		}
		return;
	}

	// ======================================================================
	// Unregister MXBean
	// ======================================================================
	/**
	 * Unregister MXBean for specified Jyro instance.
	 *
	 * @param jyro the instance to release
	 * @throws InstanceNotFoundException instance not found
	 * @throws MBeanRegistrationException fail to regist
	 * @throws MalformedObjectNameException
	 */
	public static void unregister(Jyro jyro) throws InstanceNotFoundException, MBeanRegistrationException, MalformedObjectNameException {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		String name = String.format("org.koiroha.jyro:name=%s", jyro.getName());

		for(JyroCore core: jyro.getCores()){
			String cname = name + ",core=" + core.getName();

			// unregister each node instance
			for(Node node: core.getNodes()){
				String nname = cname + ",node=" + node.getId();
				server.unregisterMBean(new ObjectName(nname));
			}

			// unregister each core instance
			server.unregisterMBean(new ObjectName(cname));
		}

		// unregister jyro instance
		server.unregisterMBean(new ObjectName(name));
		logger.debug("unregister MXBean: " + name);
		return;
	}

}
