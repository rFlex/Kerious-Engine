/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.controllers
// KController.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 3, 2013 at 5:12:58 PM
////////

package net.kerious.engine.controllers;

import net.kerious.engine.KeriousEngine;
import net.kerious.engine.view.View;

public class ViewController {

	////////////////////////
	// VARIABLES
	////////////////
	
	private KeriousEngine engine;
	private View view;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ViewController(KeriousEngine engine) {
		if (engine == null) {
			throw new IllegalArgumentException("engine may not be null");
		}
		
		this.engine = engine;
		
		this.setView(new View());
		this.view.setSize(engine.getRenderer().getWindowWidth(), engine.getRenderer().getWindowHeight());
	}

	////////////////////////
	// METHODS
	////////////////

	/**
	 * Set the view of the ViewController as the KeyView in the engine
	 */
	public void makeKeyView() {
		this.engine.setKeyView(this.getView());
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public KeriousEngine getEngine() {
		return this.engine;
	}
	
	public void setView(View view) {
		this.view = view;
	}
	
	public View getView() {
		return this.view;
	}
}
