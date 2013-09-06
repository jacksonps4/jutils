package com.minorityhobbies.util.bus;

import java.io.IOException;

interface BusMessageSerialiser {

	byte[] toBytes(BusMessage msg) throws IOException;

	BusMessage fromBytes(byte[] msg) throws IOException;

}