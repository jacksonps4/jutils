package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

public class URLUtilsTest {
	@Test
	public void testGetStringBackedUrl() throws IOException {
		final String content = "some data";
		URL url = URLUtils.getStringBackedUrl("test", content);

		URLConnection conn = url.openConnection();
		InputStream in = conn.getInputStream();
		assertNotNull(in);

		StringBuilder sb = new StringBuilder();
		byte[] b = new byte[1024 * 4];
		for (int read = 0; (read = in.read(b)) > -1;) {
			sb.append(new String(b, 0, read));
		}
		String urlContent = sb.toString();

		assertEquals(content, urlContent);
	}
	
	@Test
	public void testReadUrl() throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		URL resource = cl.getResource("urltest.file");
		String content = URLUtils.readUrlData(resource);
		assertEquals("This is a test\n", content);
	}
}
