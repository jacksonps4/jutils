package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class StreamUtilsTest {
	private final String TEST_VALUE_1 = "t1";
	private final String TEST_VALUE_2 = "t2";
	private final String TEST_VALUE_3 = "t3";
	private final String TEST_VALUE_4 = "four";
	
	private ByteArrayInputStream in;
	
	@Before
	public void buildTestString() {
		StringBuilder testValue = new StringBuilder();
		testValue.append(TEST_VALUE_1);
		testValue.append('\n');
		testValue.append(TEST_VALUE_2);
		testValue.append("\n\n");
		testValue.append(TEST_VALUE_3);
		testValue.append("\n\n");
		testValue.append(TEST_VALUE_4);
		
		in = new ByteArrayInputStream(testValue.toString().getBytes());
	}
	
	@Test
	public void testExpectedValue() throws IOException {
		assertEquals(TEST_VALUE_1, StreamUtils.readLine(in));
		assertEquals(TEST_VALUE_2, StreamUtils.readLine(in));
		assertEquals("", StreamUtils.readLine(in));
		assertEquals(TEST_VALUE_3, StreamUtils.readLine(in));
		assertEquals("", StreamUtils.readLine(in));
		assertEquals(TEST_VALUE_4, StreamUtils.readLine(in));
	}
}
