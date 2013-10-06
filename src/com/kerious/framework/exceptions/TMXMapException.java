/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.tmx
// InvalidMapException.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 6, 2012 at 10:18:32 PM
////////

package com.kerious.framework.exceptions;

public class TMXMapException extends KeriousException {

	////////////////////////
	// VARIABLES
	////////////////

	private static final long serialVersionUID = -849034103133386021L;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public TMXMapException(String mapName, String msg) {
		super("Error while loading map " + mapName + ": " + msg);
	}
	
	public TMXMapException(String msg) {
		super("Error while processing a map: " + msg);
	}
	
	////////////////////////
	// METHODS
	////////////////

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
