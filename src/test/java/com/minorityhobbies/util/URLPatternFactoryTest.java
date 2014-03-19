package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class URLPatternFactoryTest {
	private URLPatternFactory urlPatternFactory;
	
	@Before
	public void setUp() {
		List<Object[]> parameters = new ArrayList<Object[]>();
		parameters.add(new Object[] { 1 });
		parameters.add(new Object[] { 2 });
		parameters.add(new Object[] { 3 });
		parameters.add(new Object[] { 4 });
		parameters.add(new Object[] { 5 });
		
		urlPatternFactory = new URLPatternFactory("http://www.google.com/file%d.txt", (Iterable<Object[]>) parameters);
	}
	
	@Test
	public void shouldHaveCorrectNumberOfElements() {
		int count = 0;
		while (urlPatternFactory.hasNext()) {
			urlPatternFactory.next();
			count++;
		}
		assertEquals(5, count);
	}

	void checkUrl(int n) {
		URL url = null;
		for (int i = 0; i < n; i++) {
			url = urlPatternFactory.next();
		}
		assertEquals("http://www.google.com/file" + n + ".txt", url.toExternalForm());
	}
	
	@Test
	public void shouldHaveCorrectUrl1() {
		checkUrl(1);
	}

	@Test
	public void shouldHaveCorrectUrl2() {
		checkUrl(2);
	}
	
	@Test
	public void shouldHaveCorrectUrl3() {
		checkUrl(3);
	}

	@Test
	public void shouldHaveCorrectUrl4() {
		checkUrl(4);
	}

	@Test
	public void shouldHaveCorrectUrl5() {
		checkUrl(5);
	}
	
	@Test
	public void documentationTestCase() throws MalformedURLException {
		URLPatternFactory upf = new URLPatternFactory("http://www.java.com/data-%d", Arrays.asList(1, 2, 3));
		int count = 0;
		for (URL url : upf) {
			switch (count++) {
			case 0:
				assertEquals(new URL("http://www.java.com/data-1"), url);
				break;
			case 1:
				assertEquals(new URL("http://www.java.com/data-2"), url);
				break;
			case 2:
				assertEquals(new URL("http://www.java.com/data-3"), url);
				break;
			}
		}
	}
}
