package com.minorityhobbies.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CollectionUtilsTest {
	private List<String> collection;
	
	@Before
	public void setUp() {
		collection = new ArrayList<String>();
		collection.add("one");
		collection.add("two");
		collection.add("three");
	}

	@Test
	public void testEqualsCollectionEmpty() {
		collection.clear();
		assertFalse(CollectionUtils.equals(collection, "one", "two", "three"));
	}

	@Test
	public void testEqualsCollectionEmptyTest() {
		assertFalse(CollectionUtils.equals(collection, (String[]) null));
	}

	@Test
	public void testEqualsCollectionOfTTArray() {
		assertTrue(CollectionUtils.equals(collection, "one", "two", "three"));
	}

	@Test
	public void testEqualsCollectionShorter() {
		collection.add("four");
		assertFalse(CollectionUtils.equals(collection, "one", "two", "three"));
	}

	@Test
	public void testEqualsCollectionLonger() {
		assertFalse(CollectionUtils.equals(collection, "one", "two", "three", "four"));
	}
}
