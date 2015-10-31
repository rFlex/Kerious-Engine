/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.view
// Label.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 4, 2013 at 8:14:06 PM
////////

package net.kerious.engine.view;

import net.kerious.engine.font.KFont;
import net.kerious.engine.renderer.DrawingContext;

import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;

public class KLabelView extends KView {

	////////////////////////
	// VARIABLES
	////////////////
	
	private String text;
	private KFont font;
	private HAlignment alignment;
	private BitmapFontCache fontCache;
	private TextBounds textBounds;
	private boolean fontCacheDirty;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public KLabelView() {
		super();
		this.text = "";
//		this.setFont(new BitmapFont());
		this.alignment = HAlignment.LEFT;
		this.setTouchEnabled(false);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void drawView(DrawingContext context, float width, float height, float alpha) {
		if (this.fontCache != null) {
			this.computeIfDirty(this.getWidth());
			
			this.fontCache.draw(context.getBatch(), alpha);
		}
	}
	
	final private void computeIfDirty(float maxWidth) {
		if (this.fontCacheDirty) {
			this.fontCacheDirty = false;
			this.fontCache.clear();
			this.textBounds = this.fontCache.addWrappedText(this.text, 0, 0, maxWidth, this.alignment);
			this.fontCache.translate(0, this.textBounds.height);
		}
	}
	
	/**
	 * Resize the label in order to have a frame that exactly fit the current text
	 */
	public void resizeToFit() {
		if (this.fontCache != null) {
			this.computeIfDirty(Float.MAX_VALUE);
			
			this.setSize(this.textBounds.width, this.textBounds.height);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.fontCacheDirty = true;
	}

	public KFont getFont() {
		return font;
	}

	public void setFont(KFont font) {
		this.font = font;
		if (font != null) {
			this.fontCache = new BitmapFontCache(font.getBitmapFont());
			this.fontCacheDirty = true;
		} else {
			this.fontCache = null;
		}
	}

	public HAlignment getAligment() {
		return alignment;
	}

	public void setAligment(HAlignment aligment) {
		this.alignment = aligment;
		this.fontCacheDirty = true;
	}
	
	@Override
	public void setFrame(float x, float y, float width, float height) {
		super.setFrame(x, y, width, height);
		
		this.fontCacheDirty = true;
	}
}
