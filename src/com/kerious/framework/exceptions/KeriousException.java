/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.exceptions
// KSException.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 15 ao�t 2012 at 21:53:14
////////

package com.kerious.framework.exceptions;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class KeriousException extends GdxRuntimeException {

	/*
	 * VARIABLES
	 */
	private static final long serialVersionUID = -2196433375453766890L;

	/*
	 * CONSTRUCTORS
	 */
	
	public KeriousException(Throwable cause) {
		super(cause);
	}
	
	public KeriousException(String message) {
		super("Kerious Framework error: " + message);
	}

	/*
	 * METHODS
	 */

	/*
	 * SETTERS
	 */

	/*
	 * GETTERS
	 */

}
