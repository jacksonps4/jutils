package com.minorityhobbies.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * Generic sliding window collection. Elements are added in FIFO order until the
 * maximum size is reached. Once this happens, the oldest element is evicted
 * whenever a new element is added.
 * 
 * Useful for calculating moving averages or retaining a cache of an exact size.
 * 
 * @param <T>
 *            The type of object in this collection.
 */
public class SlidingWindow<T> implements Iterable<T> {
	private final Deque<T> slidingWindow;
	private int maxSize;

	public SlidingWindow(final int size) {
		super();
		this.maxSize = size;
		slidingWindow = new ArrayDeque<T>(size);
	}

	public void add(T element) {
		if (isFull()) {
			slidingWindow.removeFirst();
		}
		slidingWindow.add(element);
	}

	@Override
	public Iterator<T> iterator() {
		return slidingWindow.iterator();
	}

	public boolean isFull() {
		return slidingWindow.size() >= maxSize;
	}
}
