/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.utils
// TimeDiff.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 7, 2012 at 5:25:46 PM
////////

package com.kerious.framework.utils;

public class TimeDiff {

	////////////////////////
	// VARIABLES
	////////////////
	
	public final static float DAY = TimeDiff.HOUR * 24;
	public final static float HOUR = TimeDiff.MINUT * 60;
	public final static float MINUT = 60;
	public final static float SECOND = 1;
	public float totalSeconds;
	public int days;
	public int hours;
	public int minuts;
	public int seconds;
	public int milliseconds;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TimeDiff() {
		
	}
	
	public TimeDiff(float timeDiff) {
		this.create(timeDiff);
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void reset() {
		this.totalSeconds = 0;
		this.hours = 0;
		this.minuts = 0;
		this.seconds = 0;
		this.milliseconds = 0;
	}
	
	public void create(float timeDiff) {
		this.reset();
		
		this.totalSeconds = timeDiff;
		
		while (timeDiff >= TimeDiff.DAY) {
			this.days++;
			timeDiff -= TimeDiff.DAY;
		}
		while (timeDiff >= TimeDiff.HOUR) {
			this.hours++;
			timeDiff -= TimeDiff.HOUR;
		}
		while (timeDiff >= TimeDiff.MINUT) {
			this.minuts++;
			timeDiff -= TimeDiff.MINUT;
		}
		this.seconds = (int)timeDiff;
		this.milliseconds = (int)(timeDiff * 1000f) - this.seconds;;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
