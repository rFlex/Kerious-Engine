/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.server
// InputCommandListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 25, 2012 at 2:56:04 PM
////////

package com.kerious.framework.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.kerious.framework.console.Console;
import com.kerious.framework.console.ConsoleCommandChangeEvent;

public class InputCommandListener {

	////////////////////////
	// VARIABLES
	////////////////

	private Console console;
	private BufferedReader reader;
	private boolean started;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public InputCommandListener(Console console) {
		this.console = console;
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	public void start() {
		this.started = true;
		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		while (started) {
			try {
				final String line = reader.readLine();
				
				if (line != null) {
					ConsoleCommandChangeEvent event = this.console.enterCommand(line);
					if (event.getMessage() != null) {
						System.out.println(event.getMessage());
					}
				}
			} catch (IOException e) {
				
			}
		}
	}
	
	public void stop() {
		this.started = false;
		
		if (this.reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
