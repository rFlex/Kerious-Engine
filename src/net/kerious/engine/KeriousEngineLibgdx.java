/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine
// KeriousEngineLibgdxImpl.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 5:18:47 PM
////////

package net.kerious.engine;

import me.corsin.javatools.reflect.ReflectionUtils;
import net.kerious.engine.input.LibgdxInputManager;
import net.kerious.engine.renderer.LibgdxRenderer;
import net.kerious.engine.resource.ResourceManager;
import net.kerious.engine.view.View;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

public class KeriousEngineLibgdx extends KeriousEngine implements ApplicationListener {

	////////////////////////
	// VARIABLES
	////////////////
	

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeriousEngineLibgdx(KeriousEngineListener listener) {
		super(new LibgdxRenderer(), new LibgdxInputManager(), new InternalFileHandleResolver(), listener);
		ResourceManager.sharedResourceManager = this.getResourceManager();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void create() {
		this.initialize();
		
		if (this.getListener() != null) {
			this.getListener().onReady(this);
		}
	}
	
	@Override
	public void pause() {
		
	}

	@Override
	public void resize(int width, int height) {
		this.getRenderer().setWindowSize(width, height);
		
		View keyView = this.getKeyView();
		if (keyView != null) {
			keyView.setFrame(0, 0, width, height);
		}
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void render() {
		this.update(Gdx.graphics.getDeltaTime());
		this.draw();
	}
	
	public static void start(KeriousEngineListener listener, String windowTitle, int width, int height) {
		Object cfg = ReflectionUtils.newInstance("com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration");
		
		// Is on desktop
		if (cfg != null) {
			ReflectionUtils.setPublicField(cfg, "title", windowTitle);
			ReflectionUtils.setPublicField(cfg, "width", width);
			ReflectionUtils.setPublicField(cfg, "height", height);
			
			Object lwjglInstance = ReflectionUtils.newInstance("com.badlogic.gdx.backends.lwjgl.LwjglApplication", new KeriousEngineLibgdx(listener), cfg);
			
			if (lwjglInstance == null) {
				throw new KeriousException("Failed to start desktop application");
			}
		} else {
			throw new KeriousException("Couldn't find the Libgdx classes");
		}
	}

	@Override
	public void exit() {
		Gdx.app.exit();
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
