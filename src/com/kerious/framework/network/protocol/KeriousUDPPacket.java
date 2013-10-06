package com.kerious.framework.network.protocol;

import java.nio.ByteBuffer;

import com.kerious.framework.exceptions.UnsupportedPacketException;
import com.kerious.framework.network.protocol.packets.IKeriousPacketCreator;
import com.kerious.framework.network.protocol.tools.ReaderWriter;

import static com.kerious.framework.network.protocol.tools.SizeOf.*;

public abstract class KeriousUDPPacket extends ReaderWriter {

	////////////////////////
	// VARIABLES
	////////////////

	final public byte packetType;
	final public boolean reliable;
	
	////////////////////////
	// CONSTRUCTORS
	////////////////

	public KeriousUDPPacket(byte packetType) {
		this.packetType = packetType;
		this.reliable = false;
	}
	
	public KeriousUDPPacket(byte packetType, boolean reliable) {
		this.packetType = packetType;
		this.reliable = reliable;
	}
	
	////////////////////////
	// METHODS
	////////////////

	public void reset() {
		this.setInputChannelBuffer(null);
		this.setOutputChannelBuffer(null);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
	public static KeriousUDPPacket fromBuffer(ByteBuffer buffer, IKeriousPacketCreator creator) {
		byte packetIdentifier = buffer.get();
		
		KeriousUDPPacket packet = creator.packetForIdentifier(packetIdentifier);
		
		try {
			if (packet != null) {
				packet.unpack(buffer);
			} else {
				throw new UnsupportedPacketException(packetIdentifier);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return packet;
	}
	
	protected abstract void childUnpack();
	protected abstract void childPack();
	
	final public void unpack(ByteBuffer buffer) {
		this.reset();
		this.setInputChannelBuffer(buffer);

		this.childUnpack();
	}
	
	final public void pack(ByteBuffer buffer) {
		this.setOutputChannelBuffer(buffer);
		
		write(this.packetType);
		
		this.childPack();
		
	}
	
	////////////////////////
	// GETTERS/SETTERS
	////////////////

	public int size() {
		return sizeof(packetType);
	}

}
