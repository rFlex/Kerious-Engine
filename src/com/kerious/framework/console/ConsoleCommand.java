/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.client.kcommand
// Command.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2012 at 6:28:47 PM
////////

package com.kerious.framework.console;

public abstract class ConsoleCommand extends BaseConsoleCommand {

	////////////////////////
	// VARIABLES
	////////////////
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ConsoleCommand(String name, boolean isConfig) {
		super(name, isConfig);
	}

	////////////////////////
	// METHODS
	////////////////
	
	protected abstract void onCommandChanged(ConsoleCommandChangeEvent arg) throws Exception;

	@Override
	public void onFired(Object sender, ConsoleCommandChangeEvent arg) {
		try {
			this.onCommandChanged(arg);
		} catch (Exception e) {
			arg.cancelChange("Code crashed with error: " + e.getMessage());
		}
	}
}
