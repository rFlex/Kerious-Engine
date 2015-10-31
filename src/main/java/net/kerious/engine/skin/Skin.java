/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world.skin
// Skin.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 13, 2013 at 3:27:06 PM
////////

package net.kerious.engine.skin;

import me.corsin.javatools.misc.Pool;
import net.kerious.engine.view.KView;

public abstract class Skin {

	////////////////////////
	// VARIABLES
	////////////////

	final private Pool<KView> viewPool;
	private short id;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Skin() {
		this.viewPool = new Pool<KView>() {
			@Override
			protected KView instantiate() {
				return Skin.this.newView();
			}
		};
	}

	////////////////////////
	// METHODS
	////////////////
	
	abstract protected KView newView();
	
	public KView createView() throws SkinException {
		KView view = this.viewPool.obtain();
		
		if (view == null) {
			throw new SkinException("No view were created from skin");
		}
		
		return view;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}
}
