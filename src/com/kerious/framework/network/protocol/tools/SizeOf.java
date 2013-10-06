/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network.protocol
// SizeOf.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 21, 2012 at 4:14:01 PM
////////

package com.kerious.framework.network.protocol.tools;

import java.util.ArrayList;

import com.kerious.framework.network.protocol.KeriousUDPPacket;

public class SizeOf {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	public static int sizeof(int integer) {
		return Integer.SIZE / 8;
	}
	
	public static int sizeof(short shortInteger) {
		return Short.SIZE / 8;
	}
	
	public static int sizeof(float floating) {
		return Float.SIZE / 8;
	}
	
	public static int sizeof(double doubleFloat) {
		return Double.SIZE / 8;
	}
	
	public static int sizeof(byte[] array) {
		return (Byte.SIZE / 8) * array.length;
	}
	
	public static int sizeof(char[] charArray) {
		return charArray.length * (Character.SIZE / 8);
	}
	
	public static int sizeof(String str) {
		if (str == null) {
			return (2 * (Character.SIZE / 8));
		} else {
			return (str.length() + 2) * (Character.SIZE / 8);
		}
	}
	
	public static int sizeof(byte aByte) {
		return (Byte.SIZE / 8);
	}
	
	public static int sizeof(KeriousUDPPacket packet) {
		return packet.size();
	}
	
	public static int sizeof(boolean bool) {
		return Byte.SIZE / 8;
	}
	

	public static <T extends Sizable> int sizeof(ArrayList<T> events) {
		int size = Short.SIZE / 8;
		
		for (T event : events) {
			size += event.size();
		}
		
		return size;
	}
	
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
