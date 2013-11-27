/////////////////////////////////////////////////
// Project : killing-sight
// Package : com.kerious.killingsight.world.tmx
// CollisionHandler.java 
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Oct 11, 2012 at 2:03:19 AM
////////

package com.kerious.framework.collisions;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.kerious.framework.world.entities.Entity;

public class CollisionHandler {
	
	////////////////////////
	// VARIABLES
	////////////////

	final private LinkedList<ICollisionable>[][] trackedEntities;
	final private Rectangle entityRectangle;
	final private Rectangle comparedRectangle;
	final private float tileSize;
	final private int height;
	final private int width;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	private static abstract class CollisionAction {
		public ICollisionable collisioned;
		
		abstract void act(LinkedList<ICollisionable> collisions);
	}
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	@SuppressWarnings("unchecked")
	public CollisionHandler(float tileSize, int mapWidth, int mapHeight) {
		this.entityRectangle = new Rectangle();
		this.comparedRectangle = new Rectangle();

		this.tileSize = tileSize;
		this.width = mapWidth;
		this.height = mapHeight;
		
		this.trackedEntities = new LinkedList[mapHeight][mapWidth];
		for (int i = 0; i < mapWidth; i++) {
			for (int j = 0; j < mapHeight; j++) {
				trackedEntities[j][i] = new LinkedList<ICollisionable>();
			}
		}
	}
	
	public CollisionHandler(TiledMap tiledMap) {
		this((float)tiledMap.tileWidth, tiledMap.width, tiledMap.height);
		
		if (tiledMap.objectGroups.size() > 0) {
			TiledObjectGroup group = tiledMap.objectGroups.get(0);
			for (TiledObject object : group.objects) {
				final CollisionRegion region = this.createRegionForObject(object);
				this.addRegion(region);
			}
		}
	}

	////////////////////////
	// METHODS
	////////////////
	
	private CollisionRegion createRegionForObject(TiledObject object) {
		CollisionRegion region = new CollisionRegion(object.x, object.y, object.width, object.height);
		
		region.setY(this.height * this.tileSize - (object.y + object.height));
		
		return region;
	}
	
	public void addRegion(final CollisionRegion region) {
		this.foreach(region, new CollisionAction() {
			
			@Override
			void act(LinkedList<ICollisionable> collisions) {
				collisions.add(region);
				
			}
		});;
	}
	
	public void removeRegion(final CollisionRegion region) {
		this.foreach(region, new CollisionAction() {
			
			@Override
			void act(LinkedList<ICollisionable> collisions) {
				collisions.remove(region);
				
			}
		});;
	}
	
	private final ICollisionable foreach(ICollisionable collision, CollisionAction toDo) {
		// Adding one prevent using the >= operator which is slower on float the > operator
		final float maxX = collision.getX() + collision.getWidth() + 1; 
		final float maxY = collision.getY() + collision.getHeight() + 1;
		
		for (float x = collision.getX(); x < maxX; x += this.tileSize) {
			for (float y = collision.getY(); y < maxY; y += this.tileSize) {
				int posX = (int)(x / this.tileSize);
				int posY = (int)(y / this.tileSize);
				
				if (posX >= 0 && posY >= 0 && posY < this.trackedEntities.length && posX < this.trackedEntities[posY].length) {
					toDo.act(this.trackedEntities[posY][posX]);
				} else {
					toDo.act(null);
				}
				
				if (toDo.collisioned != null) {
					return toDo.collisioned;
				}
			}
		}
		return null;
	}

	public final void removeEntity(final Entity entity) {
		this.foreach(entity, new CollisionAction() {
			
			@Override
			public void act(LinkedList<ICollisionable> collisions) {
				if (collisions != null) {
					collisions.remove(entity);
				}
			}
		});
	}
	
	public final void addEntity(final Entity entity) {
		this.foreach(entity, new CollisionAction() {
			
			@Override
			public void act(LinkedList<ICollisionable> collisions) {
				if (collisions != null) {
					collisions.add(entity);
				}
			}
		});
		entity.setCollisionHandler(this);
	}

	public final void trackEntity(Entity entity) {
		entity.setCollisionHandler(this);
		if (entity.canBeCollisioned()) {
			this.addEntity(entity);
		}
	}
	
