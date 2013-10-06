/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.console
// UserConsoleCommand.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 2, 2012 at 7:43:49 PM
////////

package com.kerious.framework.console;

import com.kerious.framework.server.User;

public abstract class UserConsoleCommand extends BaseConsoleCommand {

	////////////////////////
	// VARIABLES
	////////////////
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public UserConsoleCommand(String name, boolean isConfig) {
		super(name, isConfig);
	}

	////////////////////////
	// METHODS
	////////////////
	
	protected abstract void onCommandChanged(User sender, ConsoleCommandChangeEvent arg) throws Exception;

	@Override
	public void onFired(Object sender, ConsoleCommandChangeEvent arg) {
		try {
			if (sender instanceof User) {
				this.onCommandChanged((User)sender, arg);
			} else {
				arg.cancelChange("This commands needs the user.");
			}
		} catch (Exception e) {
			arg.cancelChange("Code crashed with error: " + e.getMessage());
		}
	}
	
}
