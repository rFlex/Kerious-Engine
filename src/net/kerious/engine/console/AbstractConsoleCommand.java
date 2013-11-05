/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// AbstractConsoleCommand.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 3, 2013 at 4:16:05 PM
////////

package net.kerious.engine.console;

public abstract class AbstractConsoleCommand implements ConsoleCommand {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private String name;
	private Console console;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public AbstractConsoleCommand(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		
		this.name = name;
	}

	////////////////////////
	// METHODS
	////////////////
	
	protected void printToConsole(String message) {
		if (this.getConsole() != null) {
			this.getConsole().print(message);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setConsole(Console console) {
		this.console = console;
	}

	@Override
	public Console getConsole() {
		return this.console;
	}
}
