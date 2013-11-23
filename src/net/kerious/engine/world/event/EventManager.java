/////////////////////////////////////////////////
// Project : Kerious Engine
// Package : net.kerious.engine.world.event
// EventFactory.java
//
// Author : Simon CORSIN <simoncorsin@gmail.com>
// File created on Nov 20, 2013 at 9:00:55 PM
////////

package net.kerious.engine.world.event;

import me.corsin.javatools.misc.NullArgumentException;
import me.corsin.javatools.misc.Pool;
import me.corsin.javatools.misc.ReflectionPool;
import net.kerious.engine.utils.FactoryManager;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.SnapshotArray;

public class EventManager extends FactoryManager implements EventCreator {

	////////////////////////
	// VARIABLES
	////////////////
	
	private IntMap<SnapshotArray<EventListener>> listeners;
	private IntSet firedEvents;
	private EventManagerListener listener;
	private boolean autoAttributeId;
	private int sequence;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public EventManager() {
		this.listeners = new IntMap<SnapshotArray<EventListener>>();
		this.firedEvents = new IntSet();
		this.registerBuiltInEvents();
	}

	////////////////////////
	// METHODS
	////////////////
	
	private void registerBuiltInEvents() {
		this.registerEventType(Events.EntityDestroyed, EntityDestroyedEvent.class);
	}
	
	public <T extends Event> void registerEventType(byte eventType, Class<T> eventClass) {
		ReflectionPool<T> pool = new ReflectionPool<T>(eventClass);
		
		// Preload the items to avoid the slow instantiate time of the reflection
		pool.preload(10);
		
		this.registerEventType(eventType, pool);
	}
	
	public <T extends Event> void registerEventType(byte eventType, Pool<T> pool) {
		this.registerFactory(eventType, pool);
	}
	
	public void unregisterEventType(byte eventType) {
		this.unregisterFactory(eventType);
	}
	
	final private SnapshotArray<EventListener> getListeners(byte eventType, boolean createIfDoesntExist) {
		SnapshotArray<EventListener> listeners = this.listeners.get(eventType);
		
		if (listeners == null) {
			if (createIfDoesntExist) {
				listeners = new SnapshotArray<EventListener>(true, 10, EventListener.class);
				this.listeners.put(eventType, listeners); 
			}
		}
		
		return listeners;
	}
	
	public void addListener(byte eventType, EventListener eventListener) {
		if (eventListener == null) {
			throw new NullArgumentException("eventListener");
		}
		
		SnapshotArray<EventListener> listeners = this.getListeners(eventType, true);
		listeners.add(eventListener);
	}
	
	public void removeListener(byte eventType, EventListener eventListener) {
		if (eventListener == null) {
			throw new NullArgumentException("eventListener");
		}
		
		SnapshotArray<EventListener> listeners = this.getListeners(eventType, false);
		if (listeners != null) {
			listeners.removeValue(eventListener, true);
		}
	}

	@Override
	public Event createEvent(byte eventType) {
		Event event =  (Event)this.createObject(eventType);
		
		if (this.autoAttributeId) {
			this.sequence++;
			event.id = this.sequence;
		}
		
		return event;
	}
	
	public void fireEvent(Event event) {
		if (event == null) {
			throw new NullArgumentException("event");
		}
		
		// Check if the event was already processed
		if (this.firedEvents.contains(event.id)) {
			return;
		}
		
		this.firedEvents.add(event.id);
		
		if (this.listener != null) {
			this.listener.onEventFired(this, event);
		}
		
		SnapshotArray<EventListener> listeners = this.getListeners(event.type, false);
		
		if (listeners != null) {
			EventListener[] eventListenersArray = listeners.begin();
			
			for (int i = 0, length = listeners.size; i < length; i++) {
				final EventListener eventListener = eventListenersArray[i];
				
				eventListener.onEventFired(this, event);
			}
			
			listeners.end();
		}
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public boolean isAutoAttributeId() {
		return autoAttributeId;
	}

	public void setAutoAttributeId(boolean autoAttributeId) {
		this.autoAttributeId = autoAttributeId;
	}

	public EventManagerListener getListener() {
		return listener;
	}

	public void setListener(EventManagerListener listener) {
		this.listener = listener;
	}
}
