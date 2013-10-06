/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.client.hud
// IEventListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 10, 2012 at 1:02:22 PM
////////

package com.kerious.framework.utils;

public interface IEventListener<T> {

	public void onFired(Object sender, T arg);
	
}
