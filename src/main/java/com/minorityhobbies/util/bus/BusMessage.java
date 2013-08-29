package com.minorityhobbies.util.bus;

import java.util.Map;

public interface BusMessage {
	String get(String attributeName);
	Map<String, String> getAttributes();
}
