/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.framework.utils
// Utils.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 17, 2012 at 5:45:04 PM
////////

package com.kerious.framework.utils;

public final class Utils {

	////////////////////////
	// VARIABLES
	////////////////

	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	public static float limit(float value, float maxValue) {
		if (value > maxValue) {
			return maxValue;
		}
		return value;
	}
	
	public static float limit(float value, float minValue, float maxValue) {
		if (value >= minValue && value <= maxValue) {
			return value;
		} else if (value < minValue) {
			return minValue;
		}
		return maxValue;
	}

	public static float rangeRandom(float minimum, float maximum) {
		return minimum + (int)(Math.random() * ((maximum - minimum) + 1));
	}

	public static float currentTime(long startTime) {
		long time = System.currentTimeMillis() - startTime;
		
		return ((float)time) / 1000f;
	}
	
	public static byte compressToByte(float number) {
		return (byte)(number * 100f);
	}
	
	public static short compressToShort(float number) {
		return (short)(number * 100f);
	}
	
	public static float decompress(short number) {
		return ((float)number) / 100f;
	}
	
	public static float decompress(byte number) {
		return ((float)number) / 100f;
	}
	
	public static byte setBit(byte mask, int bitNumber, boolean state) {
		return state ? (mask |= 0x1 << bitNumber) : (mask &= (-1) ^ (0x1 << bitNumber));
	}
	
	public static short setBit(short mask, int bitNumber, boolean state) {
		return state ? (mask |= 0x1 << bitNumber) : (mask &= (-1) ^ (0x1 << bitNumber));
	}
	
	public static boolean getBit(short mask, int bitNumber) {
		return (mask & (0x1 << bitNumber)) >> bitNumber == 0x1;
	}
	
	public static boolean getBit(byte mask, int bitNumber) {
		return (mask & (0x1 << bitNumber)) >> bitNumber == 0x1;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
