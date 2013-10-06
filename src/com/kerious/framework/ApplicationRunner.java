/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework
// ApplicationRunner.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 27, 2012 at 11:50:07 PM
////////

package com.kerious.framework;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.kerious.framework.server.KeriousServer;

public abstract class ApplicationRunner implements ApplicationListener {


	////////////////////////
	// VARIABLES
	////////////////

	protected KeriousServer keriousServer;
	private Application application;
	private boolean enableGfx;
	private int width;
	private int height;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ApplicationRunner(boolean enableGfx, int width, int height) {
		this.enableGfx = enableGfx;
		this.width = width;
		this.height = height;
	}

	////////////////////////
	// METHODS
	////////////////

	public abstract void onReady(Application application);
	public abstract void onDisposed();
	
	@Override
	public void create() {
		this.application = new Application(enableGfx, width, height);
		this.onReady(application);
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		this.application.flush();
		this.application.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		this.application.dispose();
		this.onDisposed();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Application getApplication() {
		return this.application;
	}

	public KeriousServer getKeriousServer() {
		return keriousServer;
	}

	public void setKeriousServer(KeriousServer keriousServer) {
		this.keriousServer = keriousServer;
	}
}
