/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.renderer
// Renderer.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 4:58:07 PM
////////

package net.kerious.engine.renderer;

import me.corsin.javatools.misc.Disposable;
import net.kerious.engine.view.KView;

public interface Renderer extends Disposable {
	
	void initialize();
	void render(KView view);
	
	void setWindowSize(float width, float height);
	
	float getWindowWidth();
	float getWindowHeight();
	
	DrawingContext getContext();
	boolean isDrawingContextAvailable();

}
