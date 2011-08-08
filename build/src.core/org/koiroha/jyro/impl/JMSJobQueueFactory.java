/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyro.impl;

import java.util.*;
import java.util.concurrent.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.koiroha.jyro.Job;
import org.koiroha.jyro.JobQueue;
import org.koiroha.jyro.JobQueueFactory;
import org.koiroha.jyro.JobQueueImpl;
import org.koiroha.jyro.JyroException;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JMSJobQueueFactory:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public class JMSJobQueueFactory implements JobQueueFactory {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(JMSJobQueueFactory.class);

	// ======================================================================
	// Broker URL
	// ======================================================================
	/**
	 * Broker URL
	 */
	private QueueConnection con = null;

	// ======================================================================
	// JavaVM Job Queue Map
	// ======================================================================
	/**
	 * JavaVM job queue map.
	 */
	private static final Map<String,JMSJobQueue> queues
		= Collections.synchronizedMap(new HashMap<String,JMSJobQueue>());

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 */
	public JMSJobQueueFactory() {
		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @param nodeId
	 * @return
	 * @throws JyroException
	 */
	@Override
	public JobQueueImpl create(String nodeId) throws JyroException {
		JMSJobQueue queue = new JMSJobQueue(nodeId, con);
		queues.put(nodeId, queue);
		return queue;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 * @param nodeId
	 * @return
	 * @throws JyroException
	 */
	@Override
	public JobQueue lookup(String nodeId) throws JyroException {
		JMSJobQueue queue = queues.get(nodeId);
		if(queue == null){
			throw new JyroException("no such queue: " + nodeId);
		}
		return queue;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// JVMJobQueue:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 * @author torao
	 * @since 2011/07/24 Java SE 6
	 */
	private static class JMSJobQueue extends JobQueueImpl {

		// ======================================================================
		// Queue Session
		// ======================================================================
		/**
		 * JMS Queue session of this queue.
		 */
		private final QueueSession session;

		// ======================================================================
		// Queue
		// ======================================================================
		/**
		 * JMS Queue
		 */
		private final javax.jms.Queue queue;

		// ======================================================================
		// Queue Sender
		// ======================================================================
		/**
		 * JMS queue sender.
		 */
		private final QueueSender sender;

		// ======================================================================
		// Queue Receiver
		// ======================================================================
		/**
		 * JMS queue receiver.
		 */
		private final QueueReceiver receiver;

		// ======================================================================
		// Close Flag
		// ======================================================================
		/**
		 *
		 */
		private boolean closed = false;

		// ======================================================================
		// Constructor
		// ======================================================================
		/**
		 * @param id ID of this queue
		 * @param con JMS Queue Connection
		 * @throws JyroException fail to connect queue
		 */
		public JMSJobQueue(String id, QueueConnection con) throws JyroException {
			super(id);
			try {
				this.session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
				this.queue = session.createQueue(id);
				this.sender = session.createSender(queue);
				this.receiver = session.createReceiver(queue);
			} catch(JMSException ex){
				throw new JyroException(ex);
			}
			return;
		}

		// ======================================================================
		// Post Job
		// ======================================================================
		/**
		 * Post specified job to this queue.
		 */
		@Override
		public void post(Job job) throws JyroException {
			try {
				Message msg = session.createObjectMessage(job);
				sender.send(msg);
			} catch(JMSException ex){
				throw new JyroException(ex);
			}
			return;
		}

		// ======================================================================
		// Constructor
		// ======================================================================
		/**
		 */
		@Override
		public Job receive() throws JyroException {
			try {
				ObjectMessage msg = (ObjectMessage)receiver.receive();
				return (Job)msg.getObject();
			} catch(JMSException ex){
				throw new JyroException(ex);
			}
		}


		// ======================================================================
		// Close Queue
		// ======================================================================
		/**
		 * Close queue messaging and release resources.
		 *
		 * @throws JyroException if fail to close queue
		*/
		@Override
		public void close() throws JyroException{

			// set closed status first because normally exit should detect in receive()
			this.closed = true;

			// close sender
			try {
				sender.close();
			} catch(JMSException ex){
				logger.error("fail to close JMS queue sender, try to continue", ex);
			}

			// close receiver
			try {
				receiver.close();
			} catch(JMSException ex){
				logger.error("fail to close JMS queue receiver, try to continue", ex);
			}

			// close session
			try {
				session.close();
			} catch(JMSException ex){
				throw new JyroException("fail to close JMS session", ex);
			}

			super.close();
			return;
		}

	}

}
