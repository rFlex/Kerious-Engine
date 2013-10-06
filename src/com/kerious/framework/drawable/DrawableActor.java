/////////////////////////////////////////////////
// Project : kerious-framework
// Package : com.kerious.framework.drawable
// DrawableActor.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 18, 2012 at 12:48:24 PM
////////

package com.kerious.framework.drawable;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;

public class DrawableActor extends Group {
	
	////////////////////////
	// VARIABLES
	////////////////
	

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DrawableActor() {
		this.setTransform(false);
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Rectangle getFrame() {
		return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	public void setFrame(Rectangle frame) {
		this.setBounds(frame.x, frame.y, frame.width, frame.height);
	}
	
	public final float getCenterX() {
		return this.getX() + getWidth() / 2;
	}
	
	public final float getCenterY() {
		return this.getY() + getHeight() / 2;
	}
	
	public final void scaleSize(float scaleWidth, float scaleHeight) {
		this.setDimension(getWidth() * scaleWidth, getHeight() * scaleHeight);
	}
	
	public void setFrame(float x, float y, float width, float height) {
		this.setBounds(x, y, width, height);
	}
	
	public void setDimension(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public void setPositionCenter(float x, float y) {
		this.setPosition(x - getWidth() / 2, y - getHeight() / 2);
	}
	
	public final void setAlpha(float alpha) {
		this.getColor().a = alpha;
	}

}
