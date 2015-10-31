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
import net.kerious.engine.view.KView;

public class ViewController {

	////////////////////////
	// VARIABLES
	////////////////
	
	private KeriousEngine engine;
	private KView view;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ViewController(KeriousEngine engine) {
		if (engine == null) {
			throw new IllegalArgumentException("engine may not be null");
		}
		
		this.engine = engine;
		
		this.setView(new KView());
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
	
	public void detachView() {
		if (this.isKeyView()) {
			this.engine.setKeyView(null);
		} else {
			KView view = this.view;
			
			if (view != null) {
				view.removeFromParentView();
			}
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public KeriousEngine getEngine() {
		return this.engine;
	}
	
	public void setView(KView view) {
		this.view = view;
	}
	
	public KView getView() {
		return this.view;
	}
	
	public boolean isKeyView() {
		return this.view == this.engine.getKeyView();
	}
}
