/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.exceptions
// UnsupportedPacketException.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 21, 2012 at 4:27:22 PM
////////

package com.kerious.framework.exceptions;

public class UnsupportedPacketException extends KeriousException {

	////////////////////////
	// VARIABLES
	////////////////
	
	private static final long serialVersionUID = -7794386516129660039L;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public UnsupportedPacketException(byte packetIdentifier) {
		super("Received unsupported packet type [" + (int)packetIdentifier + "]");
	}
	
	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
