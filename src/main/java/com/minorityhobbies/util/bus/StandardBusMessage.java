package com.minorityhobbies.util.bus;

import java.util.HashMap;
import java.util.Map;

class StandardBusMessage implements BusMessage {
	private final Map<String, String> attributes;
	
	public StandardBusMessage(Map<String, String> attributes) {
		super();
		this.attributes = new HashMap<String, String>(attributes);
	}

	@Override
	public final String get(String attributeName) {
		return attributes.get(attributeName);
	}

	public final Map<String, String> getAttributes() {
		return new HashMap<String, String>(attributes);
	}
}
