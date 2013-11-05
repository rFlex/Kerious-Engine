/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// IConsoleCommand.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 6:27:13 PM
////////

package net.kerious.engine.console;

public interface ConsoleCommand {
	
	void handleInput(String input);
	String getName();
	
	void setConsole(Console console);
	Console getConsole();

}
