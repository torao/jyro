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

import javax.jms.*;

import org.apache.log4j.Logger;
import org.koiroha.jyro.*;

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JMSBus:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * <p>
 * @author torao
 * @since 2011/07/24 Java SE 6
 */
public class JMSBus extends Bus {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output for this class.
	 */
	private static final Logger logger = Logger.getLogger(JMSBus.class);

	private static String ATTR_MESSAGE = "jms.bus.message";
	private static String ATTR_QUEUE = "jms.bus.queue";

	// ======================================================================
	// JMS Connection
	// ======================================================================
	/**
	 * JSM connection.
	 */
	private QueueConnection connection = null;

	// ======================================================================
	// JavaVM Job Queue Map
	// ======================================================================
	/**
	 * JavaVM job queue map.
	 */
	private static final Map<String,Queue> queues
		= Collections.synchronizedMap(new HashMap<String,Queue>());

	// ======================================================================
	//
	// ======================================================================
	/**
	 *
	 */
	public JMSBus() {
		return;
	}

	// ======================================================================
	// Send Job
	// ======================================================================
	/**
	 * Send specified job on this bus.
	 *
	 * @param job job to post any node
	 * @throws JyroException if fail to post job
	 */
	@Override
	public void send(Job job) throws JobRoutingException, JyroException{
		Queue queue = getQueue(job.getFunction());
		queue.send(job);
		return;
	}

	// ======================================================================
	// Receive Job
	// ======================================================================
	/**
	 * Receive job.
	 *
	 * @param func function name to receive job
	 * @throws JyroException if fail to post job
	 */
	@Override
	public Job receive(String func) throws JyroException, InterruptedException{
		Queue queue = getQueue(func);
		return queue.receive();
	}

	// ======================================================================
	// Callback Result
	// ======================================================================
	/**
	 * Callback execution result from worker.
	 *
	 * @param result result of job execution
	 */
	@Override
	public void callback(Job.Result result) throws JyroException{
		Job job = result.getJob();
		Queue queue = (Queue)job.getAttribute(ATTR_QUEUE);
		queue.reply(result);
		return;
	}

	// ======================================================================
	//
	// ======================================================================
	/**
	 */
	@Override
	public void close(){
		synchronized(queues){
			for(Queue q: queues.values()){
				q.close();
			}
		}
		return;
	}

