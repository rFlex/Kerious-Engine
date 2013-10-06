/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.framework.kcommand
// CommandEventArgs.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2012 at 3:40:21 PM
////////

package com.kerious.framework.console;

public class ConsoleCommandChangeEvent {

	////////////////////////
	// VARIABLES
	////////////////

	public final ConsoleCommandHandle command;
	public final String oldValue;
	public String newValue;
	private boolean changeCanceled;
	private boolean success;
	private String message;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ConsoleCommandChangeEvent(ConsoleCommandHandle command, String oldValue, String newValue) {
		this.command = command;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.success = true;
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	public void cancelChangeWithoutFail(String reason) {
		this.changeCanceled = true;
		this.success = true;
		this.message = reason;
	}
	
	public void cancelChange(String reason) {
		this.changeCanceled = true;
		this.success = false;
		this.message = reason;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public boolean isChangeCanceled() {
		return this.changeCanceled;
	}
	
	public boolean success() {
		return this.success;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
