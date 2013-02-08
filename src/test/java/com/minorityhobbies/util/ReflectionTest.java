package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReflectionTest {
	private static final class MarshallTest {
		private MarshallTest() {}
		
		@SuppressWarnings("unused")
		public static String testValue(String value1, String value2) {
			return "updated:" + value1 + "," + value2;
		}
	}
	
	@Test
	public void testMarshallFromProperty() throws IllegalArgumentException, NoSuchMethodException {
		String returned = Reflection.marshallFromProperty("testValue", "one,two", MarshallTest.class);
		assertEquals("updated:one,two", returned);
	}
}
