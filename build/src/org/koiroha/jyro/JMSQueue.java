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
// JMSQueue: JMS Queue
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Queue implementation by JMS.
 *
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/04 Java SE 6
 */
public class JMSQueue {

	// ======================================================================
	// Log Output
	// ======================================================================
	/**
	 * Log output of this class.
	 */
	private static final Logger logger = Logger.getLogger(JMSQueue.class);

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
	// Constructor
	// ======================================================================
	/**
	 * @param con JMS Queue Connection
	 */
	public JMSQueue(QueueConnection con, String name) {
		this.session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		this.queue = session.createQueue(name);
		this.sender = session.createSender(queue);
		this.receiver = session.createReceiver(queue);
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 */
	public void close() {
		receiver.close();
		sender.close();
		session.close();
		return;
	}

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 */
	public void send() {
		receiver.close();
		sender.close();
		session.close();
		return;
	}

}
