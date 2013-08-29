package com.minorityhobbies.util.bus;

public interface BusMessageSubscription {
	boolean matches(BusMessage msg);
}
