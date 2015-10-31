/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine
// KeriousEngineLibgdxImpl.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 2, 2013 at 5:18:47 PM
////////

package net.kerious.engine;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import me.corsin.javatools.reflect.ReflectionUtils;
import net.kerious.engine.input.LibgdxInputManager;
import net.kerious.engine.renderer.LibgdxRenderer;
import net.kerious.engine.utils.ClasspathFileHandleResolver;
import net.kerious.engine.view.KView;

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
		
		KeriousEngine.sharedEngine = this;
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void create() {
		try {
			this.initialize();
			if (this.getListener() != null) {
				this.getListener().onReady(this);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.exit();
		}
	}
	
	@Override
	public void pause() {
		
	}

	@Override
	public void resize(int width, int height) {
		this.getRenderer().setWindowSize(width, height);
		
		KView keyView = this.getKeyView();
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
		start(listener, windowTitle, width, height, false);
	}
	
	public static void startFullScreen(KeriousEngineListener listener, String windowTitle) {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		start(listener, windowTitle, width, height, true);
	}
	
	public static void start(KeriousEngineListener listener, String windowTitle, int width, int height, boolean fullScreen) {
		Object cfg = ReflectionUtils.newInstance("com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration");
		
		// Is on desktop
		if (cfg != null) {
			ReflectionUtils.setPublicField(cfg, "title", windowTitle);
			ReflectionUtils.setPublicField(cfg, "width", width);
			ReflectionUtils.setPublicField(cfg, "height", height);
			ReflectionUtils.setPublicField(cfg, "fullscreen", fullScreen);
			
			Object lwjglInstance = ReflectionUtils.newInstance("com.badlogic.gdx.backends.lwjgl.LwjglApplication", new KeriousEngineLibgdx(listener), cfg);
			
			if (lwjglInstance == null) {
				throw new KeriousException("Failed to start desktop application");
			}
		} else {
			throw new KeriousException("Couldn't find the Libgdx classes. You need to link gdx-natives.jar, gdx-backend-lwjgl.jar and gdx-backend-lwjgl-natives.jar to your desktop project");
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
