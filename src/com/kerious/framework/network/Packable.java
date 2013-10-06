package com.kerious.framework.network;

import com.kerious.framework.network.protocol.tools.ReaderWriter;

public interface Packable {

	void pack(ReaderWriter rw);
	void unpack(ReaderWriter rw);
	
}
