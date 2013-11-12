/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.view
// Label.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 4, 2013 at 8:14:06 PM
////////

package net.kerious.engine.view;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;

import net.kerious.engine.renderer.DrawingContext;

public class LabelView extends View {

	////////////////////////
	// VARIABLES
	////////////////
	
	private String text;
	private BitmapFont font;
	private HAlignment alignment;
	private BitmapFontCache fontCache;
	private TextBounds textBounds;
	private boolean fontCacheDirty;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public LabelView() {
		super();
		this.alignment = HAlignment.LEFT;
		this.setTouchEnabled(false);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void drawView(DrawingContext context, float width, float height, float alpha) {
		if (this.fontCache != null) {
			this.computeIfDirty();
			
			this.fontCache.draw(context.getBatch(), alpha);
		}
	}
	
	final private void computeIfDirty() {
		if (this.fontCacheDirty) {
			this.fontCacheDirty = false;
			this.fontCache.clear();
			this.textBounds = this.fontCache.addWrappedText(this.text, 0, 0, this.getWidth(), this.alignment);
			this.fontCache.translate(0, this.textBounds.height);
		}
	}
	
	/**
	 * Resize the label in order to have a frame that exactly fit the current text
	 */
	public void resizeToFit() {
		if (this.fontCache != null) {
			this.computeIfDirty();
			
			this.setHeight(this.textBounds.height);
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

	public BitmapFont getFont() {
		return font;
	}

	public void setFont(BitmapFont font) {
		this.font = font;
		if (font != null) {
			this.fontCache = new BitmapFontCache(font);
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
}
