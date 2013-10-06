/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight
// Library.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on 10 ao���t 2012 at 00:02:57
////////

package com.kerious.framework.library;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.utils.FileManager;

public class Library implements Disposable {

	////////////////////////
	// VARIABLES
	////////////////

	private Map<String, SpriteAttributes> spritesDeclarations;
	private Map<String, SpriteAttributes> animationDeclarations;
	private Map<String, AtlasAttributes> atlasDeclarations;
	private AssetManager assetManager;
	private boolean loadingStarted;
	private FileManager fileManager;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public Library(FileManager fileManager) {
		this.fileManager = fileManager;
		this.assetManager = new AssetManager();
		this.spritesDeclarations = new HashMap<String, SpriteAttributes>();
		this.animationDeclarations = new HashMap<String, SpriteAttributes>();
		this.atlasDeclarations = new HashMap<String, AtlasAttributes>();
		this.loadingStarted = false;
	}
	
	////////////////////////
	// METHODS
	////////////////

	private void parseSprite(AtlasAttributes atlas, Element spriteElement, boolean isAnimation) {
		final int textureX = spriteElement.getIntAttribute("texturex");
		final int textureY = spriteElement.getIntAttribute("texturey");
		final int textureWidth = spriteElement.getIntAttribute("texturew");
		final int textureHeight = spriteElement.getIntAttribute("textureh");
		final int tileLen = spriteElement.getIntAttribute("tilelen", 1);
		final int tileHeight = spriteElement.getIntAttribute("tileheight", 1);
		final String name = spriteElement.getAttribute("name");
		
		SpriteAttributes sprite = new SpriteAttributes(name, atlas, textureX, textureY, textureWidth, textureHeight, tileLen, tileHeight);
		if (isAnimation) {
			animationDeclarations.put(name, sprite);
		} else {
			spritesDeclarations.put(name, sprite);
		}
	}

	private void parseAtlas(Element atlasElement) {
		AtlasAttributes atlas = new AtlasAttributes(this, atlasElement.getAttribute("path"));

		atlasDeclarations.put(atlasElement.getAttribute("name"), atlas);
 
		Array<Element> sprites = atlasElement.getChildrenByName("sprite");
		for (int i = 0; i < sprites.size; i++) {
			parseSprite(atlas, sprites.get(i), false);
		}
		Array<Element> animations = atlasElement.getChildrenByName("animation");
		for (int i = 0; i < animations.size; i++) {
			parseSprite(atlas, animations.get(i), true);
		}
	}

	public void load(String krfFilePath) {
		String krfPath = "";
		String krfFile = "";
		
		int c = krfFilePath.length() - 1;
		while (krfFilePath.charAt(c) != '/') {
			c--;
		}
		krfPath = krfFilePath.substring(0, c);
		krfFile = krfFilePath.substring(c, krfFilePath.length());
		
		if (!krfPath.endsWith("/")) {
			krfPath += "/";
		}
		
		FileHandle handle = this.fileManager.getAssetFileHandle(krfPath + krfFile);
		String krfContent = handle.readString();
		
		XmlReader xmlReader = new XmlReader();
		Element root = xmlReader.parse(krfContent);

		Array<Element> graphicsChild =  root.getChildrenByName("graphics");
		if (graphicsChild.size == 1) {
			Element graphics = graphicsChild.get(0);
			Array<Element> atlases = graphics.getChildrenByName("atlas");
			for (int i = 0; i < atlases.size; i++) {
				parseAtlas(atlases.get(i));
			}
		} else {
			throw new KeriousException("Invalid resource file");
		}
	}

	public void startLoading() {
		this.loadingStarted = true;
	}

	public void update() {
		if (this.loadingStarted) {
			if (assetManager.update()) {
				loadingStarted = false;
			}
		}
	}
	
	public void dispose() {
		Collection<AtlasAttributes> attributes = atlasDeclarations.values();
		Iterator<AtlasAttributes> it = attributes.iterator();
		while (it.hasNext()) {
			AtlasAttributes attribute = it.next();
			attribute.dispose();
		}
	}
	
	public TextureRegionDrawable createDrawable(String spriteName) {
		return this.retrieveSprite(spriteName).toDrawable();
	}
	
	public TextureRegion createRegion(String spriteName) {
		return this.retrieveSprite(spriteName).toTextureRegion();
	}
	
	public Sprite createSprite(String spriteName) {
		return this.retrieveSprite(spriteName).toSprite();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public AtlasAttributes retrieveAtlas(String atlasName) {
		AtlasAttributes attributes = atlasDeclarations.get(atlasName);

		if (attributes == null) {
			throw new KeriousException("Atlas " + atlasName + " was not found in the library");
		}

		return attributes;
	}

	public SpriteAttributes retrieveSprite(String spriteName) {
		SpriteAttributes attributes = spritesDeclarations.get(spriteName);
		if (attributes == null)
			attributes = animationDeclarations.get(spriteName);

		if (attributes == null) {
			throw new KeriousException(spriteName + " was not found in the library");
		}

		return attributes;
	}

	public boolean loadingEnded() {
		return !loadingStarted;
	}

}
