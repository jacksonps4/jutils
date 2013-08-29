package com.minorityhobbies.util.bus;

import java.util.HashMap;
import java.util.Map;

class StandardBusMessageBuilder implements BusMessageBuilder {
	private final Map<String, String> attributes = new HashMap<String, String>();
	
	public StandardBusMessageBuilder() {
		super();
	}
	
	@Override
	public final BusMessageBuilder setMessageId(String messageId) {
		attributes.put(BusMessageAttribute.MESSAGE_ID.getAttributeName(), messageId);
		return this;
	}

	@Override
	public final BusMessageBuilder setMessageType(String type) {
		attributes.put(BusMessageAttribute.MESSAGE_TYPE.getAttributeName(), type);
		return this;
	}

	@Override
	public final BusMessageBuilder setMessageSource(String source) {
		attributes.put(BusMessageAttribute.SOURCE.getAttributeName(), source);
		return this;
	}
	
	@Override
	public final BusMessageBuilder setMessageDestination(String destination) {
		attributes.put(BusMessageAttribute.DESTINATION.getAttributeName(), destination);
		return this;
	}
	
	@Override
	public final BusMessageBuilder setAttribute(String attributeName, String attributeValue) {
		if (attributeName.startsWith("_")) {
			throw new IllegalArgumentException("Attribute names cannot start with _");
		}
		attributes.put(attributeName, attributeValue);
		return this;
	}
	
	@Override
	public final BusMessage build() {
		return new StandardBusMessage(attributes);
	}
}
