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
import net.kerious.engine.renderer.DrawingContext;
import net.kerious.engine.view.View;

public class ViewController {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private KeriousEngine engine;
	final private Camera camera;
	private View view;
	private ViewController parentViewController;
	private ViewController presentedViewController;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ViewController(KeriousEngine engine) {
		this.engine = engine;
		
		this.setView(new View());
		this.view.getFrame().setSize(engine.getRenderer().getWindowWidth(), engine.getRenderer().getWindowHeight());
		this.camera = new Camera(this.view.getFrame().width, this.view.getFrame().height);
	}

	////////////////////////
	// METHODS
	////////////////

	public void act(float deltaTime) {
		if (this.presentedViewController == null) {
			this.view.act(deltaTime);
		} else {
			this.presentedViewController.act(deltaTime);
		}
	}
	
	public void render(DrawingContext context) {
		this.camera.applyToContext(context);

		context.startDrawing();
		
		this.view.draw(this.camera, context, this.camera.x +  this.view.getFrame().x, this.camera.y + this.view.getFrame().y, 1);
		
		context.endDrawing();
		
		if (this.parentViewController != null) {
			this.parentViewController.render(context);
		}
	}
	
	public void hide() {
		if (this.parentViewController != null) {
			this.parentViewController.hideShownViewController();
		} else {
			this.engine.getConsole().print("Attempted to dismiss a view controller which has no parent view controller");
		}
	}
	
	public void hideShownViewController() {
		if (this.presentedViewController != null) {
			this.presentedViewController.parentViewController = null;
			this.presentedViewController = null;
		} else {
			this.engine.getConsole().print("Attempted to dismiss a presented view controller which has no presented view controller");
		}
	}
	
	public void showViewController(ViewController viewController) {
		if (viewController == null) {
			throw new IllegalArgumentException("viewController may not be null");
		}
		
		if (this.presentedViewController == null) {
			viewController.parentViewController = this;
			this.presentedViewController = viewController;
		} else {
			this.engine.getConsole().print("Attempted to present a view controller to another view controller that already displays another view controller");
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Camera getCamera() {
		return this.camera;
	}
	
	public KeriousEngine getEngine() {
		return this.engine;
	}
	
	public void setView(View view) {
		if (view == null) {
			view = new View();
		}
		
		this.view = view;
	}
	
	public View getView() {
		return this.view;
	}
}
