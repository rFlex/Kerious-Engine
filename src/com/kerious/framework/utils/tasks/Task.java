package com.kerious.framework.utils.tasks;

import com.kerious.framework.utils.StopWatch;

public abstract class Task implements Runnable {

	////////////////////////
	// VARIABLES
	////////////////

	protected StopWatch stopWatch;
	protected boolean elapsed;
	protected float timeout;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Task() {
		this(0);
	}
	
	public Task(float timeout) {
		this.stopWatch = new StopWatch();
		this.timeout = timeout;
	}
	
	////////////////////////
	// METHODS
	////////////////

	protected void update() {
		if (stopWatch != null) {
			if (stopWatch.secondCurrent() > timeout) {
				this.run();
				this.elapsed = true;
			}
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean hasElapsed() {
		return elapsed;
	}
}
