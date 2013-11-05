/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// Console.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 6:26:19 PM
////////

package net.kerious.engine.console;

import java.util.HashMap;
import java.util.Map;

public class Console {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Map<String, ConsoleCommand> commands;
	private ConsolePrinter printer;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Console() {
		this.commands = new HashMap<String, ConsoleCommand>();
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void registerCommand(ConsoleCommand consoleCommand) {
		if (consoleCommand == null) {
			throw new NullPointerException("consoleCommand");
		}
		consoleCommand.setConsole(this);
		
		this.commands.put(consoleCommand.getName(), consoleCommand);
	}
	
	public ConsoleCommand getCommand(String commandName) {
		return this.commands.get(commandName);
	}
	
	public void enterCommand(String command) {
		command = command.trim();
		
		int spacePos = command.indexOf(' ');
		String commandName = command;
		String input = "";
		
		if (spacePos >= 0) {
			commandName = command.substring(0, spacePos);
			if (spacePos + 1 < command.length()) {
				input = command.substring(spacePos + 1, command.length()).trim();
			}
		}
		ConsoleCommand consoleCommand = this.getCommand(commandName);
		
		if (consoleCommand != null) {
			consoleCommand.handleInput(input);
		} else {
			this.print("Unknwon command [" + commandName + "]");
		}
	}
	
	public void print(String string) {
		if (this.getPrinter() != null) {
			this.getPrinter().print(string);
		} else {
			System.out.println(string);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public ConsolePrinter getPrinter() {
		return printer;
	}

	public void setPrinter(ConsolePrinter printer) {
		this.printer = printer;
	}
}
