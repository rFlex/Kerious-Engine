/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.rpc
// RPCMethod.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 30, 2013 at 8:04:22 PM
////////

package net.kerious.engine.rpc;

import java.lang.reflect.Method;

import net.kerious.engine.world.WorldObject;

public class RPCMethod {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private Method method;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public RPCMethod(Method method) {
		this.method = method;
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void call(WorldObject worldObject, RPCModel model) {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
