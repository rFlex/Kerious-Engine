package net.kerious.engine.network.protocol.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.kerious.engine.gamecontroller.AnalogPad;
import net.kerious.engine.network.protocol.KeriousProtocol;

import com.badlogic.gdx.utils.Array;

public class CommandPacket extends KeriousPacket {
	
	////////////////////////
	// VARIABLES
	////////////////
	
	/**
	 * This contains up to 32 actions on the CommandPacket
	 * Actions should be integer between 0 and 31 included that define
	 * the bit location in the bitfield. We say that the action is active
	 * if the bit is at 1.
	 */
	public long actionsBitfield;
	
	public Array<AnalogPad> analogPads;

	////////////////////////
	// CONSTRUCTORS
	////////////////

	public CommandPacket() {
		super(TypeCommand);
		
		this.analogPads = new Array<AnalogPad>(AnalogPad.class);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void deserialize(KeriousProtocol protocol, ByteBuffer buffer) throws IOException {
		super.deserialize(protocol, buffer);
		
		this.actionsBitfield = buffer.getLong();
		
		int size = buffer.getInt();
		for (int i = 0; i < size; i++) {
			AnalogPad pad = protocol.createAnalogPad();
			pad.deserialize(protocol, buffer);
			this.analogPads.add(pad);
		}
		
	}
	
	@Override
	public void serialize(KeriousProtocol protocol, ByteBuffer buffer) {
		super.serialize(protocol, buffer);
		
		buffer.putLong(this.actionsBitfield);

		buffer.putInt(this.analogPads.size);
		AnalogPad[] pads = this.analogPads.items;
		for (int i = 0, length = this.analogPads.size; i < length; i++) {
			pads[i].serialize(protocol, buffer);
		}
	}
	
	@Override
	public void reset() {
		super.reset();
		
		AnalogPad[] pads = this.analogPads.items;
		for (int i = 0, length = this.analogPads.size; i < length; i++) {
			pads[i].release();
			pads[i] = null;
		}
		
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
