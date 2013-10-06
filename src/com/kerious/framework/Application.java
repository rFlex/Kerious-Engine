/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight
// Game.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 10 ao�t 2012 at 00:25:22
////////

package com.kerious.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.kerious.framework.console.Console;
import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.library.Library;
import com.kerious.framework.network.NetworkManager;
import com.kerious.framework.utils.FileManager;
import com.kerious.framework.utils.StopWatch.TimeProvider;
import com.kerious.framework.utils.tasks.TaskManager;
import com.kerious.framework.world.GameWorld;
import com.kerious.framework.world.StageGroup;

public final class Application implements Disposable, TimeProvider {

	////////////////////////
	// VARIABLES
	////////////////
	
	public final StageGroup baseStage;
	public final SpriteBatch batch;
	public final Library library;
	public final NetworkManager networkManager;
	public final TaskManager taskManager;
	public final FileManager fileManager;
	public final int width;
	public final int height;
	public final boolean drawingEnabled;
	private Console console;
	private GameWorld currentGameWorld;
	private long currentRenderingTime;
	private long frameNumber;
	private final long timeAtStart;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Application(int width, int height) {
		this(true, width, height);
	}
	
	public Application(boolean enableDrawing, int width, int height) {
		this.drawingEnabled = enableDrawing;
		this.width = width;
		this.height = height;
		
		this.fileManager = new FileManager();
		this.library = new Library(fileManager);

		if (enableDrawing) {
			this.batch = new SpriteBatch();
		} else {
			this.batch = null;
		}
		
		this.taskManager = new TaskManager();
		this.baseStage = new StageGroup(width, height, this.batch, enableDrawing);
		this.networkManager = new NetworkManager();
		this.timeAtStart = System.currentTimeMillis();
		this.frameNumber = 0;
	}

	////////////////////////
	// METHODS
	////////////////
	
	public final void flush() {
		this.currentRenderingTime = System.currentTimeMillis() - timeAtStart;

		this.networkManager.flush();
		this.taskManager.flush();
	}
	
	public void render(float deltaTime) {
		this.baseStage.act(deltaTime);
		
		if (this.drawingEnabled) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			this.baseStage.draw();
		}
		
		this.frameNumber++;
	}
	
	@Override
	public void dispose() {
		this.networkManager.dispose();
		
		if (this.drawingEnabled && this.batch != null) {
			this.batch.dispose();
		}
		
		if (library != null) {
			library.dispose();
		}
	}

	public void acquireInputHandling(Stage stage) {
		Gdx.input.setInputProcessor(stage);
	}
	
	public void presentGameWorld(GameWorld world) {
		this.presentGameWorld(world, false);
	}
	
	public void presentGameWorld(GameWorld world, boolean acquireInput) {
		if (world == null) {
			throw new KeriousException("GameWorld cannot be null");
		}
		
		this.removeGameWorld();
		
		this.baseStage.addStage(world);
		
		this.currentGameWorld = world;
		
		if (this.drawingEnabled && acquireInput) {
			this.acquireInputHandling(world);
		}
	}
	
	public void removeGameWorld() {
		if (this.currentGameWorld != null) {
			this.currentGameWorld.removeFromSuperStage();
			this.currentGameWorld = null;
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public static final EOSPlatform getRunningPlatform() {
		return EOSPlatform.OSX;
	}
	
	public final void setCurrentGameWorld(GameWorld world) {
		this.currentGameWorld = world;
	}
	
	public final GameWorld getCurrentGameWorld() {
		return this.currentGameWorld;
	}
	
	public final void setConsole(Console console) {
		this.console = console;
	}
	
	public final Console getConsole() {
		return this.console;
	}

	@Override
	public long getCurrentTime() {
		return this.currentRenderingTime;
	}
	
	public final long getFrameNumber() {
		return this.frameNumber;
	}
}
