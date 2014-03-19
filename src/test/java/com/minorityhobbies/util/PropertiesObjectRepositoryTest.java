package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PropertiesObjectRepositoryTest {
	private File location;
	private PropertiesObjectRepository repository;

	@Before
	public void setUp() throws IOException {
		location = File.createTempFile("testRepository", ".repo");
		repository = new PropertiesObjectRepository(location);

		repository.store("someKey", "a value");
		for (int i = 0; i < 30000; i++) {
			repository.store("index-" + i, "value-" + i);
		}
	}

	@After
	public void tearDown() throws IOException {
		assertTrue(location.delete());
	}

	@Test
	public void testFirstValueIsRetrievedFromRepository() throws IOException {
		String value = repository.retrieve("someKey", String.class);
		assertEquals("a value", value);
	}

	@Test
	public void test100000RandomItems() throws IOException {
		long total = 0;
		for (int i = 0; i < 100000; i++) {
			int item = (int) (Math.random() * 30000);
			String key = String.format("index-%s", item);
			String expectedValue = String.format("value-%s", item);

			long start = System.nanoTime();
			String value = repository.retrieve(key, String.class);
			long end = System.nanoTime();
			
			assertEquals(expectedValue, value);
			total += (end - start);
		}
		
		System.out.printf("Random retrieval averages %fms%n", (total / (100000.0 * 1000000.0)));
	}
	
	@Test
	public void testRemove() throws IOException {
		assertTrue(repository.remove("someKey"));
		assertNull(repository.retrieve("someKey", String.class));
	}
}
