/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility methods to simply operations on collections.
 */
public class CollectionUtils {
	private CollectionUtils() {
	}

	/**
	 * Creates a new {@link ArrayList} of the specified type with the specified
	 * elements.
	 * 
	 * Generally, {@link ArrayList} instances are used where fast access to
	 * random indexed elements is required. Append operations can be more
	 * expensive than with a {@link LinkedList} if the backing array needs to be
	 * resized.
	 * 
	 * @param <T>
	 *            The type of element.
	 * @param elements
	 *            The elements in the list
	 * @return The list.
	 */
	public static <T> List<T> newArrayList(T... elements) {
		return (List<T>) addElements(new ArrayList<T>(), elements);
	}

	/**
	 * Creates a new {@link LinkedList} of the specified type with the specified
	 * elements.
	 * 
	 * Usually, {@link LinkedList} instances are used for mutable lists where
	 * elements are likely to be appended. Iteration of the list is fast but
	 * access to random elements is more expensive that with a
	 * {@link LinkedList}.
	 * 
	 * @param <T>
	 *            The type of element.
	 * @param elements
	 *            The elements in the list
	 * @return The list.
	 */
	public static <T> List<T> newLinkedList(T... elements) {
		return (List<T>) addElements(new LinkedList<T>(), elements);
	}

	/**
	 * Creates a new {@link HashSet} of the specified type with the specified
	 * elements.
	 * 
	 * A {@link HashSet} is an UNORDERED set .
	 * 
	 * @param <T>
	 *            The type of element.
	 * @param elements
	 *            The elements in the set.
	 * @return The set.
	 */
	public static <T> Set<T> newHashSet(T... elements) {
		return (Set<T>) addElements(new HashSet<T>(), elements);
	}

	/**
	 * Creates a new {@link TreeSet} of the specified type with the specified
	 * elements.
	 * 
	 * A {@link TreeSet} is an ORDERED set .
	 * 
	 * @param <T>
	 *            The type of element.
	 * @param elements
	 *            The elements in the set.
	 * @return The set.
	 */
	public static <T> Set<T> newTreeSet(T... elements) {
		return (Set<T>) addElements(new TreeSet<T>(), elements);
	}

	/**
	 * Adds the specified elements to the specified collection.
	 * 
	 * @param <T>
	 *            The element type.
	 * @param collection
	 *            The collection to which elements are to be added.
	 * @param elements
	 *            The elements to add.
	 * @return The collection passed in.
	 */
	private static <T> Collection<T> addElements(Collection<T> collection,
			T... elements) {
		for (T el : elements) {
			collection.add(el);
		}
		return collection;
	}

	/**
	 * Compares the specified collection against the specified elements for
	 * equality.
	 * 
	 * @param <T>
	 *            The element type.
	 * @param collection
	 *            The collection against which to test.
	 * @param expectedElements
	 *            The elements to test against the collection.
	 * @return True if the collection contains the specified elements and no
	 *         more. False otherwise.
	 */
	public static <T> boolean equals(Collection<T> collection,
			T... expectedElements) {
		if (expectedElements == null) {
			return false;
		}
		if (collection.size() != expectedElements.length) {
			return false;
		}
		int i = 0;
		for (T el : collection) {
			if (!el.equals(expectedElements[i++])) {
				return false;
			}
		}
		return true;
	}
}
