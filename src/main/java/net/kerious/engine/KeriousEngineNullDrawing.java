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
import net.kerious.engine.input.NullInputManager;
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
		super(new NullRenderer(), new NullInputManager(), new RelativeFileResolver(), listener);

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
		float deltaTime = 0;
		
		long currentTime = 0;
		long timeBeforeRender = 0;
		long timeTakenForRender = 0;
		long bestTimePerFrame = 0;
		long timeToSleep = 0;
		
		while (this.cont) {
			currentTime = System.nanoTime();
			
			if (timeBeforeRender != 0) {
				deltaTime = ((float)((currentTime - timeBeforeRender) / 1000)) / 1000000f;
			}
			
			timeBeforeRender = currentTime;
			
			this.render(deltaTime);
			
			timeTakenForRender = System.nanoTime() - timeBeforeRender;
			
			bestTimePerFrame = 1000000000 / this.fpsCommand.getValue().longValue();
			timeToSleep = bestTimePerFrame - timeTakenForRender;
			
			if (timeToSleep > 0) {
				long msPart = timeToSleep / 1000000;
				int nanoPart = (int)(timeToSleep - (msPart * 1000000));
				try {
					Thread.sleep(msPart, nanoPart);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void render(float deltaTime) {
		this.update(deltaTime);
		this.draw();
	}
	
	public void exit() {
		this.cont = false;
	}
	
	/**
	 * Start the engine and the loop in a new allocated thread
	 * @param listener
	 * @param fps
	 */
	public static void startAsync(KeriousEngineListener listener, int fps) {
		final KeriousEngineListener finalListener = listener;
		final int finalFps = fps;
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
				start(finalListener, finalFps);
			}
		});
		thread.start();
	}


	/**
	 * Start the engine and the loop in the current thread
	 * @param listener
	 * @param fps
	 */
	public static void start(KeriousEngineListener listener, int fps) {
		 KeriousEngineNullDrawing kend = new KeriousEngineNullDrawing(listener);
		 kend.setFps(fps);
		 
		 kend.initialize();
		 listener.onReady(kend);
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
