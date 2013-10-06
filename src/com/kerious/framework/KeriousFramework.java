/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework
// KeriousFramework.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 1, 2012 at 7:10:33 PM
////////

package com.kerious.framework;

import com.kerious.framework.network.protocol.KeriousUDPPacketFactory;

public class KeriousFramework {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	public static void init() {
		KeriousUDPPacketFactory.getInstance();
		KeriousObjectFactory.init();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
