package com.minorityhobbies.util.bus;

public interface BusMessageBuilder {
	BusMessageBuilder setMessageId(String messageId);

	BusMessageBuilder setMessageType(String type);

	BusMessageBuilder setMessageSource(String source);

	BusMessageBuilder setMessageDestination(String destination);

	BusMessageBuilder setAttribute(String attributeName, String attributeValue);

	BusMessage build();
}