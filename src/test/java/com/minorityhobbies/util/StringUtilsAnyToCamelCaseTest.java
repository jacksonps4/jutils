package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsAnyToCamelCaseTest {
	@Test
	public void unit() {
		assertEquals("simple", StringUtils.anyToCamelCase("simple"));
	}

	@Test
	public void whitespace() {
		assertEquals("twoWords", StringUtils.anyToCamelCase("two words"));
	}
	
	@Test
	public void nonAlnum() {
		assertEquals("twoWords", StringUtils.anyToCamelCase("two_words"));
	}
	
	@Test
	public void pascalCase() {
		assertEquals("twoWords", StringUtils.anyToCamelCase("TwoWords"));
	}
	
	@Test
	public void pascalCaseWithSpace() {
		assertEquals("twoWords", StringUtils.anyToCamelCase("Two Words"));
	}
}
