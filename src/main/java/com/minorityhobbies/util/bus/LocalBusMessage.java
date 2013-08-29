package com.minorityhobbies.util.bus;

import java.util.Map;

class LocalBusMessage extends StandardBusMessage {
	public LocalBusMessage(Map<String, String> attributes) {
		super(attributes);
	}
}
