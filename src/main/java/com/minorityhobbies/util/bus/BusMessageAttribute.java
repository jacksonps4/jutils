package com.minorityhobbies.util.bus;

enum BusMessageAttribute {
	MESSAGE_ID("_id"), SOURCE("_src"), DESTINATION("_dst"), MESSAGE_TYPE(
			"_type");

	private final String attributeName;

	private BusMessageAttribute(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeName() {
		return attributeName;
	}
}