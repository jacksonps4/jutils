package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.Test;
import org.junit.Ignore;

public class FileUtilsTest {
	private static final String FILE_DATA = "Some data\n";

	@Test
	public void testReadFileFile() throws IOException, URISyntaxException {
		ClassLoader cl = getClass().getClassLoader();
		URL url = cl.getResource("fileutil/test.txt");
		String fileData = FileUtils.readFile(new File(url.toURI()));
		assertEquals(FILE_DATA, fileData);
	}

	@Test
	public void testReadFileString() throws IOException, URISyntaxException {
		ClassLoader cl = getClass().getClassLoader();
		URL url = cl.getResource("fileutil/test.txt");
		String fileData = FileUtils.readFile(new File(url.toURI())
				.getAbsolutePath());
		assertEquals(FILE_DATA, fileData);
	}

	@Test
	public void testListFilesRecursively() throws URISyntaxException {
		ClassLoader cl = getClass().getClassLoader();
		URL url = cl.getResource("fileutil/");
		File dir = new File(url.toURI());
		List<File> files = FileUtils.listFilesRecursively(dir);
		assertEquals(3, files.size());
		assertTrue(checkForSpecificFile(files, "test.txt"));
		assertTrue(checkForSpecificFile(files, "test2.txt"));
		assertTrue(checkForSpecificFile(files, "test3.txt"));
	}
	
	private boolean checkForSpecificFile(List<File> files, String filename) {
		for (File file : files) {
			if (file.getName().equals(filename)) {
				return true;
			}
		}
		return false;
	}
	
	@Test
    @Ignore
	public void followTailTest() throws IOException {
		Closeable c = FileUtils.followTail(new File("/tmp/test.txt"), new Performer<String>() {
			@Override
			public void perform(String val) {
				System.out.printf(val);
			}
		});
		new BufferedReader(new InputStreamReader(System.in)).readLine();
		c.close();
	}
}
