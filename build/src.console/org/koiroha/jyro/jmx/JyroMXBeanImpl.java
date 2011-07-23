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
import org.koiroha.jyro.impl.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JyroMXBeanImpl: Jyro MXBean Implementation
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
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
	private JyroImpl jyro = null;

	// ======================================================================
	// MBean Prefix
	// ======================================================================
	/**
	 * The prefix of MXBean name.
	 */
	private final String prefix;

	// ======================================================================
	// Running Flag
	// ======================================================================
	/**
	 * The flag whether this instance runngin or not.
	 */
	private boolean running = false;

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
		this.prefix = DOMAIN + ":name=" + ObjectName.quote(name);
		load();
		return;
	}

	// ======================================================================
	// Refer Jyro Instance
	// ======================================================================
	/**
	 * Refer Jyro instance that this mxbean manages.
	 *
	 * @return Jyro instance
	 */
	public JyroImpl getJyro(){
		return jyro;
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

		// startup jyro instance
		try{
			jyro.startup();
		} catch(JyroException ex){
			logger.error("fail to startup Jyro instance", ex);
			throw new IllegalStateException(ex.toString());
		} catch(RuntimeException ex){
			logger.error("fail to startup Jyro instance", ex);
			throw ex;
		}
		this.running = true;

		// startup watchdog
		if(watchdog == null){
			watchdog = new Watchdog();
			JyroImpl.TIMER.scheduleAtFixedRate(watchdog, modificationDetect, modificationDetect);
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
		this.running = false;

		// shutdown watchdog
		if(watchdog != null){
			watchdog.cancel();
			watchdog = null;
			logger.debug("stop watchdog");
		}

		// shutdown jyro instance
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
		boolean running = this.running;
		try {

			// shutdown and unregister if instance still available
			if(jyro != null){
				this.jyro.shutdown();
				this.jyro = null;
			}

			// load configuration and rebuild instance
			load();

			// startup if instance has been started
			if(running){
				this.jyro.startup();
			}

			// regist mxbean
			validateRegistration();

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
	 * @throws InstanceAlreadyExistsException other instance that has same name already exists
	 * @throws MBeanRegistrationException fail to regist
	 */
	public synchronized void register() throws InstanceAlreadyExistsException, MBeanRegistrationException {
		try {

			// register MXBean for Jyro instance
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.registerMBean(this, new ObjectName(prefix));
			logger.debug("register MXBean: " + prefix);

			// register all cores
			validateRegistration();

		} catch(NotCompliantMBeanException ex){
			throw new IllegalStateException("this maybe internal bug!", ex);
		} catch(MalformedObjectNameException ex){
			throw new IllegalStateException("this maybe internal bug!", ex);
		}
		return;
	}

	// ======================================================================
	// Unregister Instance
	// ======================================================================
	/**
	 * Unregister this MXBean to MBeanServer.
	 *
	 * @throws InstanceNotFoundException instance not found
	 * @throws MBeanRegistrationException fail to regist
	 */
	public synchronized void unregister() throws InstanceNotFoundException, MBeanRegistrationException {
		try {

			// unregister jyro instance
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.unregisterMBean(new ObjectName(prefix));
			logger.debug("unregister MXBean: " + prefix);

			// unregister all cores
			for(JyroCore core: jyro.getCores()){
				String cname = prefix + ",core=" + ObjectName.quote(core.getName());

				// unregister each node instance
				for(Node node: core.getNodes()){
					String nname = cname + ",node=" + ObjectName.quote(node.getId());
					server.unregisterMBean(new ObjectName(nname));
				}

				// unregister specified core instance
				server.unregisterMBean(new ObjectName(cname));
			}
		} catch(MalformedObjectNameException ex){
			throw new IllegalStateException("this maybe internal bug!", ex);
		}
		return;
	}

	// ======================================================================
	// Validate MXBean Registration
	// ======================================================================
	/**
	 * Validate MXBean registration.
	 *
	 * @throws InstanceAlreadyExistsException other instance that has same name already exists
	 * @throws MBeanRegistrationException fail to regist
	 * @throws NotCompliantMBeanException
	 * @throws MalformedObjectNameException
	 */
	private void validateRegistration() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
		assert(Thread.holdsLock(this));
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		for(JyroCore core: getJyro().getCores()){

			// register core mxbean if it is not registered
			ObjectName cname = new ObjectName(prefix + ",core=" + ObjectName.quote(core.getName()));
			if(! server.isRegistered(cname)){
				CoreMXBean cbean = new CoreMXBeanImpl(this, core.getName());
				server.registerMBean(cbean, cname);
			}

			// TODO remove alread rejected cores

			// register node mxbean if it is not registered
			for(Node node: core.getNodes()){
				ObjectName nname = new ObjectName(cname + ",node=" + ObjectName.quote(node.getId()));
				if(! server.isRegistered(nname)){
					NodeMXBean nbean = new NodeMXBeanImpl(this, core.getName(), node.getId());
					server.registerMBean(nbean, nname);
				}

				// TODO remove alread rejected nodes
			}
		}
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
		this.jyro = new JyroImpl(name, dir, parent, prop);
		return;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Watchdog: Reload Detection
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * TimerTask to detect modification and reload Jyro instance.
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
