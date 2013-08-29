package com.minorityhobbies.util.bus;

public interface BusMessageSubscriptions {
	BusMessageSubscription newMessageTypeSubscription(String messageType);

	BusMessageSubscription newSourceSubscription(String source);

	BusMessageSubscription newDestinationSubscription(String destination);
	
	BusMessageSubscription newAllMessagesSubscription();
}