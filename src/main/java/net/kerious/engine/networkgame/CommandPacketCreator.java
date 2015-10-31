package net.kerious.engine.networkgame;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.packet.CommandPacket;

public interface CommandPacketCreator {

	CommandPacket generateCommandPacket(KeriousProtocol protocol);
	
}
