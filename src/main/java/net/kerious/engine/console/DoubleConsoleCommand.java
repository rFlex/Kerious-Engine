/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// DoubleConsoleCommand.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 3, 2013 at 4:26:04 PM
////////

package net.kerious.engine.console;

public class DoubleConsoleCommand extends ValueConsoleCommand<Double> {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public DoubleConsoleCommand(String name) {
		super(name);
	}
	
	public DoubleConsoleCommand(String name, Double minValue, Double maxValue) {
		super(name);
		this.setMinValue(minValue);
		this.setMaxValue(maxValue);
	}
	
	public DoubleConsoleCommand(String name, ValueConsoleCommandListener<Double> listener) {
		super(name);
		this.setListener(listener);
	}

	public DoubleConsoleCommand(String name, ValueConsoleCommandListener<Double> listener, Double minValue, Double maxValue) {
		super(name);
		this.setListener(listener);
		this.setMinValue(minValue);
		this.setMaxValue(maxValue);
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	@Override
	protected Double parseValue(String input) throws Exception {
		return Double.parseDouble(input);
	}

	@Override
	protected int compareValue(Double value, Double cmp) {
		return Double.compare(value, cmp);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
