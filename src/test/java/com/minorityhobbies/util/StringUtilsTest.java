package com.minorityhobbies.util;

import static com.minorityhobbies.util.StringUtils.generateRandomPayload;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class StringUtilsTest {
	private static final int LENGTH = 10;
	private static final Pattern ALPHA_ONLY = Pattern.compile("[A-Za-z]{" + LENGTH + "}");
	
	@Test
	public void testRandomPayload() {
		for (int i = 0; i < 1000000; i++) {
			String randomPayload = generateRandomPayload(10);
			checks(randomPayload);
		}
	}

	public void checks(String payload) {
		assertEquals(LENGTH, payload.length());
		Matcher matcher = ALPHA_ONLY.matcher(payload);
		assertTrue(matcher.matches());
	}
}
