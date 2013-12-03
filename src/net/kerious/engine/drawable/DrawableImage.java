/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.drawable
// DrawableImage.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 5, 2013 at 8:12:22 PM
////////

package net.kerious.engine.drawable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.kerious.engine.KeriousException;
import net.kerious.engine.renderer.DrawingContext;
import net.kerious.engine.resource.ResourceManager;

public class DrawableImage implements Drawable {

	////////////////////////
	// VARIABLES
	////////////////
	
	private TextureRegion textureRegion;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public DrawableImage(TextureRegion textureRegion) {
		this.setTextureRegion(textureRegion);
	}
	
	public DrawableImage(Texture texture) {
		this.setTextureRegion(new TextureRegion(texture));
	}
	
	public DrawableImage(String fileName) {
		if (ResourceManager.sharedResourceManager == null) {
			throw new KeriousException("No shared resource manager was set.");
		}
		this.setTextureRegion(new TextureRegion(ResourceManager.sharedResourceManager.<Texture>get(fileName)));
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void draw(DrawingContext context, float x, float y, float width, float height, float alpha) {
		if (this.textureRegion != null) {
			context.fillRectangle(this.textureRegion, x, y, width, height, alpha, 0);
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public TextureRegion getTextureRegion() {
		return textureRegion;
	}

	public void setTextureRegion(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}
}
