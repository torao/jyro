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

import java.util.*;
import java.util.concurrent.*;



// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// MemoryQueue: Memory Queue
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/**
 * Queue implementation for to use heap memory of Java VM.
 * <p>
 * @version $Revision:$
 * @author torao
 * @since 2011/07/06 Java SE 6
 */
public class MemoryQueue implements JobQueue{

	// ======================================================================
	// Queue
	// ======================================================================
	/**
	 * Queue map of current Java VM.
	*/
	private static final Map<String,BlockingQueue<Job>> QUEUES
		= new HashMap<String,BlockingQueue<Job>>();

	// ======================================================================
	// Name
	// ======================================================================
	/**
	 * Name of this queue.
	 */
	private final String name;

	// ======================================================================
	// Queue
	// ======================================================================
	/**
	 * Queue map of current Java VM.
	*/
	private final BlockingQueue<Job> queue;

	// ======================================================================
	// Constructor
	// ======================================================================
	/**
	 * @param name name of this queue.
	 */
	public MemoryQueue(String name) {
		this.name = name;
		synchronized(QUEUES){
			BlockingQueue<Job> queue = QUEUES.get(name);
			if(queue == null){
				queue = new LinkedBlockingQueue<Job>();
				QUEUES.put(name, queue);
			}
			this.queue = queue;
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
	public void send(Job job) {
		queue.offer(job);
		return;
	}

	// ======================================================================
	// Receive Job
	// ======================================================================
	/**
	 * Receive job from this queue.
	*/
	@Override
	public Job receive(){
		return queue.take();
	}

	// ======================================================================
	// Close Queue
	// ======================================================================
	/**
	 * Close queue messaging and release resources.
	*/
	@Override
	public void close() {
		return;
	}

}
