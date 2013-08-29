package com.minorityhobbies.util.bus;

class StandardBusMessageSubscriptions implements BusMessageSubscriptions {
	@Override
	public BusMessageSubscription newMessageTypeSubscription(String messageType) {
		return new StandardBusMessageSingleAttributeSubscription(
				BusMessageAttribute.MESSAGE_TYPE.getAttributeName(),
				messageType);
	}

	@Override
	public BusMessageSubscription newSourceSubscription(String source) {
		return new StandardBusMessageSingleAttributeSubscription(
				BusMessageAttribute.SOURCE.getAttributeName(), source);
	}

	@Override
	public BusMessageSubscription newDestinationSubscription(String destination) {
		return new StandardBusMessageSingleAttributeSubscription(
				BusMessageAttribute.DESTINATION.getAttributeName(), destination);
	}
	
	@Override
	public BusMessageSubscription newAllMessagesSubscription() {
		return new BusMessageSubscription() {
			@Override
			public boolean matches(BusMessage msg) {
				return true;
			}
		};
	}
}
