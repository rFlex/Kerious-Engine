/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.resource
// ResourceBundleListener.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2013 at 12:04:20 PM
////////

package net.kerious.engine.resource;

public interface ResourceBundleListener {

	void onLoadedItem(ResourceManager manager, ResourceBundle bundle, String fileName);
	void onLoaded(ResourceManager manager, ResourceBundle bundle);
	void onLoadingFailed(ResourceManager manager, ResourceBundle bundle, Throwable exception);
	void onLoadingProgressChanged(ResourceManager manager, ResourceBundle bundle, float progressRatio);
	
}
