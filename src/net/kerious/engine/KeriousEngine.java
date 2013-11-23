/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine
// KeriousEngine.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 4:50:09 PM
////////

package net.kerious.engine;

import me.corsin.javatools.misc.Disposable;
import me.corsin.javatools.task.TaskQueue;
import net.kerious.engine.console.Console;
import net.kerious.engine.input.InputManager;
import net.kerious.engine.renderer.Renderer;
import net.kerious.engine.resource.ResourceManager;
import net.kerious.engine.skin.SkinManager;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.utils.TemporaryUpdatableArray;
import net.kerious.engine.view.View;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;

public abstract class KeriousEngine implements Disposable {

	////////////////////////
	// VARIABLES
	////////////////

	final private TaskQueue taskQueue;
	final private Renderer renderer;
	final private Console console;
	final private KeriousEngineListener listener;
	final private InputManager inputManager;
	final private TemporaryUpdatableArray<TemporaryUpdatable> updatables;
	final private SkinManager globalSkinManager;
	final private FileHandleResolver fileHandleResolver;
	final private ResourceManager resourceManager;
	private View keyView;
	private boolean disposed;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KeriousEngine(Renderer renderer, InputManager inputManager, FileHandleResolver fileHandleResolver, KeriousEngineListener listener) {
		this.listener = listener;
		this.inputManager = inputManager;
		this.taskQueue = new TaskQueue();
		this.console = new Console();
		this.updatables = new TemporaryUpdatableArray<TemporaryUpdatable>(TemporaryUpdatable.class);
		this.globalSkinManager = new SkinManager();
		this.globalSkinManager.setAutoAttributeId(true);
		this.renderer = renderer;
		this.fileHandleResolver = fileHandleResolver;
		this.resourceManager = new ResourceManager(this, fileHandleResolver);
	}

	////////////////////////
	// METHODS
	////////////////
	
	public void initialize() {
		this.inputManager.initialize();
		this.renderer.initialize();
	}
	
	public void update(float deltaTime) {
		this.taskQueue.flushTasks();
		
		this.updatables.update(deltaTime);
		
		if (this.keyView != null) {
			this.keyView.update(deltaTime);
		}
	}
	
	public void draw() {
		this.renderer.render(this.keyView);
	}
	
	public void addTemporaryUpdatable(TemporaryUpdatable updatable) {
		this.updatables.add(updatable);
	}
	
	public void removeTemporaryUpdatable(TemporaryUpdatable updatable) {
		this.updatables.removeValue(updatable, true);
	}
	
	public abstract void exit();
	
	@Override
	public void dispose() {
		this.taskQueue.close();
		this.renderer.dispose();
		this.resourceManager.close();
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
		this.inputManager.setTouchResponder(keyView);
	}

	public InputManager getInputManager() {
		return inputManager;
	}
	
	public SkinManager getGlobalSkinManager() {
		return this.globalSkinManager;
	}

	public FileHandleResolver getFileHandleResolver() {
		return fileHandleResolver;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}
}
