package com.kerious.framework.network;

public interface Compressable<T> {

	void compress(T delta);
	void decompress(T delta);
	
}
