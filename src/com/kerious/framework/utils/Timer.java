/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.framework.utils
// Timer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 18, 2012 at 4:40:29 PM
////////

package com.kerious.framework.utils;

import com.kerious.framework.utils.StopWatch.TimeProvider;

public class Timer {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private StopWatch sw;
	private float time;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Timer(TimeProvider timeProvider) {
		this.sw = new StopWatch(timeProvider);
	}
	
	public Timer() {
		this.sw = new StopWatch();
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void start(float time) {
		this.time = time;
		this.sw.start();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final boolean hasElapsed() {
		return this.sw.secondCurrent() > time;
	}
	
	public final StopWatch getStopWatch() {
		return this.sw;
	}
}
