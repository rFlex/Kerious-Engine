/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.server
// EntityStateCreator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 13, 2012 at 1:08:05 PM
////////

package com.kerious.framework.server;

import com.kerious.framework.network.protocol.packets.EntityState;

public interface EntityStateCreator {

	EntityState createForEntity();
	
}
