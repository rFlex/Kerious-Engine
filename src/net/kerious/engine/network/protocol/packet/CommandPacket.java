package net.kerious.engine.network.protocol.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.network.protocol.KeriousProtocol;

public class CommandPacket extends KeriousPacket {
	
	////////////////////////
	// VARIABLES
	////////////////
	
	public float directionAngle;
	public float directionStrength;
	
	/**
	 * This contains up to 32 actions on the CommandPacket
	 * Actions should be integer between 0 and 31 included that define
	 * the bit location in the bitfield. We say that the action is active
	 * if the bit is at 1.
	 */
	public long actionsBitfield;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CommandPacket() {
		super(TypeCommand);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
		
		this.directionAngle = buffer.getFloat();
		this.directionStrength = buffer.getFloat();
		this.actionsBitfield = buffer.getLong();
	}
	
	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);
		
		buffer.putFloat(this.directionAngle);
		buffer.putFloat(this.directionStrength);
		buffer.putLong(this.actionsBitfield);
	}
	
	@Override
	public void reset() {
		super.reset();
		
		this.directionAngle = 0;
		this.directionStrength = 0;
		this.actionsBitfield = 0;
	}

	////////////////////////
	// GETTERS/SETTERS
	////////////////
	
	final public void setActionEnabled(int action) {
		this.actionsBitfield |= (0x1 << action);
	}
	
	final public boolean isActionEnabled(int action) {
		return (this.actionsBitfield & 0x1 << action) == 0x1;
	}
}
