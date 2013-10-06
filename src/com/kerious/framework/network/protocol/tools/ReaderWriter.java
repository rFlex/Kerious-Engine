/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.network.protocol
// Reader.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 21, 2012 at 3:37:32 PM
////////

package com.kerious.framework.network.protocol.tools;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import com.kerious.framework.network.Packable;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class ReaderWriter {

	////////////////////////
	// VARIABLES
	////////////////

	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public ReaderWriter() {
		
	}
	
	////////////////////////
	// METHODS
	////////////////

	/**
	 * READ
	 */
	
	public final int read(int dispatch) {
		return this.readBuffer.getInt();
	}
	
	public final short read(short dispatch) {
		return this.readBuffer.getShort();
	}
	
	public final float read(float dispatch) {
		return this.readBuffer.getFloat();
	}
	
	public final double read(double dispatch) {
		return this.readBuffer.getDouble();
	}
	
	public final long read(long dispatch) {
		return this.readBuffer.getLong();
	}
	
	public final boolean read(boolean dispatch) {
		byte aByte = this.readBuffer.get();
		
		return aByte != 0;
	}
	
	public final byte read(byte aByte) {
		return this.readBuffer.get();
	}
	
	public final char[] read(char[] charArray) {
		for (int i = 0; i < charArray.length; i++) {
			charArray[i] = this.readBuffer.getChar();
		}
		return charArray;
	}
	
	@SuppressWarnings("unchecked")
	public final <T extends Packable> T[] read(Class<T> returnType, ObjectCreator<T> objectCreator) {
		short size = 0;
		size = this.read(size);
		
		T[] array = (T[]) Array.newInstance(returnType, size);
		
		for (int i = 0; i < size; i++) {
			T obj = objectCreator.instanciate();
			obj.unpack(this);
			array[i] = obj;
		}
		
		return array;
	}
	
	public final String read(String dispatch) {
		StringBuilder strBuilder = new StringBuilder();
		this.readBuffer.getChar();
		
		boolean cont = true;
		
		while (cont) {
			char c = this.readBuffer.getChar();
			
			if (c != '"') {
				strBuilder.append(c);
			} else {
				cont = false;
			}
		}
		return strBuilder.toString();
	}
	
	public final byte[] read(byte[] dest) {
		this.readBuffer.get(dest, 0, dest.length);
		
		return dest;
	}
	
	public final byte[] read(byte[] dest, int length) {
		this.readBuffer.get(dest, 0, length);

		return dest;
	}
	
	public final byte[] read(byte[] dest, int destIndex, int length) {
		this.readBuffer.get(dest, destIndex, length);
		
		return dest;
	}
	
	/**
	 * WRITE
	 */
	
	public final void write(byte aByte) {
		this.writeBuffer.put(aByte);
	}
	
	public final void write(int value) {
		this.writeBuffer.putInt(value);
	}
	
	public final void write(short value) {
		this.writeBuffer.putShort(value);
	}
	
	public final void write(long value) {
		this.writeBuffer.putLong(value);
	}

	public final void write(float value) {
		this.writeBuffer.putFloat(value);
	}
	
	public final void write(double value) {
		this.writeBuffer.putDouble(value);
	}
	
	public final void write(boolean value) {
		byte binaryVal = value ? (byte)1 : (byte)0;

		this.writeBuffer.put(binaryVal);
	}
	
	public final void write(char[] charArray) {
		for (int i = 0; i < charArray.length; i++) {
			this.writeBuffer.putChar(charArray[i]);
		}
	}
	
	public final void write(String str) {
		this.writeBuffer.putChar('"');

		if (str != null) {
			for (int i = 0; i < str.length(); i++) {
				this.writeBuffer.putChar(str.charAt(i));
			}
		}
		
		this.writeBuffer.putChar('"');
	}
	
	public final <T extends Packable> void write(T[] objects) {
		this.write((short)objects.length);
		
		for (int i = 0; i < objects.length; i++) {
			objects[i].pack(this);
		}
	}
	
	public final void write(byte[] src) {
		this.writeBuffer.put(src);
	}
	
	public final void write(byte[] src, int length) {
		this.writeBuffer.put(src, 0, length);
	}
	
	public final void write(byte[] src, int srcIndex, int length) {
		this.writeBuffer.put(src, srcIndex, length);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public final void setInputChannelBuffer(ByteBuffer buffer) {
		this.readBuffer = buffer;
	}
	
	public final void setOutputChannelBuffer(ByteBuffer buffer) {
		this.writeBuffer = buffer;
	}
	
	public final ByteBuffer getInputBuffer() {
		return this.readBuffer;
	}
	
	public final ByteBuffer getOutputBuffer() {
		return this.writeBuffer;
	}
}
