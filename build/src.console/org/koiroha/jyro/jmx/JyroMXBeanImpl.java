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
	 * The name of Jyro instance to rebuild instance.
	 */
	private final String name;

	// ======================================================================
	// Home Directory
	// ======================================================================
	/**
	 * The home directory of Jyro to rebuild instance.
	 */
	private final File dir;

	// ======================================================================
	// Class Loader
	// ======================================================================
	/**
	 * The class loader of Jyro to rebuild instance.
	 */
	private final ClassLoader parent;

	// ======================================================================
	// Property
	// ======================================================================
	/**
	 * The property of Jyro to rebuild instance.
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
	// MBean Prefix
	// ======================================================================
	/**
	 * The prefix of MXBean name.
	 */
	private final String prefix;

	// ======================================================================
	// Regist Flag
	// ======================================================================
	/**
	 * The flag whether this instance registered to MBeanServer.
	 */
	private boolean regist = false;

	// ======================================================================
	// Modification Detect Time span
	// ======================================================================
	/**
	 * Modification detection time span.
	 */
	private long modificationDetect = 2000;

	// ======================================================================
	// Watchdog
	// ======================================================================
	/**
	 * Watchdog task to detect modification and reload.
	 */
	private TimerTask watchdog = null;

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
		this.prefix = "org.koiroha.jyro:name=" + name;
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

		// start watchdog
		if(watchdog == null){
			watchdog = new Watchdog();
			Jyro.TIMER.scheduleAtFixedRate(watchdog, modificationDetect, modificationDetect);
			logger.debug("start modification detect and reload watchdog");
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

		// stop watchdog
		if(watchdog != null){
			watchdog.cancel();
			watchdog = null;
			logger.debug("stop watchdog");
		}

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
	public synchronized void reload() {
		logger.debug("reloading configurations...");
		try {
			boolean regist = this.regist;
			if(jyro != null){

				// shutdown currently running jyro instance
				this.jyro.shutdown();

				// unregister mxbean if this registered
				if(regist){
					unregister();
				}

				this.jyro = null;
			}

			// load configuration and rebuild instance
			load();
			this.jyro.startup();

			// regist mxbean
			if(regist){
				registerAllCores();
			}

		} catch(Exception ex){
			logger.error("fail to reload jyro configuration", ex);
			throw new IllegalStateException(ex.toString());
		}

		return;
	}

	// ======================================================================
	// Register Instance
	// ======================================================================
	/**
	 * Register this MXBean to MBeanServer.
	 *
	 * @param childOnly if unregister all child without self
	 * @throws InstanceAlreadyExistsException other instance that has same name already exists
	 * @throws MBeanRegistrationException fail to regist
	 * @throws NotCompliantMBeanException
	 * @throws MalformedObjectNameException
	 */
	public synchronized void register() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {

		// register MXBean for Jyro instance
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		server.registerMBean(this, new ObjectName(prefix));
		logger.debug("register MXBean: " + prefix);

		// register all cores
		registerAllCores();
		return;
	}

	// ======================================================================
	// Register Instance
	// ======================================================================
	/**
	 * Register this MXBean to MBeanServer.
	 *
	 * @param childOnly if unregister all child without self
	 * @throws InstanceAlreadyExistsException other instance that has same name already exists
	 * @throws MBeanRegistrationException fail to regist
	 * @throws NotCompliantMBeanException
	 * @throws MalformedObjectNameException
	 */
	private void registerAllCores() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
		assert(Thread.holdsLock(this));

		// register each core instance
		for(JyroCore core: jyro.getCores()){
			register(core);
		}
		regist = true;
		return;
	}

	// ======================================================================
	// Register Instance
	// ======================================================================
	/**
	 * Register this MXBean to MBeanServer.
	 *
	 * @param childOnly if unregister all child without self
	 * @throws InstanceAlreadyExistsException other instance that has same name already exists
	 * @throws MBeanRegistrationException fail to regist
	 * @throws NotCompliantMBeanException
	 * @throws MalformedObjectNameException
	 */
	private void register(JyroCore core) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
		assert(Thread.holdsLock(this));

		// register mxbean for specified core instance
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		String cname = prefix + ",core=" + core.getName();
		CoreMXBean cbean = new CoreMXBeanImpl(core);
		server.registerMBean(cbean, new ObjectName(cname));

		// register each node instance
		for(Node node: core.getNodes()){
			String nname = cname + ",node=" + node.getId();
			NodeMXBean nbean = new NodeMXBeanImpl(node);
			server.registerMBean(nbean, new ObjectName(nname));
		}
		return;
	}

	// ======================================================================
	// Unregister Instance
	// ======================================================================
	/**
	 * Unregister this MXBean to MBeanServer.
	 *
	 * @param childOnly if unregister all child without self
	 * @throws InstanceNotFoundException instance not found
	 * @throws MBeanRegistrationException fail to regist
	 * @throws MalformedObjectNameException
	 */
	public synchronized void unregister() throws InstanceNotFoundException, MBeanRegistrationException, MalformedObjectNameException {

		// unregister jyro instance
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		server.unregisterMBean(new ObjectName(prefix));
		logger.debug("unregister MXBean: " + prefix);
		regist = false;

		// unregister all cores
		unregisterAllCores();
		return;
	}

	// ======================================================================
	// Unregister MXBean
	// ======================================================================
	/**
	 * Unregister MXBean for specified Jyro instance.
	 *
	 * @param childOnly if unregister all child without self
	 * @throws InstanceNotFoundException instance not found
	 * @throws MBeanRegistrationException fail to regist
	 * @throws MalformedObjectNameException
	 */
	private void unregisterAllCores() throws InstanceNotFoundException, MBeanRegistrationException, MalformedObjectNameException {
		assert(Thread.holdsLock(this));
		for(JyroCore core: jyro.getCores()){
			unregister(core);
		}
		return;
	}

	// ======================================================================
	// Unregister Core
	// ======================================================================
	/**
	 * Unregister MXBean for specified core instance.
	 *
	 * @param childOnly if unregister all child without self
	 * @throws InstanceNotFoundException instance not found
	 * @throws MBeanRegistrationException fail to regist
	 * @throws MalformedObjectNameException
	 */
	private void unregister(JyroCore core) throws InstanceNotFoundException, MBeanRegistrationException, MalformedObjectNameException {
		assert(Thread.holdsLock(this));
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		String cname = prefix + ",core=" + core.getName();

		// unregister each node instance
		for(Node node: core.getNodes()){
			String nname = cname + ",node=" + node.getId();
			server.unregisterMBean(new ObjectName(nname));
		}

		// unregister specified core instance
		server.unregisterMBean(new ObjectName(cname));
		return;
	}

	// ======================================================================
	// Load Configuration
	// ======================================================================
	/**
	 * Load jyro configuration via JMX.
	 *
	 * @throws JyroException if fail to load configuration
	 */
	private void load() throws JyroException{
		assert(this.jyro == null);
		this.jyro = new Jyro(name, dir, parent, prop);
		return;
	}

	// ======================================================================
	// Shutdown Services
	// ======================================================================
	/**
	 * Shutdown all services in this instance.
	 *
	 * @throws JyroException if fail to shutdown jyro
	 */
	private class Watchdog extends TimerTask {
		/**
		 * Check modification of all cores and reload if necessary.
		 */
		@Override
		public void run(){
			for(JyroCore core: jyro.getCores()){
				if(core.isModified()){
					logger.info("configuration modification detected, reload automatically");
					reload();
					return;
				}
			}
			return;
		}
	}

}