	// ======================================================================
	// Retrieve Queue
	// ======================================================================
	/**
	 * Retrieve queue for specified worker interface.
	 *
	 * @param func function of queue
	 * @throws JyroException if fail to create new queue
	 */
	private Queue getQueue(String func) throws JyroException {
		Queue queue = null;
		synchronized(queues){
			queue = queues.get(func);
			if(queue == null){
				String id = "jyro:" + func;
				queue = new Queue(id);
				queues.put(func, queue);
			}
		}
		return queue;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Queue:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 * @author torao
	 * @since 2011/07/24 Java SE 6
	 */
	private class Queue {

		// ==================================================================
		// Session
		// ==================================================================
		/**
		 * JMS session of this queue.
		 */
		private final QueueSession session;

		// ==================================================================
		// Queue
		// ==================================================================
		/**
		 * JMS Queue
		 */
		private final javax.jms.Queue queue;

		// ==================================================================
		// Queue
		// ==================================================================
		/**
		 * JMS Queue
		 */
		private javax.jms.Queue reply = null;

		// ==================================================================
		// Reply Consumer
		// ==================================================================
		/**
		 * Replay consumer.
		 */
		private MessageConsumer replyConsumer = null;

		// ==================================================================
		// Queue Sender
		// ==================================================================
		/**
		 * JMS queue sender.
		 */
		private volatile QueueSender sender = null;

		// ==================================================================
		// Queue Receiver
		// ==================================================================
		/**
		 * JMS queue receiver.
		 */
		private volatile QueueReceiver receiver = null;

		// ==================================================================
		// Lock
		// ==================================================================
		/**
		 * Lock to create sender and receiver.
		 */
		private final Object[] lock = new Object[2];

		// ==================================================================
		// Close Flag
		// ==================================================================
		/**
		 *
		 */
		private volatile boolean closed = false;

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * @param id ID of this queue
		 * @param con JMS Queue Connection
		 * @throws JyroException fail to connect queue
		 */
		public Queue(String id) throws JyroException {
			try {
				this.session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
				this.queue = session.createQueue(id);
			} catch(JMSException ex){
				throw new JyroException(ex);
			}
			return;
		}

		// ==================================================================
		// Send Job
		// ==================================================================
		/**
		 * Send specified job to this queue.
		 *
		 * @param job job to post
		 * @throws JyroException if fail to send job
		 */
		public void send(Job job) throws JyroException {
			try {
				if(closed){
					throw new JyroException("closed");
				}

				// create queue sender if not started
				synchronized(lock[0]){
					if(sender == null){
						this.sender = session.createSender(queue);
						this.reply = session.createTemporaryQueue();
						this.replyConsumer = session.createConsumer(reply);
						this.replyConsumer.setMessageListener(new ReplyListener());
					}
				}

				// create message
				ObjectMessage msg = session.createObjectMessage(job);
				msg.setJMSReplyTo(reply);

				// send message
				sender.send(msg);

				// write send log
				if(logger.isDebugEnabled()){
					logger.debug("[SEND] Time:       " + System.currentTimeMillis() + " ms");
					logger.debug("[SEND] Message ID: " + msg.getJMSMessageID());
					logger.debug("[SEND] Correl. ID: " + msg.getJMSCorrelationID());
					logger.debug("[SEND] Reply to:   " + msg.getJMSReplyTo());
					logger.debug("[SEND] Contents:   " + job);
				}
			} catch(JMSException ex){
				throw new JyroException(ex);
			}
			return;
		}

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 */
		public Job receive() throws JyroException {
			try {
				if(closed){
					throw new JyroException("closed");
				}

				// create queue receiver if not started
				synchronized(lock[1]){
					if(receiver == null){
						this.receiver = session.createReceiver(queue);
					}
				}

				// receive job
				ObjectMessage msg = (ObjectMessage)receiver.receive();
				Job job = (Job)msg.getObject();

				// write receive log
				if(logger.isDebugEnabled()){
					logger.debug("[RECV] Time:       " + System.currentTimeMillis() + " ms");
					logger.debug("[RECV] Message ID: " + msg.getJMSMessageID());
					logger.debug("[RECV] Correl. ID: " + msg.getJMSCorrelationID());
					logger.debug("[RECV] Reply to:   " + msg.getJMSReplyTo());
					logger.debug("[RECV] Contents:   " + job);
				}

				job.setAttribute(ATTR_MESSAGE, msg);
				job.setAttribute(ATTR_QUEUE, this);

				return job;
			} catch(JMSException ex){
				throw new JyroException(ex);
			}
		}

		// ==================================================================
		// Send Job
		// ==================================================================
		/**
		 * Send specified job to this queue.
		 *
		 * @param job job to post
		 * @throws JyroException if fail to send job
		 */
		public void reply(Job.Result result) throws JyroException {
			Job job = result.getJob();
			Message msg = (Message)job.getAttribute(ATTR_MESSAGE);
			try {
				Destination d = msg.getJMSReplyTo();
				MessageProducer producer = session.createProducer(d);

				// create reply message
				ObjectMessage reply = session.createObjectMessage(result);

				// send message
				producer.send(reply);
			} catch(JMSException ex){
				throw new JyroException(ex);
			}
			return;
		}

		// ==================================================================
		// Close Queue
		// ==================================================================
		/**
		 * Close queue messaging and release resources.
		*/
		public void close() {

			// set closed status first because normally exit should detect in receive()
			this.closed = true;

			// close sender
			try {
				if(sender != null){
					sender.close();
				}
			} catch(JMSException ex){
				logger.error("fail to close JMS queue sender, try to continue", ex);
			}

			// close receiver
			try {
				if(receiver != null){
					receiver.close();
				}
			} catch(JMSException ex){
				logger.error("fail to close JMS queue receiver, try to continue", ex);
			}

			// close session
			try {
				session.close();
			} catch(JMSException ex){
				logger.error("fail to close JMS session", ex);
			}

			return;
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Queue:
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 *
	 * @author torao
	 * @since 2011/07/24 Java SE 6
	 */
	private class ReplyListener implements MessageListener {

		// ==================================================================
		// Constructor
		// ==================================================================
		/**
		 * Default constructor do nothing.
		*/
		public ReplyListener(){
			return;
		}

		// ==================================================================
		// Message Callback
		// ==================================================================
		/**
		 * Callback to receive reply message.
		 *
		 * @param msg received message
		*/
		@Override
		public void onMessage(Message msg) {
			try {
				if(msg instanceof ObjectMessage){
					ObjectMessage omsg = (ObjectMessage)msg;
					Job.Result result = (Job.Result)omsg.getObject();
					result.callback();
				}
			} catch(JMSException ex){
				logger.fatal("", ex);
			}
			return;
		}

	}

}
