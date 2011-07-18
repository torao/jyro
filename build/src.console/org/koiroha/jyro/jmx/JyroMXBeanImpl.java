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

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;

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
	// Jyro Name
	// ======================================================================
	/**
	 * The name of Jyro instance.
	 */
	private final String name;

	// ======================================================================
	// Home Directory
	// ======================================================================
	/**
	 * The home directory of Jyro.
	 */
	private final File dir;

	// ======================================================================
	// Class Loader
	// ======================================================================
	/**
	 * Class loader.
	 */
	private final ClassLoader parent;

	// ======================================================================
	// Property
	// ======================================================================
	/**
	 * Property
	 */
	private final Properties prop;

	// ======================================================================
	// Jyro Instance
	// ======================================================================
	/**
	 * Jyro instance to watch on JMX.
	 */
	private Jyro jyro = null;

	// ======================================================================
	// Regist Flag
	// ======================================================================
	/**
	 * The flag whether this instance registered to MBeanServer.
	 */
	private boolean regist = false;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param name instance name
	 * @param dir home directory
	 * @param parent parent class loader
	 * @param prop init properties
	 * @throws JyroException if fail to build jyro instance
	 */
	public JyroMXBeanImpl(String name, File dir, ClassLoader parent, Properties prop) throws JyroException{
		this.name = name;
		this.dir = dir;
		this.parent = parent;
		this.prop = prop;
		load();
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
	public void startup() {
		logger.debug("starting up...");
		try{
			jyro.startup();
		} catch(JyroException ex){
			logger.error("fail to startup Jyro instance", ex);
			throw new IllegalStateException(ex.toString());
		} catch(RuntimeException ex){
			logger.error("fail to startup Jyro instance", ex);
			throw ex;
		}
		return;
	}

	// ======================================================================
	// Reload Cores
	// ======================================================================
	/**
	 * Reload all cores.
	*/
	@Override
	public void shutdown() {
		logger.debug("shutting down...");
		try{
			jyro.shutdown();
		} catch(JyroException ex){
			logger.error("fail to shutdown Jyro instance", ex);
			throw new IllegalStateException(ex.toString());
		} catch(RuntimeException ex){
			logger.error("fail to startup Jyro instance", ex);
			throw ex;
		}
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

		boolean regist = this.regist;
		if(regist){
			try {
				unregist();
			} catch(Exception ex){
				logger.error("fail to unregister JMX", ex);
				throw new IllegalStateException(ex.toString());
			}
		}

		try {
			load();
		} catch(JyroException ex){
			logger.error("fail to reload configuration", ex);
			throw new IllegalStateException(ex.toString());
		}


		if(regist){
			try {
				regist();
			} catch(Exception ex){
				logger.error("fail to unregister JMX", ex);
				throw new IllegalStateException(ex.toString());
			}
		}

		return;
	}

	// ======================================================================
	// Register Instance
	// ======================================================================
	/**
	 * Register this MXBean to MBeanServer.
	 *
	 * @throws InstanceAlreadyExistsException other instance that has same name already exists
	 * @throws MBeanRegistrationException fail to regist
	 * @throws NotCompliantMBeanException
	 * @throws MalformedObjectNameException
	 */
	public void regist() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {

		// register MXBean for Jyro instance
		String name = String.format("org.koiroha.jyro:name=%s", jyro.getName());
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		server.registerMBean(this, new ObjectName(name));
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
		regist = true;
		return;
	}

	// ======================================================================
	// Unregister MXBean
	// ======================================================================
	/**
	 * Unregister MXBean for specified Jyro instance.
	 *
	 * @throws InstanceNotFoundException instance not found
	 * @throws MBeanRegistrationException fail to regist
	 * @throws MalformedObjectNameException
	 */
	public void unregist() throws InstanceNotFoundException, MBeanRegistrationException, MalformedObjectNameException {
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
		regist = false;
		return;
	}

	// ======================================================================
	// Reload Configuration
	// ======================================================================
	/**
	 * Reload jyro configuration via JMX.
	 *
	 * @throws JyroException if fail to load configuration
	 */
	private void load() throws JyroException{

		// shutdown jyro service
		if(jyro != null){
			this.jyro.shutdown();
			this.jyro = null;
		}

		// create new jyro instance
		this.jyro = new Jyro(name, dir, parent, prop);
		return;
	}

}
