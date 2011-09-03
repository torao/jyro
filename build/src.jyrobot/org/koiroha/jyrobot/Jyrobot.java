/* **************************************************************************
 * Copyright (C) 2011 BJoRFUAN. All Rights Reserved
 * **************************************************************************
 * This module, contains source code, binary and documentation, is in the
 * Apache License Ver. 2.0, and comes with NO WARRANTY.
 *
 *                                           takami torao <koiroha@gmail.com>
 *                                                   http://www.bjorfuan.com/
 */
package org.koiroha.jyrobot;

import java.net.URI;

import org.koiroha.jyrobot.Session.Request;


// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// Jyrobot:
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 *
 * @version $Revision:$
 * @author Takami Torao
 * @since 2011/08/28 Java SE 6
 */
public class Jyrobot {

	// ======================================================================
	// Serial Version
	// ======================================================================
	/**
	 * Serial version of this class.
	 */
	private static final long serialVersionUID = 1L;

	// ======================================================================
	// コンストラクタ
	// ======================================================================
	/**
	 * コンストラクタは何も行いません。
	 */
	public Jyrobot() {
		return;
	}

	public static void main(String[] args) throws Exception{
		final Scheduler scheduler = new Scheduler();
		scheduler.resetAllSessions();
		scheduler.put(new Request(URI.create("http://www.yahoo.co.jp"), null));
		Runnable r = new Runnable(){
			@Override
			public void run(){
				try {
					Session session = scheduler.next();
					System.out.println(session);
					for(Session.Request r=session.take(); r != null; r=session.take()){
						System.out.println(r);
					}
					session.close();
				} catch(InterruptedException ex){
					System.err.println(ex);
				}
				return;
			}
		};
		Thread[] t = new Thread[3];
		for(int i=0; i<t.length; i++){
			t[i] = new Thread(r);
			t[i].start();
		}
		return;
	}

}
