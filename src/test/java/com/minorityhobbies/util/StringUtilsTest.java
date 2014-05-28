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
	
	@Test
	public void testSplitWithQuotes() {
		String[] fields = StringUtils.splitQuoted("12 Jan 13,\" The Star Inn, Harome \",MR C WRAITH,Entertainment,,120.00", ',');
		assertEquals(6, fields.length);
		
		assertEquals("12 Jan 13", fields[0]);
		assertEquals(" The Star Inn, Harome ", fields[1]);
		assertEquals("MR C WRAITH", fields[2]);
		assertEquals("Entertainment", fields[3]);
		assertEquals("", fields[4]);
		assertEquals("120.00", fields[5]);
	}
	
	@Test
	public void testSnakeCaseToCamelCase() {
		assertEquals("convertedValueOne", StringUtils.convertSnakeCaseToCamelCase("converted_value_one"));
		assertEquals("anotherTwo", StringUtils.convertSnakeCaseToCamelCase("another_two"));
		assertEquals("ending", StringUtils.convertSnakeCaseToCamelCase("ending_"));
		assertEquals("Beginning", StringUtils.convertSnakeCaseToCamelCase("_beginning"));
	}
}
