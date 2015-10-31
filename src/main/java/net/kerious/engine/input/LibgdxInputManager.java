/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.input
// LibgdxInputManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 8, 2013 at 12:49:35 AM
////////

package net.kerious.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;

public class LibgdxInputManager implements InputManager, InputProcessor {
	
	private class TouchAssociation {
		public TouchResponder touchResponder;
		public int button;
		
	}

	////////////////////////
	// VARIABLES
	////////////////

	final private Vector2 touchLocationInTouchResponderCoords;
	final private IntMap<TouchAssociation> touchLockedResponders;
	final private Pool<TouchAssociation> touchAssociationPool;
	
	private KeyboardResponder keyboardResponder;
	private TouchResponder touchResponder;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public LibgdxInputManager() {
		this.touchLocationInTouchResponderCoords = new Vector2();
		this.touchLockedResponders = new IntMap<TouchAssociation>();
		
		this.touchAssociationPool = new Pool<LibgdxInputManager.TouchAssociation>() {
			protected TouchAssociation newObject() {
				return new TouchAssociation();
			}
		};
	}
	
	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void initialize() {
		Gdx.input.setInputProcessor(this);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (this.keyboardResponder != null) {
			this.keyboardResponder.onKeyDown(keycode);
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (this.keyboardResponder != null) {
			this.keyboardResponder.onKeyUp(keycode);
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		if (this.keyboardResponder != null) {
			this.keyboardResponder.onCharTyped(character);
		}
		
		return false;
	}
	
	final private TouchResponder getBestTouchResponderForLocation(int screenX, int screenY) {
		TouchResponder bestResponder = null;
		
		if (this.touchResponder != null && this.touchResponder.isAvailableForTouchResponding()) {
			bestResponder = this.touchResponder.getBestTouchResponderForLocation(screenX, screenY);
		}
		
		return bestResponder;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenY = Gdx.graphics.getHeight() - screenY;
		
		TouchResponder bestResponder = this.getBestTouchResponderForLocation(screenX, screenY);
		
		if (bestResponder != null) {
			TouchAssociation ta = this.touchAssociationPool.obtain();
			ta.touchResponder = bestResponder;
			ta.button = button;
			
			this.touchLockedResponders.put(pointer, ta);

			bestResponder.convertScreenToTouchResponderLocation(screenX, screenY, this.touchLocationInTouchResponderCoords);
			bestResponder.onTouchDown(pointer, this.touchLocationInTouchResponderCoords.x, this.touchLocationInTouchResponderCoords.y, button);
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenY = Gdx.graphics.getHeight() - screenY;

		TouchAssociation ta = this.touchLockedResponders.get(pointer);
		
		if (ta != null) {
			TouchResponder responder = ta.touchResponder;
			this.touchLockedResponders.remove(pointer);
			
			responder.convertScreenToTouchResponderLocation(screenX, screenY, this.touchLocationInTouchResponderCoords);
			responder.onTouchUp(pointer, this.touchLocationInTouchResponderCoords.x, this.touchLocationInTouchResponderCoords.y, button);
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		screenY = Gdx.graphics.getHeight() - screenY;
		
		TouchAssociation ta = this.touchLockedResponders.get(pointer);
		
		if (ta != null) {
			TouchResponder responder = ta.touchResponder;
			
			responder.convertScreenToTouchResponderLocation(screenX, screenY, this.touchLocationInTouchResponderCoords);
			responder.onTouchDragged(pointer, this.touchLocationInTouchResponderCoords.x, this.touchLocationInTouchResponderCoords.y, ta.button);
		}

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		screenY = Gdx.graphics.getHeight() - screenY;

		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	@Override
	public KeyboardResponder getKeyboardResponder() {
		return this.keyboardResponder;
	}

	@Override
	public void setKeyboardResponder(KeyboardResponder keyboardResponder) {
		if (keyboardResponder != this.keyboardResponder) {
			final KeyboardResponder oldResponder = this.keyboardResponder;
			
			this.keyboardResponder = keyboardResponder;
			
			if (oldResponder != null) {
				oldResponder.onResignedResponder();
			}
			
			if (keyboardResponder != null) {
				keyboardResponder.onBecameResponder();
			}
		}
	}

	@Override
	public void setTouchResponder(TouchResponder touchResponder) {
		this.touchResponder = touchResponder;
	}

	@Override
	public TouchResponder getTouchResponder() {
		return this.touchResponder;
	}
}
