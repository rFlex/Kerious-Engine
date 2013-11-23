/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.console
// Console.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 6:26:19 PM
////////

package net.kerious.engine.console;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Console {

	////////////////////////
	// VARIABLES
	////////////////
	
	private ObjectMap<String, ConsoleCommand> commands;
	private Array<String> tmpArray;
	private StringBuilder sb;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Console() {
		this.commands = new ObjectMap<String, ConsoleCommand>();
		this.tmpArray = new Array<String>(true, 10, String.class);
		this.sb = new StringBuilder();
		
		this.registerBaseCommands();
	}

	////////////////////////
	// METHODS
	////////////////
	
	protected void registerBaseCommands() {
		this.registerCommand(new SimpleCommand(Commands.Print) {
			public void handle(String... parameters) {
				for (int i = 0, length = parameters.length; i < length; i++) {
					System.out.println("Console: " + parameters[i]);
				}
			}
		});
		this.registerCommand(new SimpleCommand(Commands.PrintError) {
			public void handle(String... parameters) {
				for (int i = 0, length = parameters.length; i < length; i++) {
					System.err.println("Console error: " + parameters[i]);
				}
			}
		});
		this.registerCommand(new SimpleCommand(Commands.RemoteInformation) {
			public void handle(String... parameters) {
				if (parameters.length > 1) {
					final String info = parameters[0];
					
					for (int i = 1, length = parameters.length; i < length; i++) {
						if (InfoType.Error.equals(info)) {
							System.err.println("Server error: " + parameters[i]);
						} else if (InfoType.Info.equals(info)) {
							System.out.println("Server information: " + parameters[i]);
						} else if (InfoType.Warning.equals(info)) {
							System.out.println("Server warning: " + parameters[i]);
						} else {
							System.out.println("Server " + info + ": " + parameters[i]);
						}
					}
				}
			}
		});
	}
	
	public boolean unregisterCommand(ConsoleCommand consoleCommand) {
		if (consoleCommand == null) {
			throw new IllegalArgumentException("consoleCommand may not be null");
		}

		consoleCommand.setConsole(this);
		return this.commands.remove(consoleCommand.getName()) != null;
	}
	
	public void registerCommand(ConsoleCommand consoleCommand) {
		if (consoleCommand == null) {
			throw new IllegalArgumentException("consoleCommand may not be null");
		}
		consoleCommand.setConsole(this);
		
		this.commands.put(consoleCommand.getName(), consoleCommand);
	}
	
	public ConsoleCommand getCommand(String commandName) {
		return this.commands.get(commandName);
	}
	
	/**
	 * Parse the input and retrieve the arguments
	 * @param input
	 * @return
	 */
	final private String[] getParameters(String input) {
		this.tmpArray.clear();
		this.sb.setLength(0);

		boolean parsingArgument = false;
		boolean inQuote = false;
		boolean shouldIgnoreInterpretation = false;
		for (int i = 0, length = input.length(); i < length; i++) {
			char c = input.charAt(i);
			
			if (parsingArgument) {
				if (shouldIgnoreInterpretation) {
					this.sb.append(c);
				} else {
					if (c == '"') {
						// If we were in quote, we finished the argument
						// Otherwise, we start another one
						if (inQuote) {
							parsingArgument = false;
							inQuote = false;
						} else {
							inQuote = true;
						}
						
						this.tmpArray.add(this.sb.toString());
						this.sb.setLength(0);							
					} else if (c == ' ') {
						if (inQuote) {
							this.sb.append(c);
						} else {
							this.tmpArray.add(this.sb.toString());
							this.sb.setLength(0);							
							parsingArgument = false;
						}
					} else if (c == '\\') {
						shouldIgnoreInterpretation = true;
					} else {
						this.sb.append(c);
					}
				}
			} else {
				if (c == '"') {
					inQuote = true;
					parsingArgument = true;
				} else if (c == ' ') {
					
				} else if (c >= '!' && c <= '~') {
					parsingArgument = true;
					this.sb.append(c);
				}
			}
		}
		
		if (parsingArgument) {
			String lastArgument = this.sb.toString();
			if (!lastArgument.isEmpty()) {
				this.tmpArray.add(lastArgument);
			}
		}
		
		String[] array = new String[this.tmpArray.size];
		String[] currentElements = this.tmpArray.items;
		for (int i = 0, length = this.tmpArray.size; i < length; i++) {
			array[i] = currentElements[i];
		}
		
		return array;
	}
	
	/**
	 * Process the command using a complete string (typically from
	 * a user input). The string will be parsed then processCommand
	 * will be called 
	 * @param command
	 */
	public void processCommandString(String command) {
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
		
		String[] parameters = this.getParameters(input);

		this.processCommand(commandName, parameters);
	}
	
	/**
	 * Process the command commandName using the parameters
	 * @param commandName
	 * @param parameters
	 */
	public void processCommand(String commandName, String ... parameters) {
		ConsoleCommand consoleCommand = this.getCommand(commandName);
		
		if (consoleCommand != null) {
			try {
				consoleCommand.handle(parameters);
			} catch (Throwable e) {
				this.printError("Command failed: " + e.getMessage());
			}
			
		} else {
			// Prevent infinite loop if the user removed the print command
			if (!Commands.PrintError.equals(commandName)) {
				this.printError("Unknown command [" + commandName + "]");
			}
		}
	}
	
	public void print(String string) {
		this.processCommand(Commands.Print, string);
	}
	
	public void printError(String string) {
		this.processCommand(Commands.PrintError, string);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public ObjectMap<String, ConsoleCommand> getCommands() {
		return this.commands;
	}
}
