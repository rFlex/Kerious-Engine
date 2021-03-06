/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.skin
// SkinManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 13, 2013 at 5:09:08 PM
////////

package net.kerious.engine.skin;

import net.kerious.engine.view.KView;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

public class SkinManager {

	////////////////////////
	// VARIABLES
	////////////////
	
	final private ObjectMap<String, Skin> skinsByName;
	final private IntMap<Skin> skinsById;
	private String directory;
	private short sequence;
	private boolean autoAttributeId;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public SkinManager() {
		this.sequence = 1;
		this.skinsByName = new ObjectMap<String, Skin>();
		this.skinsById = new IntMap<Skin>();
		this.setDirectory(null);
	}

	////////////////////////
	// METHODS
	////////////////
	
	private Skin loadSkinFromFilename(String fileName) throws SkinException {
		throw new SkinException("Load skin failed: Not implemented");
	}
	
	final private void attributeSkinId(Skin skin, short id) {
		int oldId = skin.getId();
		
		if (oldId != id) {
			if (oldId > 0) {
				this.skinsById.remove(oldId);
			}

			skin.setId(id);
			this.skinsById.put(id, skin);
		}
	}
	
	public void registerSkin(String skinName, Skin skin) {
		this.skinsByName.put(skinName, skin);
		
		if (this.autoAttributeId) {
			this.attributeSkinId(skin, this.sequence++);
		}
	}
	
	public Skin loadSkin(String skinName) throws SkinException {
		String fileName = this.directory + "/" + skinName + ".skin";
		
		return loadSkin(skinName, fileName);
	}
	
	public Skin loadSkin(String skinName, String fileName) throws SkinException {
		Skin skin = this.skinsByName.get(skinName);
		
		if (skin == null) {
			skin = this.loadSkinFromFilename(fileName);
			
			this.registerSkin(skinName, skin);
		}
		
		return skin;
	}
	
	public Skin loadSkin(String skinName, short id) throws SkinException {
		Skin skin = this.loadSkin(skinName);
		
		this.attributeSkinId(skin, id);
		
		return skin;
	}
	
	public Skin getSkin(String skinName) throws SkinException {
		Skin skin = this.skinsByName.get(skinName);
		
		if (skin == null) {
			throw new SkinException("Skin " + skinName + " not loaded");
		}
		
		return skin;
	}
	
	public Skin getSkin(int id) throws SkinException {
		Skin skin = this.skinsById.get(id);
		
		if (skin == null) {
			throw new SkinException("Skin with id " + id + " not loaded");
		}
		
		return skin;
	}
	
	public short getSkinIdForSkinName(String skinName) throws SkinException {
		Skin skin = this.getSkin(skinName);

		return skin.getId();
	}
	
	public KView createView(String skinName) throws SkinException {
		Skin skin = this.getSkin(skinName);
		
		return skin.createView();
	}
	
	public KView createView(int id) throws SkinException {
		Skin skin = this.getSkin(id);
		
		KView view = skin.createView();
		
		return view;
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public String getDirectory() {
		return this.directory;
	}
	
	public void setDirectory(String directory) {
		if (directory == null) {
			directory = ".";
		}
		
		while (directory.endsWith("/")) {
			directory = directory.substring(0, directory.length() - 1);
		}
		
		this.directory = directory;
	}
	
	public void setAutoAttributeId(boolean autoAttributeId) {
		this.autoAttributeId = autoAttributeId;
	}
}
