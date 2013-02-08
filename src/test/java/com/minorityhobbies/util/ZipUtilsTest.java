package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class ZipUtilsTest {
	private Set<String> data;
	private Performer<String> callback = new Performer<String>() {
		@Override
		public void perform(String val) {
			data.add(val);
		}
	};
	
	@Before
	public void setUp() {
		data = new HashSet<String>();
	}
	
	@Test
	public void testReadLinesFromZipStream() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		ZipUtils.readLinesFromZipStream(cl.getResourceAsStream("zip/ZipUtilsTest.zip"), callback);
		assertEquals(10, data.size());
		assertTrue(data.contains("one"));
		assertTrue(data.contains("two"));
		assertTrue(data.contains("three"));
		assertTrue(data.contains("four"));
		assertTrue(data.contains("five"));
		assertTrue(data.contains("six"));
		assertTrue(data.contains("seven"));
		assertTrue(data.contains("eight"));
		assertTrue(data.contains("nine"));
		assertTrue(data.contains("ten"));
	}
}
