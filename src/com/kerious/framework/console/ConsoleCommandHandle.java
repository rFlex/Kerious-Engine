/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.framework.kcommand
// CommandHandler.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2012 at 3:34:09 PM
////////

package com.kerious.framework.console;

import com.kerious.framework.utils.EventListenerHolder;

public class ConsoleCommandHandle {

	////////////////////////
	// VARIABLES
	////////////////

	public final EventListenerHolder<ConsoleCommandChangeEvent> onChanged = new EventListenerHolder<ConsoleCommandChangeEvent>();
	private final String name;
	private String value;
	private boolean command;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ConsoleCommandHandle(String commandName, boolean isCommand) {
		this.name = commandName;
		this.command = isCommand;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final boolean isCommand() {
		return this.command;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final ConsoleCommandChangeEvent setValue(String value) {
		return this.setValue(value, null);
	}
	
	public final ConsoleCommandChangeEvent setValue(String value, Object sender) {
		String oldValue = this.value;
		
		this.value = value;
		
		ConsoleCommandChangeEvent commandEvent = new ConsoleCommandChangeEvent(this, oldValue, value);
		if (sender != null) {
			this.onChanged.call(sender, commandEvent);
		} else {
			this.onChanged.call(this, commandEvent);
		}
		
		if (commandEvent.isChangeCanceled() || this.command) {
			this.value = oldValue;
		}
		
		return commandEvent;
	}
	
	public final ConsoleCommandChangeEvent setValue(int value) {
		return this.setValue(Integer.toString(value));
	}
	
	public final ConsoleCommandChangeEvent setValue(float value) {
		return this.setValue(Float.toString(value));
	}
	
	public final int getValueAsInt() {
		return Console.getAsInt(this.value);
	}
	
	public final float getValueAsFloat() {
		return Console.getAsFloat(this.value);
	}
	
	public final String getValueAsString() {
		return Console.getAsString(this.value);
	}
	
}
