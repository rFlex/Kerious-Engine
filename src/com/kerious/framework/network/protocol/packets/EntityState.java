package com.kerious.framework.network.protocol.packets;

import com.badlogic.gdx.math.Vector2;
import com.kerious.framework.exceptions.KeriousException;
import com.kerious.framework.network.Compressable;
import com.kerious.framework.network.Packable;
import com.kerious.framework.network.protocol.tools.ReaderWriter;
import com.kerious.framework.utils.Utils;
import com.kerious.framework.world.entities.Entity;

public class EntityState implements Packable, Compressable<EntityState> {

	////////////////////////
	// VARIABLES
	////////////////

	public static final int BITMASK_FIRST_FREE_POSITION = 5;
	public static final int VIEW_DIRECTION_X = 0;
	public static final int VIEW_DIRECTION_Y = 1;
	public static final int MOVE_DIRECTION_X = 2;
	public static final int MOVE_DIRECTION_Y = 3;
	public static final int SPEED = 4;
	
	public int entityID;
	public float positionX;
	public float positionY;
	protected short fieldPresenceBitMask;
	public byte viewDirectionX;
	public byte viewDirectionY;
	public byte moveDirectionX;
	public byte moveDirectionY;
	public short speed;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public EntityState() {
		this.fieldPresenceBitMask = -1;
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void fillFromEntity(Entity entity) {
		this.entityID = entity.getEntityID();
		this.positionX = entity.getX();
		this.positionY = entity.getY();
		this.viewDirectionX = (byte)(entity.getViewDirectionX() * 100f);
		this.viewDirectionY = (byte)(entity.getViewDirectionY() * 100f);
		
		this.moveDirectionX = (byte)(entity.getMoveDirectionX() * 100f);
		this.moveDirectionY = (byte)(entity.getMoveDirectionY() * 100f);
		
		this.speed = Utils.compressToShort(entity.getSpeed());
	}
	
	public void exportToEntity(Entity entity) {
		entity.teleport(this.positionX, this.positionY);

		Vector2 viewDirection = entity.getViewDirection();
		Vector2 moveDirection = entity.getMoveDirection();
		
		viewDirection.x = ((float)this.viewDirectionX) / 100f;
		viewDirection.y = ((float)this.viewDirectionY) / 100f;
		
		moveDirection.x = ((float)this.moveDirectionX) / 100f;
		moveDirection.y = ((float)this.moveDirectionY) / 100f;
		
		entity.setViewDirection(viewDirection);
		entity.setMoveDirection(moveDirection);
		
		entity.setSpeed(((float)this.speed) / 100f);
	}
	
	@Override
	public void compress(EntityState oldState) {
		if (this.viewDirectionX == oldState.viewDirectionX) {
			this.removeFieldPresence(VIEW_DIRECTION_X);
		}
		if (this.viewDirectionY == oldState.viewDirectionY) {
			this.removeFieldPresence(VIEW_DIRECTION_Y);
		}
		if (this.moveDirectionX == oldState.moveDirectionX) {
			this.removeFieldPresence(MOVE_DIRECTION_X);
		}
		if (this.moveDirectionY == oldState.moveDirectionY) {
			this.removeFieldPresence(MOVE_DIRECTION_Y);
		}
		if (this.speed == oldState.speed) {
			this.removeFieldPresence(SPEED);
		}		
	}
	
	@Override
	public void decompress(EntityState lastKnownState) {
		if (!((this.fieldPresenceBitMask & (0x1 << VIEW_DIRECTION_X)) >> VIEW_DIRECTION_X == 0x1)) {
			this.viewDirectionX = lastKnownState.viewDirectionX;
		}
		if (!((this.fieldPresenceBitMask & (0x1 << VIEW_DIRECTION_Y)) >> VIEW_DIRECTION_Y == 0x1)) {
			this.viewDirectionY = lastKnownState.viewDirectionY;
		}
		if (!((this.fieldPresenceBitMask & (0x1 << MOVE_DIRECTION_X)) >> MOVE_DIRECTION_X == 0x1)) {
			this.moveDirectionX = lastKnownState.moveDirectionX;
		}
		if (!((this.fieldPresenceBitMask & (0x1 << MOVE_DIRECTION_Y)) >> MOVE_DIRECTION_Y == 0x1)) {
			this.moveDirectionY = lastKnownState.moveDirectionY;
		}
		if (!((this.fieldPresenceBitMask & (0x1 << SPEED)) >> SPEED == 0x1)) {
			this.speed = lastKnownState.speed;
		}
	}
	
	public void pack(ReaderWriter rw) {
		rw.write(this.entityID);
		rw.write(this.positionX);
		rw.write(this.positionY);

		rw.write(this.fieldPresenceBitMask);
		
		if ((this.fieldPresenceBitMask & (0x1 << VIEW_DIRECTION_X)) >> VIEW_DIRECTION_X == 0x1) {
			rw.write(this.viewDirectionX);
		}
		if ((this.fieldPresenceBitMask & (0x1 << VIEW_DIRECTION_Y)) >> VIEW_DIRECTION_Y == 0x1) {
			rw.write(this.viewDirectionY);
		}
		if ((this.fieldPresenceBitMask & (0x1 << MOVE_DIRECTION_X)) >> MOVE_DIRECTION_X == 0x1) {
			rw.write(this.moveDirectionX);
		}
		if ((this.fieldPresenceBitMask & (0x1 << MOVE_DIRECTION_Y)) >> MOVE_DIRECTION_Y == 0x1) {
			rw.write(this.moveDirectionY);
		}
		if ((this.fieldPresenceBitMask & (0x1 << SPEED)) >> SPEED == 0x1) {
			rw.write(this.speed);
		}
	}
	
	public void unpack(ReaderWriter rw) {
		this.entityID = rw.read(this.entityID);
		this.positionX = rw.read(this.positionX);
		this.positionY = rw.read(this.positionY);

		this.fieldPresenceBitMask = rw.read(fieldPresenceBitMask);
		
		if ((this.fieldPresenceBitMask & (0x1 << VIEW_DIRECTION_X)) >> VIEW_DIRECTION_X == 0x1) {
			this.viewDirectionX = rw.read(viewDirectionX);
		}
		if ((this.fieldPresenceBitMask & (0x1 << VIEW_DIRECTION_Y)) >> VIEW_DIRECTION_Y == 0x1) {
			this.viewDirectionY = rw.read(viewDirectionY);
		}
		if ((this.fieldPresenceBitMask & (0x1 << MOVE_DIRECTION_X)) >> MOVE_DIRECTION_X == 0x1) {
			this.moveDirectionX = rw.read(moveDirectionX);
		}
		if ((this.fieldPresenceBitMask & (0x1 << MOVE_DIRECTION_Y)) >> MOVE_DIRECTION_Y == 0x1) {
			this.moveDirectionY = rw.read(moveDirectionY);
		}
		if ((this.fieldPresenceBitMask & (0x1 << SPEED)) >> SPEED == 0x1) {
			this.speed = rw.read(speed);
		}
	}
	
	public final void removeFieldPresence(int field) {
		if (field > 15) {
			throw new KeriousException("The bitfield has no more room for a new value! Only 8 values are possible");
		}
		
		this.fieldPresenceBitMask = Utils.setBit(this.fieldPresenceBitMask, field, false);
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	public final float getCurrentVelocity() {
		Vector2.tmp.x = ((float)moveDirectionX) / 100f;
		Vector2.tmp.y = ((float)moveDirectionY) / 100f;		
		float velocity = Vector2.tmp.len();
		
		if (velocity > 1.27f) {
			velocity = 1.27f;
		}
		
		return velocity;
	}

	public String getFieldPresence() {
		StringBuilder sw = new StringBuilder();
		
		for (int i = 7; i >= 0; i--) {
			sw.append((this.fieldPresenceBitMask & (0x1 << i)) >> i == 0x1 ? "1": "0");
		}
		
		return sw.toString();
	}

}
