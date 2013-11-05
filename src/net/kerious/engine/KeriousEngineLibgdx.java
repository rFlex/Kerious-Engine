/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine
// KeriousEngineLibgdxImpl.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 5:18:47 PM
////////

package net.kerious.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

import me.corsin.javatools.reflect.ReflectionUtils;
import net.kerious.engine.renderer.LibgdxRenderer;

public class KeriousEngineLibgdx extends KeriousEngine implements ApplicationListener {

	////////////////////////
	// VARIABLES
	////////////////
	

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeriousEngineLibgdx(KeriousEngineListener listener) {
		super(new LibgdxRenderer(), listener);
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
		Object cfg = ReflectionUtils.newInstance("LwjglApplicationConfiguration");
		
		// Is on desktop
		if (cfg != null) {
			ReflectionUtils.setPublicField(cfg, "title", windowTitle);
			ReflectionUtils.setPublicField(cfg, "width", width);
			ReflectionUtils.setPublicField(cfg, "height", height);
			
			Object lwjglInstance = ReflectionUtils.newInstance("LwjglApplication", new KeriousEngineLibgdx(listener), cfg);
			
			if (lwjglInstance == null) {
				throw new KeriousException("Failed to start desktop application");
			}
		} else {
			throw new KeriousException("The start method is only available on desktop for now");
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
}
