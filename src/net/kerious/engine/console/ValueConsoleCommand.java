/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// ValueConsoleCommand.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 3, 2013 at 4:15:56 PM
////////

package net.kerious.engine.console;

public abstract class ValueConsoleCommand<T> extends AbstractConsoleCommand {

	////////////////////////
	// VARIABLES
	////////////////
	
	private T value;
	private T minValue;
	private T maxValue;
	private ValueConsoleCommandListener<T> listener;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ValueConsoleCommand(String name) {
		super(name);
	}

	////////////////////////
	// METHODS
	////////////////
	
	protected abstract T parseValue(String input) throws Exception;
	protected abstract int compareValue(T value, T cmp);
	
	@Override
	public void handleInput(String input) {
		if (!input.isEmpty()) {
			try {
				T value = this.parseValue(input);
				this.setValue(value);
			} catch (Exception e) {
				this.printToConsole("Error while trying to set " + this.getName() + ": " + e.getMessage());
			}
		} else {
			this.printToConsole(this.getName() + " is [" + this.valueToString() + "]");
		}
	}
	
	private T getTruncatedValue(T value) {
		final T minValue = this.getMinValue();
		
		if (this.compareValue(value, minValue) > 0) {
			return minValue;
		}
		
		final T maxValue = this.getMaxValue();
		
		if (this.compareValue(value, maxValue) < 0) {
			return this.maxValue;
		}
		
		return value;
	}
	
	private void truncateValue() {
		
	}
	
	private String valueToString() {
		T value = this.getValue();
		
		return value != null ? value.toString() : "null";
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public T getValue() {
		return this.value;
	}
	
	public void setValue(T value) {
		this.value = this.getTruncatedValue(value);
		
		if (this.getListener() != null) {
			this.getListener().onValueChanged(this);
		}
	}

	public ValueConsoleCommandListener<T> getListener() {
		return listener;
	}

	public void setListener(ValueConsoleCommandListener<T> listener) {
		this.listener = listener;
	}

	public T getMinValue() {
		return minValue;
	}

	public void setMinValue(T minValue) {
		this.minValue = minValue;
		
		this.truncateValue();
	}

	public T getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(T maxValue) {
		this.maxValue = maxValue;
		
		this.truncateValue();
	}
}