	public final void untrackEntity(Entity entity) {
		entity.setCollisionHandler(null);
		if (entity.canBeCollisioned()) {
			this.removeEntity(entity);
		}
	}

	public boolean hasCollisionableAtPosition(int x, int y) {
		for (ICollisionable collisionable : trackedEntities[y][x]) {
			if (collisionable instanceof CollisionRegion) {
				return true;
			}
		}
		return false;
	}
	
	public final ICollisionable checkCollisions(final ICollisionable collision, final ICollisionable owner) {
		entityRectangle.set(collision.getX(), collision.getY(), collision.getWidth(), collision.getHeight());

		final ICollisionable collisionned = this.foreach(collision, new CollisionAction() {
			
			@Override
			public void act(LinkedList<ICollisionable> collisions) {
				if (collisions != null) {
					for (ICollisionable collisionable : collisions) {
						// Skip the owner for preventing suicide
						if (collisionable == owner) {
							continue;
						}
						
						comparedRectangle.set(collisionable.getX(), collisionable.getY(), collisionable.getWidth(), collisionable.getHeight());
						if (Intersector.intersectRectangles(entityRectangle, comparedRectangle) && collision != collisionable) {
							this.collisioned = collisionable;
						}
					}
				} else {
					this.collisioned = collision;
				}
			}
		});

		return collisionned;
	}
	
	public final ICollisionable checkCollisions(final Entity entity) {
		entityRectangle.set(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());

		final ICollisionable collisionned = this.foreach(entity, new CollisionAction() {
			
			@Override
			public void act(LinkedList<ICollisionable> collisions) {
				if (collisions != null) {
					for (ICollisionable collisionable : collisions) {
						// Skip the owner for preventing suicide
						if (collisionable == entity.getParentEntity()) {
							continue;
						}
						
						comparedRectangle.set(collisionable.getX(), collisionable.getY(), collisionable.getWidth(), collisionable.getHeight());
						if (Intersector.intersectRectangles(entityRectangle, comparedRectangle) && entity != collisionable) {
							this.collisioned = collisionable;
						}
					}
				} else {
					this.collisioned = entity;
				}
			}
		});
		
		if (collisionned != null) {
			collisionned.isTouched(entity);
			entity.hasTouched(collisionned);
		}

		return collisionned;
	}

	public ICollisionable isFreeBetween(Vector2 vec1, Vector2 vec2, Entity player, Entity target) {
		if (Math.abs(vec1.x - vec2.x) < Math.abs(vec1.y - vec2.y)) {
			return (isFreeBetweenVertical(vec1, vec2, player, target));
		}
		
		return (isFreeBetweenHorizontal(vec1, vec2, player, target));
	}

	public ICollisionable isFreeBetween(float width, Entity player, Entity target) {
		Vector2 vec1 = new Vector2();
		Vector2 vec2 = new Vector2();
		ICollisionable res = null;

		if (Math.abs(player.getX() - target.getX()) < Math.abs(player.getY() - target.getY())) {
			vec1.x = player.getX() + player.getWidth() / 2 - width / 2;
			vec1.y = player.getY() + player.getHeight() / 2;
			vec2.x = target.getX() + target.getWidth() / 2 - width / 2;
			vec2.y = target.getY() + target.getHeight() / 2;
			res = isFreeBetweenVertical(vec1, vec2, player, target);
			if (res != null)
				return res;
			vec1.x += width;
			vec2.x += width;
			res = isFreeBetweenVertical(vec1, vec2, player, target);
		} else {
			vec1.x = player.getX() + player.getWidth() / 2;
			vec1.y = player.getY() + player.getHeight() / 2 - width / 2;
			vec2.x = target.getX() + target.getWidth() / 2;
			vec2.y = target.getY() + target.getHeight() / 2 - width / 2;
			res = isFreeBetweenHorizontal(vec1, vec2, player, target);
			if (res != null)
				return res;
			vec1.y += width;
			vec2.y += width;
			res = isFreeBetweenHorizontal(vec1, vec2, player, target);
		}
		return res;
	}
	
