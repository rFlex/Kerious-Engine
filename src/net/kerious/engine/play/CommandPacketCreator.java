package net.kerious.engine.play;

import net.kerious.engine.network.protocol.KeriousProtocol;
import net.kerious.engine.network.protocol.packet.KeriousPacket;

public interface CommandPacketCreator {

	KeriousPacket generateCommandPacket(KeriousProtocol protocol);
	
}
