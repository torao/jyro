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
package org.koiroha.jyro;

import javax.jms.*;

import org.apache.log4j.Logger;




// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// JMSJobQueue: JMS Job Queue
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Job queue implementation by JMS.
 *
 * @version $Revision:$
 * @author torao
 * @since 2011/07/04 Java SE 6
 */
public class JMSJobQueue extends JobQueueImpl {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JMSJobQueue.class);

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
