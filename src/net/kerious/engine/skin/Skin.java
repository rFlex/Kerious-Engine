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
import net.kerious.engine.view.View;

public abstract class Skin {

	////////////////////////
	// VARIABLES
	////////////////

	final private Pool<View> viewPool;
	private int id;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Skin() {
		this.viewPool = new Pool<View>() {
			@Override
			protected View instantiate() {
				return Skin.this.newView();
			}
		};
	}

	////////////////////////
	// METHODS
	////////////////
	
	abstract protected View newView();
	
	public View createView() throws SkinException {
		View view = this.viewPool.obtain();
		
		if (view == null) {
			throw new SkinException("No view were created from skin");
		}
		
		return view;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
