/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// ResourceLoadListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 2:16:35 AM
////////

package net.kerious.engine.resource;

public interface ResourceLoadListener<T> {
	
	void onLoaded(ResourceManager resourceManager, String fileName, T resource);
	void onLoadingFailed(ResourceManager resourceManager, String fileName, Throwable exception);
	
}
