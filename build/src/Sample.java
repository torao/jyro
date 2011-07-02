import javax.jms.*;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;

/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */

// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Sample: 
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * 
 * <p>
 * @version $Revision:$ $Date:$
 * @author torao
 * @since 2011/07/02 Java SE 6
 */
public class Sample {
	private QueueConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://thyme:61616");

	private void start(){
		for(int i=0; i<10; i++){
			Receiver r = new Receiver(i);
			synchronized(r){
				r.start();
				try { 
					r.wait();
				} catch(InterruptedException ex){
					ex.printStackTrace();
				}
			}
		}

		try {
			QueueConnection connection = factory.createQueueConnection();
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("sample");
			QueueSender sender = session.createSender(queue);
			connection.start();
			for(int i=0; i<10; i++){
				TextMessage msg = session.createTextMessage("hello, world(" + i + ")");
				sender.send(msg);
			}
			sender.close();
			session.close();
			connection.close();
		} catch(JMSException ex){
			ex.printStackTrace();
		}
		
		return;
	}
	public static void main(String[] args){
		new Sample().start();
		return;
	}

	private class Receiver extends Thread {
		private final int num;
		public Receiver(int num){ this.num = num; }
		public void run(){
			try {
				QueueConnection connection = factory.createQueueConnection();
				QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
				Queue queue = session.createQueue("sample");
				QueueReceiver receiver = session.createReceiver(queue);
				connection.start();
				System.out.printf("[%d] start listening%n", num);
				synchronized(this){
					this.notify();
				}
				TextMessage msg = (TextMessage)receiver.receive();
				System.out.printf("[%d] %s%n", num, msg.getText());
				receiver.close();
				session.close();
				connection.close();
			} catch(JMSException ex){
				ex.printStackTrace();
			}
			return;
		}
	}

}
