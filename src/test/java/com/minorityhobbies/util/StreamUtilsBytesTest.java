package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class StreamUtilsBytesTest {
	private ByteArrayInputStream in;
	
	@Before
	public void buildTestString() {
		in = new ByteArrayInputStream(new byte[] { 13, 10, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 13, 10, 13, 10 });
	}
	
	@Test
	public void testByteDecoding() throws IOException {
		assertEquals("", StreamUtils.readLine(in));
		assertEquals("0123456789", StreamUtils.readLine(in));
		assertEquals("", StreamUtils.readLine(in));
	}
}
