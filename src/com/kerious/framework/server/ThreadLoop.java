/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.server
// ThreadLoop.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 24, 2012 at 4:55:32 PM
////////

package com.kerious.framework.server;

import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.utils.EventListenerHolder;
import com.kerious.framework.utils.StopWatch;

public class ThreadLoop implements Runnable {

	////////////////////////
	// VARIABLES
	////////////////

	public EventListenerHolder<ThreadLoop> onThreadStarted = new EventListenerHolder<ThreadLoop>();
	public EventListenerHolder<ThreadLoop> onThreadEnded = new EventListenerHolder<ThreadLoop>();
	final private String threadName; 
	private Thread thread;
	private LoopIteration loop;
	private StopWatch deltaCounter;
	private boolean started;
	private float interval;

	////////////////////////
	// VARIABLES
	////////////////
	
	public static interface LoopIteration {
		void run(float delta);
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ThreadLoop(String threadName, float intervalBetweenLoop, LoopIteration loop) {
		this.threadName = threadName;
		this.setInterval(intervalBetweenLoop);
		this.setLoop(loop);
		
		this.deltaCounter = new StopWatch();
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void start() {
		if (this.thread != null) {
			throw new KeriousException("The ThreadLoop is already started");
		}
		
		this.started = true;
		this.thread = new Thread(this, this.threadName);
		this.thread.start();
	}
	
	public void stop() {
		if (this.thread == null) {
			throw new KeriousException("The ThreadLoop is not started");
		}
		
		this.started = false;
		
		try {
			this.thread.join();
		} catch (InterruptedException e) {
		}
		
		this.thread = null;
	}
	
	@Override
	public void run() {
		this.onThreadStarted.call(this, this);
		
		final StopWatch sw = new StopWatch();
		long toSleepTime = 0;
		
		try {
			while (this.started) {
				//final long realSleepTime = (long)(sw.secondCurrent() * 1000f);
				final float delta = this.deltaCounter.secondCurrent();
				
				this.deltaCounter.start();
				sw.start();
				
				this.loop.run(delta);
				
				final float timeTakenForLoop = sw.secondCurrent();
				toSleepTime = (long)((this.interval - timeTakenForLoop) * 1000f);
				
//				sw.start();
				if (toSleepTime > 1) {
					Thread.sleep(toSleepTime);
				}
			}
		} catch (Exception e) {
			System.err.println("Loop exited with message: " + e.getMessage());
			e.printStackTrace();
		}
		
		this.onThreadEnded.call(this, this);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final float getInterval() {
		return interval;
	}

	public final void setInterval(float interval) {
		this.interval = interval;
	}
	
	public final boolean isStarted() {
		return this.started;
	}
	
	public final void setLoop(LoopIteration loop) {
		if (loop == null) {
			throw new KeriousException("Loop cannot be null");
		}
		
		this.loop = loop;
	}
}
