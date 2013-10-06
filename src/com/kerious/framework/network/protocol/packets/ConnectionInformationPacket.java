package com.kerious.framework.network.protocol.packets;

import com.kerious.framework.network.protocol.KeriousUDPPacket;
import com.kerious.framework.utils.Pool.ObjectCreator;

import static com.kerious.framework.network.protocol.tools.SizeOf.*;

public class ConnectionInformationPacket extends KeriousUDPPacket {

	////////////////////////
	// VARIABLES
	////////////////
	
	public final static byte byteIdentifier = 0x7;
	private String ipStr;
	private int ip;
	private int port;
	
	////////////////////////
	// NESTED CLASSES
	////////////////
	
	public static class Instancier implements ObjectCreator<KeriousUDPPacket> {

		@Override
		public KeriousUDPPacket instanciate() {
			return new ConnectionInformationPacket();
		}
		
	}

	////////////////////////
	// CONSTRUCTORS
	////////////////
	
	public ConnectionInformationPacket() {
		super(byteIdentifier);
	}

	////////////////////////
	// METHODS
	////////////////
	
	@Override
	public void reset() {
		super.reset();
		
		this.ip = 0;
		this.port = 0;
	}

	@Override
	protected void childUnpack() {
		this.ip = read(ip);
		this.port = read(port);
		
		this.ipStr = this.intToIp(this.ip);
	}

	@Override
	protected void childPack() {
		write(this.ip);
		write(this.port);
	}
	
	private String intToIp(int rawAddr) {
		return ((rawAddr >> 24 ) & 0xFF) + "." +
				((rawAddr >> 16 ) & 0xFF) + "." +
				((rawAddr >>  8 ) & 0xFF) + "." +
				(rawAddr & 0xFF);
	}
	
	private int ipToInt(String addr) {
        String[] addrArray = addr.split("\\.");

        int num = 0;

        for (int i = 0;i < addrArray.length; i++) {
            int power = 3 - i;
            num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
        }

        return num;
    }

	////////////////////////
	// GETTERS/SETTERS
	////////////////

	@Override
	public int size() {
		return super.size() + sizeof(this.ip) + sizeof(this.port);
	}
	
	public final void setIP(String ip) {
		this.ipStr = ip;
		
		this.ip = this.ipToInt(ip);
	}
	
	public final String getIP() {
		return this.ipStr;
	}
	
	public final void setPort(int port) {
		this.port = port;
	}
	
	public final int getPort() {
		return this.port;
	}
	
	public final String getAddress() {
		return this.getIP() + ":" + this.getPort();
	}
	
}
