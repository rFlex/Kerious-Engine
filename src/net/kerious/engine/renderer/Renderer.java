/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.renderer
// Renderer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 4:58:07 PM
////////

package net.kerious.engine.renderer;

import net.kerious.engine.view.View;
import me.corsin.javatools.misc.Disposable;

public interface Renderer extends Disposable {
	
	void initialize();
	void render(View view);
	
	float getWindowWidth();
	float getWindowHeight();

}