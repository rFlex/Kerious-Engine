/////////////////////////////////////////////////
// Project : Kerious-Engine
// Package : net.kerious.engine.font
// FontGenerator.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Mar 16, 2014 at 5:55:34 PM
////////

package net.kerious.engine.font;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public interface FontGenerator {

	BitmapFont generateFont(int size);
	
}
