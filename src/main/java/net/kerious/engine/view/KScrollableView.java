/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.view
// ScrollableView.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 2, 2013 at 7:18:18 PM
////////

package net.kerious.engine.view;

import net.kerious.engine.renderer.DrawingContext;

public class KScrollableView extends KView {

	////////////////////////
	// VARIABLES
	////////////////
	
	private float contentOffsetX;
	private float contentOffsetY;
	private float contentWidth;
	private float contentHeight;
	private float contentOffsetStartX;
	private float contentOffsetStartY;
	private float beganX;
	private float beganY;
	private boolean scrollEnabled;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KScrollableView() {
//		this.setClipsViews(true);
		this.setScrollEnabled(true);
	}

	////////////////////////
	// METHODS
	////////////////
	
	protected void drawSubviews(DrawingContext context, float currentScreenX, float currentScreenY, float currentAlpha) {
		this.drawingTransformMatrix.trn(-this.contentOffsetX, -this.contentOffsetY);
		super.drawSubviews(context, currentScreenX - this.contentOffsetX, currentScreenY - this.contentOffsetY, currentAlpha);
		this.drawingTransformMatrix.trn(this.contentOffsetX, this.contentOffsetY);
	}
	
	@Override
	public void onTouchDown(int pointer, float x, float y, int button) {
		this.beganX = x;
		this.beganY = y;
		this.contentOffsetStartX = this.contentOffsetX;
		this.contentOffsetStartY = this.contentOffsetY;
	}
	
	@Override
	public void onTouchDragged(int pointer, float x, float y, int button) {
		this.contentOffsetX = this.contentOffsetStartX + (this.beganX - x);
		this.contentOffsetY = this.contentOffsetStartY + (this.beganY - y);
//		this.setPosition(x, y);
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public float getContentOffsetX() {
		return contentOffsetX;
	}

	public void setContentOffsetX(float contentOffsetX) {
		this.contentOffsetX = contentOffsetX;
	}

	public float getContentOffsetY() {
		return contentOffsetY;
	}

	public void setContentOffsetY(float contentOffsetY) {
		this.contentOffsetY = contentOffsetY;
	}
	
	public void setContentOffset(float offsetX, float offsetY) {
		this.contentOffsetX = offsetX;
		this.contentOffsetY = offsetY;
	}

	public float getContentWidth() {
		return contentWidth;
	}

	public void setContentWidth(float contentWidth) {
		this.contentWidth = contentWidth;
	}

	public float getContentHeight() {
		return contentHeight;
	}

	public void setContentHeight(float contentHeight) {
		this.contentHeight = contentHeight;
	}

	public boolean isScrollEnabled() {
		return scrollEnabled;
	}

	public void setScrollEnabled(boolean scrollEnabled) {
		this.scrollEnabled = scrollEnabled;
	}
}
