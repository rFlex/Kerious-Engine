/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// Entity.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 5:23:58 PM
////////

package net.kerious.engine.entity;

import me.corsin.javatools.misc.PoolableImpl;
import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.skin.SkinException;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.view.View;
import net.kerious.engine.world.World;

@SuppressWarnings("rawtypes")
public class Entity<EntityModelType extends EntityModel, ViewType extends View> extends PoolableImpl implements TemporaryUpdatable {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Entity parentEntity;
	private EntityManager entityManager;
	private EntityModelType model;
	private World world;
	private ViewType view;
	private int currentSkinId;
	private boolean shouldBeRemoved;
	private boolean destroyWhenRemoved;

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public Entity() {
		
	}

	////////////////////////
	// METHODS
	////////////////
	
	/**
	 * Updated every frame. This is where you should update the entity logic
	 * @param deltaTime
	 */
	public void update(float deltaTime) {
		// Update entity logic
	}
	
	/**
	 * Called when the Entity has just been created and a model has been set for the first time
	 */
	public void buildFromModel() {
		// Build entity from the set model
	}
	
	/**
	 * This is where the Entity decides to add its view to the world. The basic implementation adds
	 * the view to the root view of the world if they don't have a parentEntity. If they have a parent entity
	 * they are added to the view of the parent entity
	 * You can (and should) override
	 * this method to implement a better behavior. Entities are totally free to add the view wherever they want 
	 * @param view
	 * @param world
	 */
	protected void addViewToWorld(ViewType view, World world) {
		if (this.parentEntity != null) {
			View parentView = this.parentEntity.getView();
			
			if (parentView != null) {
				parentView.addView(view);
			} else {
				world.getEngine().getConsole().print("ERROR: Attempted to add a view from an Entity to a parent entity that does not have a view");
			}
		} else {
			View worldView = world.getView();
			
			if (worldView != null) {
				worldView.addView(view);
			} else {
				world.getEngine().getConsole().print("ERROR: Attempted to add a view from an Entity to a world that doesn't have a view");
			}
		}
	}
	
	protected void updateView(ViewType view, EntityModelType model) {
		view.setFrame(model.getX(), model.getY(), model.getWidth(), model.getHeight());
		view.setPosition(model.getX(), model.getY());
	}
	
	@SuppressWarnings("unchecked")
	final private ViewType createViewFromSkinId(int skinId) {
		if (skinId == 0) {
			return null;
		}
		
		try {
			View view = this.world.getSkinManager().createView(skinId);
			try {
				return (ViewType)view;
			} catch (ClassCastException e2) {
				throw new SkinException("Mismatching entity view types");
			}
		} catch (SkinException e) {
			this.world.getEngine().getConsole().print("ERROR: Failed to create view for entity: " + e.getMessage());
		}
		
		return null;
	}
	
	final private void updateSkin(EntityModelType model) {
		if (model != null) {
			if (this.world != null) {
				if (this.world.isRenderingEnabled()) {
					int skinId = this.model.getSkinId();
					// Skin has changed
					if (this.currentSkinId != skinId) {
						this.currentSkinId = skinId;
						this.setView(this.createViewFromSkinId(skinId));
					} else {
						if (this.view != null) {
							this.updateView(this.view, model);
						}
					}
				}
			}
		} else {
			this.setView(null);
		}
	}
	
	final private void updateParent(EntityModelType model) {
		int parentId = 0;
		
		if (model != null) {
			parentId = model.getParentId();
		}
		
		EntityModel parentModel = this.parentEntity != null ? this.parentEntity.getModel() : null;
				
		if (parentId == 0 || parentModel == null) {
			this.setParentEntity(null);
		} else {
			if (parentModel.getId() != parentId) {
				this.setParentEntity(this.entityManager.getEntity(parentId));
			}
		}
	}
	
	/**
	 * Signal to the entity that the model has changed
	 */
	public void modelChanged() {
		EntityModelType model = this.model;
		
		this.updateSkin(model);
		this.updateParent(model);
	}
	
	public void parentChanged() {
		
	}
	
	/**
	 * Called when the view has changed
	 */
	public void viewChanged() {
		
	}
	
	/**
	 * Signal to the entity that it has been removed from the world
	 */
	public void removedFromWorld() {
		if (this.destroyWhenRemoved) {
			this.destroyFromEntityManager();
		}
	}
	
	/**
	 * Signal to the entity that it has been added to the world
	 */
	public void addedToWorld() {
		this.updateSkin(this.model);
	}
	
	/**
	 * Ask the world to remove the entity
	 */
	public void removeFromWorld() {
		this.shouldBeRemoved = true;
	}
	
	/**
	 * Destroy the entity and release it. If the entity is in a world, it will be removed from it first
	 */
	public void destroy() {
		if (this.world != null) {
			this.destroyWhenRemoved = true;
			this.removeFromWorld();
		} else {
			this.destroyFromEntityManager();
		}
	}
	
	final private void destroyFromEntityManager() {
		if (this.entityManager != null) {
			this.entityManager.destroyEntity(this);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.currentSkinId = 0;
		this.destroyWhenRemoved = false;
		this.shouldBeRemoved = false;
		this.setView(null);
	}
	
	public void setSkin(String skinName) {
		if (this.world != null) {
			try {
				int skinId = this.world.getSkinManager().getSkinIdForSkinName(skinName);
				this.getModel().setSkinId(skinId);
				this.modelChanged();
			} catch (SkinException e) {
				this.world.getEngine().getConsole().print("ERROR: Unable to set skin: " + e.getMessage());
			}
		} else {
			System.err.println("ERROR: Attempted to set a skin to an entity which is not in a world");
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	/**
	 * Change the Entity's position and update the model
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y) {
		this.model.setX(x);
		this.model.setY(y);
		
		this.modelChanged();
	}
	
	public EntityModelType getModel() {
		return this.model;
	}
	
	public void setModel(EntityModelType model) {
		this.model = model;
		this.modelChanged();
	}

	public ViewType getView() {
		return view;
	}

	public void setView(ViewType view) {
		if (this.view != view) {
			if (this.view != null) {
				this.view.removeFromParentView();
				this.view.release();
			}
			
			this.view = view;
			
			if (view != null) {
				if (this.world != null) {
					this.addViewToWorld(view, this.world);
				}
				
				if (this.model != null) {
					this.updateView(view, this.model);
				}
			}
			this.viewChanged();
		}
	}

	@Override
	public boolean hasExpired() {
		return this.shouldBeRemoved;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Entity getParentEntity() {
		return parentEntity;
	}

	public void setParentEntity(Entity parentEntity) {
		if (this.parentEntity != parentEntity) {
			this.parentEntity = parentEntity;
			
			this.parentChanged();
		}
	}
	
}
