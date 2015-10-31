/////////////////////////////////////////////////
// Project : Kerious-Engine
// Package : net.kerious.engine.font
// LibgdxFontGenerator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 16, 2014 at 5:56:08 PM
////////

package net.kerious.engine.font;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class LibgdxFontGenerator implements FontGenerator {

	////////////////////////
	// VARIABLES
	////////////////

	private FreeTypeFontGenerator fontGenerator;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LibgdxFontGenerator(FileHandle fileHandle) {
		this.fontGenerator = new FreeTypeFontGenerator(fileHandle);
	}

	////////////////////////
	// METHODS
	////////////////

	@Override
	public BitmapFont generateFont(int size) {
		return this.fontGenerator.generateFont(size);
	}
	
	protected void finalize() throws Throwable {
		try {
			super.finalize();
		} finally {
			this.fontGenerator.dispose();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
