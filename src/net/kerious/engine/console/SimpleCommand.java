/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// SimpleCommand.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 23, 2013 at 3:06:25 PM
////////

package net.kerious.engine.console;

public abstract class SimpleCommand extends AbstractConsoleCommand {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public SimpleCommand(String name) {
		super(name);
	}

	////////////////////////
	// METHODS
	////////////////
	

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public boolean isValueCommand() {
		return false;
	}

	@Override
	public String getValueAsString() {
		return null;
	}
}
