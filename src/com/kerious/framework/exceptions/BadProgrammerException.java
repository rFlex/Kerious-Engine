/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.exceptions
// BadProgrammerException.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 19, 2012 at 3:38:58 PM
////////

package com.kerious.framework.exceptions;

public class BadProgrammerException extends KeriousException {

	////////////////////////
	// VARIABLES
	////////////////
	
	private static final long serialVersionUID = -4467097844413904485L;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public BadProgrammerException(String message) {
		super("Invalid program construction: " + message);
	}
	
	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
