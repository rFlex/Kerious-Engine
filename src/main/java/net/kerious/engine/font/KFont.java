/////////////////////////////////////////////////
// Project : Kerious-Engine
// Package : net.kerious.engine.font
// KFont.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 16, 2014 at 5:54:56 PM
////////

package net.kerious.engine.font;

import net.kerious.engine.KeriousEngine;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class KFont {

	////////////////////////
	// VARIABLES
	////////////////

	private FontGenerator fontGenerator;
	private BitmapFont bitmapFont;
	private int size;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KFont(String fontPath, int size) {
		this(KeriousEngine.sharedEngine.getFileHandleResolver().resolve(fontPath), size);
	}
	
	public KFont(FileHandle fontHandle, int size) {
		this(new LibgdxFontGenerator(fontHandle), size);
	}
	
	private KFont(FontGenerator fontGenerator, int size) {
		this.fontGenerator = fontGenerator;
		this.bitmapFont = this.fontGenerator.generateFont(size);
		this.size = size;
	}

	////////////////////////
	// METHODS
	////////////////
	

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public int getSize() {
		return this.size;
	}
	
	public KFont createWithSize(int size) {
		return new KFont(this.fontGenerator, size);
	}
	
	public BitmapFont getBitmapFont() {
		return this.bitmapFont;
	}
}
