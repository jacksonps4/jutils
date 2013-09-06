package com.minorityhobbies.util.bus;

public enum StandardBusMessageSerialisers {
	STANDARD(new StandardBusMessageSerialiser()), AES(new StandardBusMessageSerialiserAES());
	
	private final BusMessageSerialiser serialiser;
	
	private StandardBusMessageSerialisers(BusMessageSerialiser serialiser) {
		this.serialiser = serialiser;
	}
	
	public BusMessageSerialiser get() {
		return serialiser;
	}
}
