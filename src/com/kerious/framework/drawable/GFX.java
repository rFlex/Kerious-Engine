/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.drawable
// GFX.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 20, 2012 at 6:19:17 PM
////////

package com.kerious.framework.drawable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.kerious.framework.Application;
import com.kerious.framework.utils.Pool;
import com.kerious.framework.utils.Timer;

public class GFX extends AnimatedSpriteActor {

	////////////////////////
	// VARIABLES
	////////////////

	private static Pool<GFX> gfxPool = new Pool<GFX>();  
	private TerminationCondition terminationCondition;

	////////////////////////
	// NESTED CLASSES
	////////////////

	public static interface TerminationCondition {
		boolean shouldEnd(GFX gfx);
	}
	
	public static class AnimationFinishedCondition implements TerminationCondition {

		@Override
		public boolean shouldEnd(GFX gfx) {
			return gfx.getSprite().isAnimationFinished();
		}
		
	}
	
	public static class TimeElapseCondition implements TerminationCondition {

		final public Timer timer;
		
		public TimeElapseCondition(Application application, float elapseTime) {
			this.timer = new Timer(application);
			this.timer.start(elapseTime);
		}
		
		@Override
		public boolean shouldEnd(GFX gfx) {
			return timer.hasElapsed();
		}
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public GFX(Application application) {
		super(application);
	}
	
	////////////////////////
	// METHODS
	////////////////

	public static GFX createGFX(Application application, String spriteName, int layer) {
		return createGFX(application, spriteName, layer, null);
	}
	
	public static GFX createGFX(Application application, String spriteName, int layer, TerminationCondition terminationCondition) {
		return createGFX(application, spriteName, layer, null, terminationCondition);
	}
	
	public static GFX createGFX(Application application, String spriteName, Group outputGroup, TerminationCondition terminationCondition) {
		return createGFX(application, spriteName, 0, outputGroup, terminationCondition);
	}
	
	public static GFX createGFX(Application application, String spriteName, int layer, Group outputGroup, TerminationCondition terminationCondition) {
		GFX gfx = gfxPool.obtain();
		
		if (gfx == null) {
			gfx = new GFX(application);
		}
		
		gfx.setSpriteAttributesFromIdentifier(spriteName);
		gfx.scaleToSpriteAttributes();
		gfx.setTerminationCondition(terminationCondition);
		gfx.getSprite().setPlayMode(Animation.NORMAL);
		gfx.getSprite().animate(true);
		gfx.setColor(Color.WHITE);
		
		if (outputGroup != null) {
			outputGroup.addActor(gfx);
		} else {
			application.getCurrentGameWorld().addDrawable(gfx, layer);
		}
		
		return gfx;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (this.terminationCondition.shouldEnd(this)) {
			this.remove();
			this.getSprite().reset();
			gfxPool.release(this);
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final void setTerminationCondition(TerminationCondition terminationCondition) {
		if (terminationCondition == null) {
			terminationCondition = new TerminationCondition() {
				
				@Override
				public boolean shouldEnd(GFX gfx) {
					return true;
				}
			};
		}
		
		this.terminationCondition = terminationCondition;
	}
	
	public final TerminationCondition getTerminationCondition() {
		return this.terminationCondition;
	}
}
