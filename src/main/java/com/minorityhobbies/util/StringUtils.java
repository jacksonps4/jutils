package com.minorityhobbies.util;

public class StringUtils {
	private StringUtils() {}
	
	public static String generateRandomPayload(int length) {
		StringBuilder random = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int chr = (int) (Math.random() * 26) + 65;
			boolean uppercase = Math.random() >= 0.5 ? true : false;
			if (uppercase) {
				chr = chr + 32;
			}
			random.append((char) chr);
		}
		return random.toString();
	}
}
