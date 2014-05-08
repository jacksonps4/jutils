package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileTreeReaderTest {
	private File root;
	private Set<String> values;
	
	@Before
	public void createTestTree() throws IOException {
		String tmpDir = System.getProperty("java.io.tmpdir");
		root = new File(tmpDir, "filetreetest");
		if (!root.exists() && !root.mkdir()) {
			throw new IOException();
		}
		File d1 = new File(root, "d1");
		d1.mkdir();
		File d2 = new File(root, "d2");
		d2.mkdir();
		File d3 = new File(d1, "d3");
		d3.mkdir();
		
		File f1 = new File(d1, "f1");
		FileUtils.writeDataToFile(f1, "123\n");
		File f2 = new File(d1, "f2");
		FileUtils.writeDataToFile(f2, "234\n");
		File f3 = new File(d2, "f3");
		FileUtils.writeDataToFile(f3, "345\n");
		File f4 = new File(d3, "f4");
		FileUtils.writeDataToFile(f4, "456\n");
		File f5 = new File(d3, "f5");
		FileUtils.writeDataToFile(f5, "567\n");
		
		BufferedReader reader = new BufferedReader(new FileTreeReader(root).getReader());
		values = new HashSet<>();
		for (String line = null; (line = reader.readLine()) != null; ) {
			values.add(line);
		}
	}
	
	@After
	public void removeTestTree() throws IOException {
		for (File f : FileUtils.listFilesRecursively(root)) {
			if (!f.delete()) {
				f.delete();
			}
		}
		for (File d : FileUtils.listDirectoriesRecursively(root)) {
			if (!d.delete()) {
				throw new IOException();
			}
		}
	}

	@Test
	public void shouldHaveCorrectLength() throws IOException {
		assertEquals(5, values.size());
	}
	
	@Test
	public void shouldHave123() throws IOException {
		assertTrue(values.contains("123"));
	}
	
	@Test
	public void shouldHave234() throws IOException {
		assertTrue(values.contains("234"));
	}
	
	@Test
	public void shouldHave345() throws IOException {
		assertTrue(values.contains("345"));
	}
	
	@Test
	public void shouldHave456() throws IOException {
		assertTrue(values.contains("456"));
	}
	
	@Test
	public void shouldHave567() throws IOException {
		assertTrue(values.contains("567"));
	}
}
