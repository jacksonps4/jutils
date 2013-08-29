package com.minorityhobbies.util.bus;

class StandardBusMessageSingleAttributeSubscription implements BusMessageSubscription {
	private final String attributeName;
	private final String attributeValue;
	
	public StandardBusMessageSingleAttributeSubscription(String attributeName,
			String attributeValue) {
		super();
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
	}

	@Override
	public final boolean matches(BusMessage msg) {
		return attributeValue.equals(msg.get(attributeName));
	}
}
