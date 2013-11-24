/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// Entity.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 5:23:58 PM
////////

package net.kerious.engine.entity;

import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.skin.SkinException;
import net.kerious.engine.utils.Controller;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.view.View;
import net.kerious.engine.world.World;

@SuppressWarnings("rawtypes")
public abstract class Entity<EntityModelType extends EntityModel, ViewType extends View>
								extends Controller<EntityModelType> implements TemporaryUpdatable {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Entity parentEntity;
	private EntityManager entityManager;
	private World world;
	private ViewType view;
	private int currentSkinId;
	private boolean shouldBeRemoved;
	private boolean destroyWhenRemoved;
	private boolean worldRenderingEnabled;

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
	public abstract void update(float deltaTime);
	
	/**
	 * Called when the Entity has just been created and a model has been set for the first time
	 */
	public abstract void initialize();
	
	/**
	 * This is where the Entity decides to add its view to the world. The basic implementation adds
	 * the view to the root view of the world if they don't have a parentEntity. If they have a parent entity
	 * they are added to the view of the parent entity
	 * You can (and should) override
	 * this method to implement a better behavior. Entities are totally free to add the view wherever they want 
	 * @param view
	 * @param world
	 */
	protected void addViewToWorld() {
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
	
	/**
	 * Update the Entity's view so it matches the model
	 */
	public void updateView() {
		if (view != null) {
			view.setFrame(model.x, model.y, model.width, model.height);
			view.setPosition(model.x, model.y);			
		}
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
	
	/**
	 * Update the Entity's skin so it matches its EntityModel
	 */
	final public void updateSkin() {
		if (model != null) {
			if (this.worldRenderingEnabled) {
				int skinId = this.model.skinId;
				// Skin has changed
				if (this.currentSkinId != skinId) {
					this.currentSkinId = skinId;
					this.setView(this.createViewFromSkinId(skinId));
				}
			}
		} else {
			this.setView(null);
		}
	}
	
	/**
	 * Update the Entity's parent so it matches its EntityModel
	 */
	final public void updateParent() {
		int parentId = 0;
		
		if (model != null) {
			parentId = model.parentId;
		}
		
		EntityModel parentModel = this.parentEntity != null ? (EntityModel)this.parentEntity.model : null;
				
		if (parentId == 0 || parentModel == null) {
			this.setParentEntity(null);
		} else {
			if (parentModel.id != parentId) {
				this.setParentEntity(this.entityManager.getEntity(parentId));
			}
		}
	}
	
	/**
	 * Called when the Entity's model has been changed
	 * The contract here is to update the Entity's attribute
	 * so every model's fields match the Entity
	 */
	@Override
	protected void modelChanged() {
		this.updateParent();
		this.updateSkin();
		this.updateView();
	}
	
	/**
	 * Called when the parent Entity changed
	 */
	protected void parentChanged() {
		
	}
	
	/**
	 * Called when the view has changed
	 */
	protected void viewChanged() {
		
	}
	
	/**
	 * Called when the Entity has been removed from the world
	 */
	protected void removedFromWorld() {
		if (this.destroyWhenRemoved) {
			this.destroyFromEntityManager();
		}
	}
	
	/**
	 * Called when the Entity has been added to the world
	 */
	public void addedToWorld() {
		this.updateSkin();
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
	
	/**
	 * Change the entity skin
	 * @param skinName
	 */
	public void setSkin(String skinName) {
		if (this.world != null) {
			try {
				short skinId = this.world.getSkinManager().getSkinIdForSkinName(skinName);
				this.model.skinId = skinId;
				this.updateSkin();
			} catch (SkinException e) {
				this.world.getConsole().printError("Unable to set skin to Entity" + this.model.id + ": " + e.getMessage());
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
		this.model.x = x;
		this.model.y = y;
		
		if (this.worldRenderingEnabled) {
			this.updateView();
		}
	}
	
	public void setSize(float width, float height) {
		this.model.width = width;
		this.model.height = height;
		
		if (this.worldRenderingEnabled) {
			this.updateView();
		}
	}
	
	public int getEntityId() {
		return this.model.id;
	}
	
	public void setFrame(float x, float y, float width, float height) {
		this.model.x = x;
		this.model.y = y;
		this.model.width = width;
		this.model.height = height;
		
		if (this.worldRenderingEnabled) {
			this.updateView();
		}
	}

	public ViewType getView() {
		return view;
	}

	final private void setView(ViewType view) {
		if (this.view != view) {
			if (this.view != null) {
				this.view.removeFromParentView();
				this.view.release();
			}
			
			this.view = view;
			
			if (view != null) {
				if (this.world != null) {
					this.addViewToWorld();
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
		
		this.worldRenderingEnabled = world != null && world.isRenderingEnabled();
		
		if (world == null) {
			this.removedFromWorld();
		}
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
			this.model.parentId = parentEntity != null ? ((EntityModel)parentEntity.model).parentId : 0;
			
			this.parentChanged();
		}
	}
	
}
