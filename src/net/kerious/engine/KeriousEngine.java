/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine
// KeriousEngine.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 4:50:09 PM
////////

package net.kerious.engine;

import net.kerious.engine.console.Console;
import net.kerious.engine.renderer.Renderer;
import net.kerious.engine.view.View;

import me.corsin.javatools.misc.Disposable;
import me.corsin.javatools.task.TaskQueue;

public class KeriousEngine implements Disposable {

	////////////////////////
	// VARIABLES
	////////////////

	final private TaskQueue taskQueue;
	final private Renderer renderer;
	final private Console console;
	final private KeriousEngineListener listener;
	private View keyView;
	private boolean disposed;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousEngine(Renderer renderer, KeriousEngineListener listener) {
		this.listener = listener;
		this.taskQueue = new TaskQueue();
		this.console = new Console();
		this.renderer = renderer;
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void initialize() {
		this.renderer.initialize();
	}
	
	public void update(float deltaTime) {
		this.taskQueue.flushTasks();
		
		if (this.keyView != null) {
			this.keyView.update(deltaTime);
		}
	}
	
	public void draw() {
		if (this.keyView != null) {
			this.renderer.render(this.keyView);
		}
	}
	
	@Override
	public void dispose() {
		this.taskQueue.close();
		this.renderer.dispose();
		this.disposed = true;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public TaskQueue getTaskQueue() {
		return this.taskQueue;
	}
	
	public Renderer getRenderer() {
		return this.renderer;
	}
	
	public boolean isDisposed() {
		return this.disposed;
	}

	public Console getConsole() {
		return console;
	}

	public KeriousEngineListener getListener() {
		return listener;
	}

	public View getKeyView() {
		return keyView;
	}

	public void setKeyView(View keyView) {
		this.keyView = keyView;
	}
}
