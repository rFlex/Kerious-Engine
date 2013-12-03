/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.map
// GameMapView.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Dec 2, 2013 at 6:52:19 PM
////////

package net.kerious.engine.map;

import me.corsin.javatools.misc.NullArgumentException;
import net.kerious.engine.KeriousException;
import net.kerious.engine.entity.Entity;
import net.kerious.engine.view.ScrollableView;
import net.kerious.engine.view.View;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameMapView extends ScrollableView {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Array<MapLayerView> mapLayers;
	final private float baseMetersToPixelsRatio;
	private View chasedView;
	private Entity chasedEntity;
	private float chaseOffsetX;
	private float chaseOffsetY;
	private boolean useViewCenterForOffset;
	private float metersToPixelsRatio;
	private Vector2 outputPosition;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public GameMapView(GameMap gameMap, float baseMetersToPixelsRatio) {
		if (gameMap == null) {
			throw new NullArgumentException("gameMap");
		}
		
		this.mapLayers = gameMap.getLayersView();
		
		if (this.mapLayers == null) {
			throw new KeriousException("The map views on the GameMap were not loaded. ");
		}
		
		this.addViews(this.mapLayers);
		this.baseMetersToPixelsRatio = baseMetersToPixelsRatio;
		this.setMetersToPixelsRatio(baseMetersToPixelsRatio);
		this.outputPosition = new Vector2();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		View chasedView = this.chasedView;
		
		if (chasedView == null && this.chasedEntity != null) {
			chasedView = this.chasedEntity.getView();
		}
		
		if (chasedView != null) {
			float offsetX = this.chaseOffsetX;
			float offsetY = this.chaseOffsetY;
			
			if (this.useViewCenterForOffset) {
				offsetX += chasedView.getWidth() / 2f;
				offsetY += chasedView.getHeight() / 2f;
			}
			
			Vector2 outputPosition = this.outputPosition;
			chasedView.getPositionForViewCoordinate(this, outputPosition);
			
			offsetX += outputPosition.x;
			offsetY += outputPosition.y;
			
			this.setContentOffset(offsetX, offsetY);
		}
	}
	
	public void addViewToMapLayer(View view, int mapLayer) {
		this.mapLayers.get(mapLayer).addView(view);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public Array<MapLayerView> getMapLayers() {
		return this.mapLayers;
	}

	public float getMetersToPixelsRatio() {
		return metersToPixelsRatio;
	}

	public void setMetersToPixelsRatio(float metersToPixelsRatio) {
		this.metersToPixelsRatio = metersToPixelsRatio;
		
		float newScale = metersToPixelsRatio / this.baseMetersToPixelsRatio;
		for (MapLayerView mapLayerView : this.mapLayers) {
			mapLayerView.setSize(mapLayerView.getMapWidth() * newScale, mapLayerView.getMapHeight() * newScale);
		}
	}

	public View getChasedView() {
		return chasedView;
	}

	public void setChasedView(View chasedView) {
		this.chasedView = chasedView;
	}

	public Entity getChasedEntity() {
		return chasedEntity;
	}

	public void setChasedEntity(Entity chasedEntity) {
		this.chasedEntity = chasedEntity;
	}

	public float getChaseOffsetX() {
		return chaseOffsetX;
	}

	public void setChaseOffsetX(float chaseOffsetX) {
		this.chaseOffsetX = chaseOffsetX;
	}
	
	public float getChaseOffsetY() {
		return chaseOffsetY;
	}

	public void setChaseOffsetY(float chaseOffsetY) {
		this.chaseOffsetY = chaseOffsetY;
	}

	public boolean isUseViewCenterForOffset() {
		return useViewCenterForOffset;
	}

	public void setUseViewCenterForOffset(boolean useViewCenterForOffset) {
		this.useViewCenterForOffset = useViewCenterForOffset;
	}

	public void setChaseOffset(float chaseOffsetX, float chaseOffsetY) {
		this.chaseOffsetX = chaseOffsetX;
		this.chaseOffsetY = chaseOffsetY;
	}
}
