package com.minorityhobbies.util.bus;

public enum BusMessageAttribute {
	MESSAGE_ID("_id"), SOURCE("_src"), DESTINATION("_dst"), MESSAGE_TYPE(
			"_type");

	private final String attributeName;

	private BusMessageAttribute(String attributeName) {
		this.attributeName = attributeName;
	}

	String getAttributeName() {
		return attributeName;
	}
	
	public String get(BusMessage m) {
		return m.get(attributeName);
	}
}