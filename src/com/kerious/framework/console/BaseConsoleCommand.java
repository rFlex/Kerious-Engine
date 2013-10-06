/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.console
// BaseConsoleCommand.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 2, 2012 at 7:49:28 PM
////////

package com.kerious.framework.console;

import com.kerious.framework.utils.IEventListener;

public abstract class BaseConsoleCommand implements IEventListener<ConsoleCommandChangeEvent> {

	////////////////////////
	// VARIABLES
	////////////////

	public final String name;
	public final boolean isConfig;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public BaseConsoleCommand(String name, boolean isConfig) {
		this.name = name;
		this.isConfig = isConfig;
	}

	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
