/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.entity
// EntityManager.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 12, 2013 at 11:23:06 PM
////////

package net.kerious.engine.entity;

import net.kerious.engine.entity.model.EntityModel;
import net.kerious.engine.entity.model.EntityModelCreator;
import net.kerious.engine.world.event.EntityCreatedEvent;
import net.kerious.engine.world.event.EntityDestroyedEvent;
import net.kerious.engine.world.event.Event;
import net.kerious.engine.world.event.EventListener;
import net.kerious.engine.world.event.EventListenerRegisterer;
import net.kerious.engine.world.event.EventManager;
import net.kerious.engine.world.event.Events;

import com.badlogic.gdx.utils.IntMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EntityManager implements EntityModelCreator, EventListenerRegisterer {
	
	////////////////////////
	// VARIABLES
	////////////////

	final private IntMap<Entity> entities;
	final private IntMap<EntityCreator> entityCreators;
	private EntityManagerListener listener;
	private int sequence;
	private EventManager eventManager;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EntityManager() {
		this.entities = new IntMap<Entity>(64);
		this.entityCreators = new IntMap<EntityCreator>();
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void registerEventListeners(EventManager eventManager) {
		this.eventManager = eventManager;
		
		if (!eventManager.canGenerateEvent()) {
			eventManager.addListener(Events.EntityCreated, new EventListener() {
				public void onEventFired(EventManager eventManager, Event event) {
					EntityCreatedEvent entityCreatedEvent = (EntityCreatedEvent)event;
					createEntity(entityCreatedEvent.entityType, entityCreatedEvent.entityId);
				}					
			});
			eventManager.addListener(Events.EntityDestroyed, new EventListener() {
				public void onEventFired(EventManager eventManager, Event event) {
					EntityDestroyedEvent entityDestroyedEvent = (EntityDestroyedEvent)event;
					destroyEntity(entityDestroyedEvent.entityId);
				}
			});
		}
	}
	
	public void preload(int entityType, int quantity) throws EntityException {
		this.getEntityCreator(entityType).preload(quantity);
	}
	
	public void registerEntity(int entityType, Class<?> entityClass, Class<?> entityModelClass) {
		this.registerEntity(entityType, new ReflectionEntityCreator(entityClass, entityModelClass));
	}
	
	public void registerEntity(int entityType, EntityCreator entityCreator) {
		entityCreator.setEntityType((byte)entityType);
		
		this.entityCreators.put(entityType, entityCreator);
	}
	
	final private EntityCreator getEntityCreator(int entityType) throws EntityException {
		EntityCreator entityCreator = this.entityCreators.get(entityType);
		
		if (entityCreator == null) {
			throw new EntityException(entityType, "No entity creator was registered for entityType " + entityType);
		}
		
		return entityCreator;
	}
	
	final private Entity createEntity(EntityCreator entityCreator, EntityModel entityModel) throws EntityException {
		Entity entity = entityCreator.createEntity(entityModel);
		entity.setEntityManager(this);
		
		this.entities.put(entityModel.id, entity);
		
		if (this.eventManager.canGenerateEvent()) {
			EntityCreatedEvent.createAndFire(this.eventManager, entityModel.id, entityModel.type);
		}
		
		if (this.listener != null) {
			this.listener.onEntityCreated(entity);
		}
		
		return entity;
	}
	
	/**
	 * Create an Entity of type entityType and attribute a generated ID
	 * @param entityType
	 * @return the created Entity
	 */
	public Entity createEntity(int entityType) {
		this.sequence++;
		return this.createEntity(entityType, this.sequence);
	}
	
	/**
	 * Create an Entity of type entityType and attribute the specified entityId
	 * @param entityType
	 * @param entityId
	 * @return the created Entity
	 */
	public Entity createEntity(int entityType, int entityId) {
		EntityCreator entityCreator = this.getEntityCreator(entityType);
		
		EntityModel entityModel = entityCreator.createEntityModel();
		entityModel.id = entityId;
		
		Entity entity = this.createEntity(entityCreator, entityModel);
		
		entityModel.release();
		
		return entity;
	}
	
	public void destroyEntity(int entityId) {
		Entity entity = this.getEntity(entityId);
		
		if (entity != null) {
			this.destroyEntity(entity);
		} else {
			throw new EntityException(0, "No such entity: " + entityId);
		}
	}
	
	public void destroyEntity(Entity entity) {
		if (entity == null) {
			throw new IllegalArgumentException("entity may not be null");
		}
		
		EntityModel model = (EntityModel)entity.getModel();
		
		if (model == null) {
			throw new IllegalArgumentException("the entity doesn't have any model");
		}
		
		int entityId = model.id;
		
		this.entities.remove(entityId);
		
		if (this.eventManager.canGenerateEvent()) {
			EntityDestroyedEvent.createAndFire(this.eventManager, entityId);
		}
		
		entity.release();
		
		if (this.listener != null) {
			this.listener.onEntityDestroyed(entityId);
		}
	}
	
	public EntityModel createEntityModel(byte entityType) throws EntityException {
		EntityCreator entityCreator = this.getEntityCreator(entityType);
		
		return entityCreator.createEntityModel();
	}
	
	public void updateEntity(EntityModel entityModel) throws EntityException {
		Entity entity = this.getEntity(entityModel.id);
		
		if (entity != null) {
			entity.setModel(entityModel);
		}
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public Entity getEntity(int entityId) {
		return this.entities.get(entityId);
	}

	public EntityManagerListener getListener() {
		return listener;
	}

	public void setListener(EntityManagerListener listener) {
		this.listener = listener;
	}
	
	public Iterable<Entity> getEntites() {
		return this.entities.values();
	}
}