	private ICollisionable isFreeBetweenVertical(Vector2 vec1, Vector2 vec2, Entity player, Entity target) {
		float yBegin;
		float yEnd;
		float step;

		if (vec1.y < vec2.y) {
			yBegin = vec1.y;
			yEnd = vec2.y;
		} else {
			yBegin = vec2.y;
			yEnd = vec1.y;
		}

		float x = vec1.x / tileSize;
		int intYEnd = (int) (yEnd / tileSize);

		step = (vec2.x - vec1.x) / (Math.abs(vec1.y - vec2.y) / 64.0f);
		step /= 64;

		for (int y = (int) (yBegin / tileSize); y < intYEnd; y++) {
			if (y < 0 || y >= this.height || x < 0 || x >= this.width) {
				return player;
			}
			if (!this.trackedEntities[y][(int) x].isEmpty() &&
				!this.listOnlyContains(this.trackedEntities[y][(int) x], player, target))
				return this.trackedEntities[y][(int) x].getFirst();
			x += step;
		}
		return null;
	}

	private boolean listOnlyContains(LinkedList<ICollisionable> linkedList, Entity player, Entity target) {
		for (ICollisionable coll : linkedList) {
			if (coll != player && coll != target)
				return false;
		}
		return true;
	}

	private ICollisionable isFreeBetweenHorizontal(Vector2 vec1, Vector2 vec2, Entity player, Entity target) {
		float step;
		int	offset;

		if (vec1.x < vec2.x) {
			offset = 1;
		} else {
			offset = -1;
		}
		
		float y = vec1.y / tileSize;
 
		step = (vec2.y - vec1.y) / (Math.abs(vec1.x - vec2.x) / 64.0f);
		step /= 64;
		for (int x = (int) (vec1.x / tileSize); x != (int) (vec2.x / tileSize); x += offset) {
			if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
				return player;
			}
			
			if (!this.trackedEntities[(int) y][x].isEmpty() &&
				!this.listOnlyContains(trackedEntities[(int) y][x], player, target)) {
				return this.trackedEntities[(int) y][x].getFirst();
			}
			y += step;
		}
		return null;
	}

	public LinkedList<ICollisionable>[][] getCollisionMap() {
		return this.trackedEntities;
	}

	public boolean isFree(Rectangle frame) {
		entityRectangle.set(frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight());

		for (float x = frame.getX() - tileSize; x <= frame.getX() + frame.getWidth(); x = x + this.tileSize) {
			for (float y = frame.getY(); y <= frame.getY() + frame.getHeight(); y += this.tileSize) {
				final int posX = (int)(x / this.tileSize);
				final int posY = (int)(y / this.tileSize);
				if (posX < 0 || posY < 0 || posX > this.tileSize || posY > this.tileSize) {
					return false;
				}
				
				for (ICollisionable collisionable : trackedEntities[posY][posX]) {
					comparedRectangle.set(collisionable.getX(), collisionable.getY(), collisionable.getWidth(), collisionable.getHeight());
					if (Intersector.intersectRectangles(entityRectangle, comparedRectangle)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void drawCollisionRegions(ShapeRenderer debugRenderer) {
		debugRenderer.setColor(Color.RED);
		debugRenderer.begin(ShapeType.Rectangle);

		LinkedList<ICollisionable> collisions = new LinkedList<ICollisionable>();
		for (int y = 0; y < this.trackedEntities.length; y++) {
			for (int x = 0; x < this.trackedEntities[y].length; x++) {
				for (ICollisionable collision : this.trackedEntities[y][x]) {
					if (!collisions.contains(collision)) {
						collisions.add(collision);
					}
				}
			}
		}
		
		for (ICollisionable collision : collisions) {
			debugRenderer.rect(collision.getX(), collision.getY(), collision.getWidth(), collision.getHeight());
		}
		
		
		debugRenderer.end();
	}
	
	public void drawGrid(ShapeRenderer renderer) {
		renderer.begin(ShapeType.Rectangle);
		
		for (int y = 0; y < this.trackedEntities.length; y++) {
			for (int x = 0; x < this.trackedEntities[y].length; x++) {
				if (this.trackedEntities[y][x].size() > 0) {
					renderer.setColor(Color.RED);
				} else {
					renderer.setColor(Color.BLUE);
				}
				renderer.rect(x * this.tileSize, y * this.tileSize, this.tileSize, this.tileSize);
			}
		}
		
		renderer.end();
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
