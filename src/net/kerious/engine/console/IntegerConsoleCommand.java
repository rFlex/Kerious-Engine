/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// IntegerConsoleCommand.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 3, 2013 at 4:24:53 PM
////////

package net.kerious.engine.console;

public class IntegerConsoleCommand extends ValueConsoleCommand<Integer> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public IntegerConsoleCommand(String name) {
		super(name);
	}
	
	public IntegerConsoleCommand(String name, Integer minValue, Integer maxValue) {
		super(name);
		this.setMinValue(minValue);
		this.setMaxValue(maxValue);
	}
	
	public IntegerConsoleCommand(String name, ValueConsoleCommandListener<Integer> listener) {
		super(name);
		this.setListener(listener);
	}

	public IntegerConsoleCommand(String name, ValueConsoleCommandListener<Integer> listener, Integer minValue, Integer maxValue) {
		super(name);
		this.setListener(listener);
		this.setMinValue(minValue);
		this.setMaxValue(maxValue);
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	@Override
	protected Integer parseValue(String input) throws Exception {
		return Integer.parseInt(input);
	}

	@Override
	protected int compareValue(Integer value, Integer cmp) {
		return value.compareTo(cmp);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
