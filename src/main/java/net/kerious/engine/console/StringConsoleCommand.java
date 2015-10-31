/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// StringConsoleCommand.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 21, 2013 at 8:25:42 PM
////////

package net.kerious.engine.console;

public class StringConsoleCommand extends ValueConsoleCommand<String> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public StringConsoleCommand(String name) {
		super(name);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	protected String parseValue(String input) throws Exception {
		return input;
	}

	@Override
	protected int compareValue(String value, String cmp) {
		return 0;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
