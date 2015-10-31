/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// Entity.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 5:23:58 PM
////////

package net.kerious.engine.entity;

import net.kerious.engine.KeriousException;
import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.player.Player;
import net.kerious.engine.skin.SkinException;
import net.kerious.engine.utils.Controller;
import net.kerious.engine.utils.TemporaryUpdatable;
import net.kerious.engine.view.KView;
import net.kerious.engine.world.GameWorld;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public abstract class Entity extends Controller<EntityModel> implements TemporaryUpdatable {

	////////////////////////
	// VARIABLES
	////////////////
	
	private Player player;
	private Entity parentEntity;
	private EntityManager entityManager;
	private GameWorld world;
	private KView view;
	private int currentPlayerId;
	private int currentSkinId;
	private boolean shouldBeRemoved;
	private boolean destroyWhenRemoved;
	private boolean worldRenderingEnabled;
	private Body physicsBody;

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
			KView parentView = this.parentEntity.getView();
			
			if (parentView != null) {
				parentView.addView(view);
			} else {
				world.getEngine().getConsole().print("Attempted to add a view from an Entity to a parent entity that does not have a view");
			}
		} else {
			KView worldView = world.getView();
			
			if (worldView != null) {
				worldView.addView(view);
			} else {
				world.getEngine().getConsole().printError("Attempted to add a view from an Entity to a world that doesn't have a view");
			}
		}
	}
	
	/**
	 * Update the Entity's view so it matches the model
	 */
	public void updateView() {
		GameWorld world = this.world;
		if (view != null && world != null) {
			float pointsSize = world.getMetersToPixelsRatio();
			view.setFrame(model.x * pointsSize, model.y * pointsSize, model.width * pointsSize, model.height * pointsSize);
		}
	}
	
	final private KView createViewFromSkinId(int skinId) {
		if (skinId == 0) {
			return null;
		}
		
		try {
			KView view = this.world.getSkinManager().createView(skinId);
			try {
				return (KView)view;
			} catch (ClassCastException e2) {
				throw new SkinException("Mismatching entity view types");
			}
		} catch (SkinException e) {
			this.world.getEngine().getConsole().print("ERROR: Failed to create view for entity: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Update the Entity's player so it matches its EntityModel
	 */
	final public void updatePlayer() {
		if (this.model != null) {
			int playerId = this.model.playerId;
			
			if (this.currentPlayerId != playerId) {
				Player player = this.world.getPlayerManager().getPlayer(playerId);
				this.setPlayer(player);
			}
		} else {
			this.setPlayer(null);
		}
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
		this.matchPhysicsWithModel();
		this.updatePlayer();
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
	 * Changed when the owner player changed
	 */
	protected void playerChanged() {
		
	}
	

	public void addedToWorld() {
		this.updateSkin();
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
	 * Ask the world to remove the entity
	 */
	public void removeFromWorld() {
		this.shouldBeRemoved = true;
	}
	
	/**
	 * Destroy the entity and release it. If the entity is in a world, it will be removed from it first
	 */
	public void destroy() {
		this.setPlayer(null);
		
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
		this.setPlayer(null);
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
	
	private BodyDef tmpBodyDef = new BodyDef();
	
	/**
	 * Create the physics body. The default implementation create a rectangle shape
	 * that exactly fits the Entity.
	 * Do NOT call this method directly if you want to add a physics body to the Entity,
	 * use addPhysicsBody instead.
	 * @param physicsWorld
	 * @return
	 */
	protected Body createPhysicsBodyWithWorld(com.badlogic.gdx.physics.box2d.World physicsWorld) {
		float x = this.model.x;
		float y = this.model.y;
		float widthRadius = this.model.width / 2f;
		float heightRadius = this.model.height / 2f;
		
		tmpBodyDef.type = BodyType.DynamicBody;
		tmpBodyDef.position.x = x;
		tmpBodyDef.position.y = y;
		tmpBodyDef.fixedRotation = true;
		
		Body body = physicsWorld.createBody(tmpBodyDef);

		tmpBodyDef.position.x = widthRadius;
		tmpBodyDef.position.y = heightRadius;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(widthRadius, heightRadius, tmpBodyDef.position, 0);
		
		body.createFixture(shape, 1);
		
		shape.dispose();
		
		return body;
	}
	
	final public void removePhysicsBody() {
		if (this.physicsBody != null && this.world != null) {
			final com.badlogic.gdx.physics.box2d.World physicsWorld = this.world.getPhysicsWorld();
			physicsWorld.destroyBody(this.physicsBody);
			this.physicsBody = null;
		}
	}

	/**
	 * Create a physics body from the Entity data and add it to the world
	 * This method will call createPhysicsBodyWithWorld and expects it to
	 * return the newly created Body
	 */
	final public void addPhysicsBody() {
		this.removePhysicsBody();
		
		GameWorld world = this.world;
		
		if (world != null) {
			World physicsWorld = world.getPhysicsWorld();
			this.physicsBody = this.createPhysicsBodyWithWorld(physicsWorld);
			
			if (this.physicsBody != null) {
				this.physicsBody.setUserData(this);
			}
		} else {
			throw new KeriousException("Cannot create physics body when the Entity is not in the World");
		}
	}
	
	/**
	 * Make the physics position be the same as the model
	 * On an Entity that doesn't have a physics body, this method
	 * does nothing
	 */
	final public void matchPhysicsWithModel() {
		Body body = this.physicsBody;
		GameWorld world = this.world;
		
		if (body != null && world != null) {
			body.setTransform(this.model.x, this.model.y, body.getAngle());
		}
	}
	
	/**
	 * Make the model position be the same as the physics.
	 * On an Entity that doesn't have a physics body, this method
	 * does nothing
	 */
	final public void matchModelWithPhysics() {
		Body body = this.physicsBody;
		GameWorld world = this.world;
		
		if (body != null && world != null) {
			Vector2 position = body.getPosition();
			this.model.x = position.x;
			this.model.y = position.y;
		}
	}

	/**
	 * Called when the Entity has started the contact with another entity 
	 * @param otherEntity the other Entity which the Entity has contacted with
	 * @param entityFixture the Fixture responsible for the contact on this entity
	 * @param otherEntityFixture the Fixture responsible for the contact on the other entity
	 */
	public void beginContact(Entity otherEntity, Fixture entityFixture, Fixture otherEntityFixture) {
		
	}
	
	/**
	 * Called when the Entity has ended the contact with another entity 
	 * @param otherEntity the other Entity which the Entity has contacted with
	 * @param entityFixture the Fixture responsible for the contact on this entity
	 * @param otherEntityFixture the Fixture responsible for the contact on the other entity
	 */
	public void endContact(Entity otherEntity, Fixture entityFixture, Fixture otherEntityFixture) {
		
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public boolean hasPhysicsBody() {
		return this.physicsBody != null;
	}
	
	/**
	 * Change the Entity's position, update the model
	 * and update the physics if any
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y) {
		this.model.x = x;
		this.model.y = y;

		this.matchPhysicsWithModel();
		
		if (this.worldRenderingEnabled) {
			this.updateView();
		}
	}
	
	/**
	 * Translate the Entity's position, update the model
	 * and update the physics if any
	 * @param x
	 * @param y
	 */
	public void translate(float offsetX, float offsetY) {
		this.model.x += offsetX;
		this.model.y += offsetY;
		
		this.matchPhysicsWithModel();
		
		if (this.worldRenderingEnabled) {
			this.updateView();
		}
	}
	
	/**
	 * Change the entity size and update the model
	 * This method has no impact on the physics.
	 * If you really need to change the Entity size
	 * after the physics body was created, you need
	 * to remove the physics body and create one again
	 * @param width
	 * @param height
	 */
	public void setSize(float width, float height) {
		this.model.width = width;
		this.model.height = height;
		
		if (this.worldRenderingEnabled) {
			this.updateView();
		}
	}
	
	public int getId() {
		return this.model.id;
	}
	
	public byte getType() {
		return this.model.type;
	}
	
	public void setFrame(Rectangle frame) {
		this.setFrame(frame.x, frame.y, frame.width, frame.height);
	}
	
	/**
	 * Change the Entity's frame, update the model
	 * and update the physics if any
	 * @param x
	 * @param y
	 */
	public void setFrame(float x, float y, float width, float height) {
		this.model.x = x;
		this.model.y = y;
		this.model.width = width;
		this.model.height = height;
		
		this.matchPhysicsWithModel();
		
		if (this.worldRenderingEnabled) {
			this.updateView();
		}
	}

	public KView getView() {
		return view;
	}

	final private void setView(KView view) {
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

	public GameWorld getWorld() {
		return world;
	}

	public void setWorld(GameWorld world) {
		this.world = world;
		
		this.worldRenderingEnabled = world != null && world.isRenderingEnabled();
		
		if (world == null) {
			this.removedFromWorld();
		} else {
			this.addedToWorld();
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
			this.model.parentId = parentEntity != null ? (parentEntity.model).parentId : 0;
			
			this.parentChanged();
		}
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * Set the Player that owns this Entity
	 * @param player
	 */
	public void setPlayer(Player player) {
		if (this.player != player) {
			Player oldPlayer = this.player;
			
			this.player = player;
			this.model.playerId = player != null ? player.getId() : 0;
			this.currentPlayerId = this.model.playerId;
			
			if (oldPlayer != null) {
				oldPlayer.lostEntityOwnership(this);
			}
			if (player != null) {
				player.gainedEntityOwnership(this);
			}
			
			this.playerChanged();
		}
	}

	public Body getPhysicsBody() {
		return physicsBody;
	}
	
	/**
	 * Return the physics friction
	 * Make sense and can be used as convenience if the physics body
	 * doesn't have more than one fixture
	 * @return 
	 */
	public float getFriction() {
		Array<Fixture> fixtures = null;

		if (this.physicsBody != null) {
			fixtures = this.physicsBody.getFixtureList();
		}
		
		return fixtures != null && fixtures.size > 0 ? fixtures.items[0].getFriction() : 0;
	}
	
	/**
	 * Set the physics friction of every fixtures in the physics body
	 * @param friction
	 */
	public void setFriction(float friction) {
		if (this.physicsBody != null) {
			Array<Fixture> fixtures = this.physicsBody.getFixtureList();
			for (int i = 0, length = fixtures.size; i < length; i++) {
				fixtures.get(i).setFriction(friction);
			}
		}
	}
	
	/**
	 * Return the physics density of the first fixture in the physics body
	 * Make sense and can be used as convenience if the physics body
	 * doesn't have more than one fixture
	 * @return
	 */
	public float getDensity() {
		Array<Fixture> fixtures = null;

		if (this.physicsBody != null) {
			fixtures = this.physicsBody.getFixtureList();
		}
		
		return fixtures != null && fixtures.size > 0 ? fixtures.items[0].getDensity() : 0;
	}
	
	/**
	 * Set the density of every fixtures in the physics body
	 * @param density
	 */
	public void setDensity(float density) {
		if (this.physicsBody != null) {
			Array<Fixture> fixtures = this.physicsBody.getFixtureList();
			for (int i = 0, length = fixtures.size; i < length; i++) {
				fixtures.get(i).setDensity(density);
			}
			this.physicsBody.resetMassData();
		}
	}
}
