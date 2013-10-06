/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.framework.console
// Console.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2012 at 9:23:08 PM
////////

package com.kerious.framework.console;

import java.util.HashMap;
import java.util.Map;

public abstract class Console {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Map<String, ConsoleCommandHandle> _commands;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Console() {
		this._commands = new HashMap<String, ConsoleCommandHandle>();
	}
	
	////////////////////////
	// METHODS
	////////////////

	public abstract void printMessage(String message);
	
	public void registerCommand(BaseConsoleCommand command) {
		final ConsoleCommandHandle commandHandle = new ConsoleCommandHandle(command.name, !command.isConfig);
		
		commandHandle.onChanged.addListener(command);
		
		this._commands.put(command.name, commandHandle);
	}
	
	public boolean unregisterCommand(BaseConsoleCommand command) {
		return this.unregisterCommand(command.name);
	}
	
	public boolean unregisterCommand(String commandName) {
		return this._commands.remove(commandName) != null;
	}
	
	public ConsoleCommandChangeEvent enterCommand(String command) {
		return this.enterCommand(command, null);
	}
	
	public ConsoleCommandChangeEvent enterCommand(String command, Object sender) {
		final int whiteSpaceIndex = command.indexOf(" ");
		if (whiteSpaceIndex > 0 && whiteSpaceIndex + 1 < command.length()) {
			final String commandName = command.substring(0, whiteSpaceIndex);
			final String commandContent = command.substring(whiteSpaceIndex + 1, command.length());
			
			return this.setCommand(commandName, commandContent, sender);
		} else {
			return this.setCommand(command, sender);
		}
	}

	public ConsoleCommandChangeEvent setCommand(String commandName) {
		return this.setCommand(commandName, null);
	}
	
	public ConsoleCommandChangeEvent setCommand(String commandName, Object sender) {
		return this.setCommand(commandName, "", sender);
	}
	
	public ConsoleCommandChangeEvent setCommand(String commandName, float value) {
		return this.setCommand(commandName, Float.toString(value));
	}
	
	public ConsoleCommandChangeEvent setCommand(String commandName, int value) {
		return this.setCommand(commandName, Integer.toString(value));
	}
	
	public ConsoleCommandChangeEvent setCommand(String commandName, String commandContent) {
		return this.setCommand(commandName, commandContent, null);
	}
	
	public ConsoleCommandChangeEvent setCommand(String commandName, String commandContent, Object sender) {
		ConsoleCommandHandle commandHandle = this._commands.get(commandName);
		
		ConsoleCommandChangeEvent changeEvent = null;
		
		if (commandHandle != null) {
			if (!commandHandle.isCommand() && (commandContent == null || commandContent.isEmpty())) {
				final String currentValue = commandHandle.getValueAsString();
				changeEvent = new ConsoleCommandChangeEvent(commandHandle, currentValue, currentValue);
				changeEvent.cancelChangeWithoutFail(commandName + " is " + currentValue);
			} else {
				changeEvent = commandHandle.setValue(commandContent, sender);
			}
		} else {
			changeEvent = new ConsoleCommandChangeEvent(null, "", commandContent);
			changeEvent.cancelChange("Unknown command " + commandName + ".");
		}
		
		return changeEvent;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public ConsoleCommandHandle getCommand(String commandName) {
		return this._commands.get(commandName);
	}
	

	public static final int getAsInt(String value) {
		int intValue = 0;
		
		if (value != null) {
			try {
				intValue = Integer.parseInt(value);
			} catch (Exception e) {
				
			}
		}
		
		return intValue;
	}
	
	public static final float getAsFloat(String value) {
		float floatValue = 0;
		
		if (value != null) {
			try {
				floatValue = Float.parseFloat(value);
			} catch (Exception e) {
				
			}
		}
		
		return floatValue;
	}
	
	public static final String getAsString(String value) {
		String strValue = "";
		
		if (value != null) {
			strValue = value;
		}
		
		return strValue;
	}

}
