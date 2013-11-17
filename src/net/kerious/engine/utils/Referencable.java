/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.utils
// Referencable.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:17:54 AM
////////

package net.kerious.engine.utils;

public interface Referencable {

	void retain();
	void release();
	void dispose();
	
	int getRetainCount();
}
