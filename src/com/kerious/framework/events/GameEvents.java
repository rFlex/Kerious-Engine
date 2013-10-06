package com.kerious.framework.events;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.utils.Pool.ObjectCreator;

public class GameEvents {

	////////////////////////
	// VARIABLES
	////////////////

	private static ArrayList<ObjectCreator<GameEvent>> classAssociation;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	////////////////////////
	// METHODS
	////////////////

	private static void checkInit() {
		if (classAssociation == null) {
			classAssociation = new ArrayList<ObjectCreator<GameEvent>>();
			
			declareEvent(EntityRegisterEvent.class);
			declareEvent(EntityUnregisterEvent.class);
			declareEvent(UserConnectedEvent.class);
			declareEvent(UserDisconnectedEvent.class);
			declareEvent(WorldLoadEvent.class);
			declareEvent(ChatMessageEvent.class);
			declareEvent(ConsoleInstructionEvent.class);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void declareEvent(Class<?> eventClass) {
		checkInit();
		
		try {
			Field field = eventClass.getField("byteIdentifier");
			byte byteIdentifier = field.getByte(null);
			
			while (classAssociation.size() <= byteIdentifier) {
				classAssociation.add(null);
			}
			
			boolean found = false;
			
			for (Class<?> declaredClass : eventClass.getDeclaredClasses()) {
				if (declaredClass.getSimpleName().equals("Instancier")) {
					if (classAssociation.get(byteIdentifier) != null) {
						throw new Exception("The byteIdentifier " + byteIdentifier + " is already taken");
					}
					classAssociation.set(byteIdentifier, (ObjectCreator<GameEvent>) declaredClass.newInstance());
					found = true;
					break;
				}
			}
			
			if (!found) {
				throw new Exception("The declared GameEvent " + eventClass.getSimpleName() + " must have a nested class called \"Instancier\" of type \"ObjectCreator<GameEvent>\"");
			}
			
		} catch (Exception e) {
			throw new KeriousException(e.getMessage());
		}
	}
	
	public static GameEvent retrieveFromBuffer(ReaderWriter rw) {
		checkInit();
		
		byte byteIdentifier = rw.read((byte)0);
		GameEvent event = null;
		
		ObjectCreator<GameEvent> creator = null;
		
		if (byteIdentifier >= 0 && byteIdentifier < classAssociation.size()) {
			creator = classAssociation.get(byteIdentifier);
		}
		
		if (creator != null) {
			event = creator.instanciate();
			event.unpackFrom(rw);
		} else {
			System.err.println("Unknown event type " + byteIdentifier);
		}
		
		return event;
	}
	
	public static void packInBuffer(GameEvent event, ReaderWriter rw) {
		rw.write(event.eventType);
		event.packIn(rw);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

}
