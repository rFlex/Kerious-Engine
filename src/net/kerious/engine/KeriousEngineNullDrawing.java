/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine
// KeriousEngineNullDrawingImpl.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 5:19:52 PM
////////

package net.kerious.engine;

import net.kerious.engine.console.IntegerConsoleCommand;
import net.kerious.engine.renderer.NullRenderer;

public class KeriousEngineNullDrawing extends KeriousEngine {

	////////////////////////
	// VARIABLES
	////////////////

	private IntegerConsoleCommand fpsCommand;
	private boolean cont;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousEngineNullDrawing(KeriousEngineListener listener) {
		super(new NullRenderer(), null, listener);

		this.initCommands();
	}

	////////////////////////
	// METHODS
	////////////////
	
	private void initCommands() {
		this.fpsCommand = new IntegerConsoleCommand("fps_max", 1, 10000);
		
		this.getConsole().registerCommand(this.fpsCommand);
	}
	
	public void startLoop() {
		this.cont = true;
		
		while (this.cont) {
			this.render(0);
		}
	}
	
	public void render(float deltaTime) {
		this.update(deltaTime);
		this.draw();
	}
	
	public void exit() {
		this.cont = false;
	}
	
	public static void start(KeriousEngineListener listener, int fps) {
		 KeriousEngineNullDrawing kend = new KeriousEngineNullDrawing(listener);
		 kend.setFps(fps);
		 
		 kend.initialize();
		 kend.startLoop();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public int getFps() {
		return this.fpsCommand.getValue();
	}
	
	public void setFps(int fps) {
		this.fpsCommand.setValue(fps);
	}
}
