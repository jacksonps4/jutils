package com.minorityhobbies.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileTailReaderTest {
	private FileTailReader reader;
	private File testFile;
	
	@Before
	public void setUp() throws IOException {
		testFile = File.createTempFile("test", ".test");
		reader = new FileTailReader(testFile);
	}
	
	@After
	public void tearDown() {
		if (testFile != null) {
			if (!testFile.delete()) {
				fail("Failed to delele test file: " + testFile.getAbsolutePath());
			}
		}
	}
	
	@Test
	public void writeRead() throws IOException {
		String test1 = "test1";
		try (FileWriter writer = new FileWriter(testFile, true)) {
			writer.write(test1);
			writer.flush();
		}
		
		char[] cbuf = new char[1024];
		StringBuilder readFromStream = new StringBuilder();
		for (int read = 0; (read = reader.read(cbuf, 0, cbuf.length)) > 0; ) {
			readFromStream.append(new String(cbuf, 0, read));
		}
		
		assertEquals(test1, readFromStream.toString());
		
		String test2 = "blah2";
		try (FileWriter writer = new FileWriter(testFile, true)) {
			writer.write(test2);
			writer.flush();
		}

		readFromStream = new StringBuilder();
		for (int read = 0; (read = reader.read(cbuf, 0, cbuf.length)) > 0; ) {
			readFromStream.append(new String(cbuf, 0, read));
		}
		assertEquals(test2, readFromStream.toString());
	}
}
