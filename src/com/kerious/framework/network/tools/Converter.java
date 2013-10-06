/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.framework.network.tools
// Converter.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 20, 2012 at 1:19:11 AM
////////

package com.kerious.framework.network.tools;

public class Converter {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////
	
	public static String humanReadableByteCount(long bytes, boolean si) {
		return humanReadableByteCount(bytes, si, false);
	}
	
	public static String humanReadableByteCount(long bytes, boolean si, boolean disablePrefixSuffix) {
		final int unit = si ? 1000 : 1024;
	    
	    if (bytes < unit) {
	    	return bytes + " B";
	    }
	    
	    final int exp = (int) (Math.log(bytes) / Math.log(unit));
	    
	    if (!disablePrefixSuffix) {
	    	final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    	return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	    } else {
	    	return String.format("%.1f", bytes / Math.pow(unit, exp));
	    }
	    
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
