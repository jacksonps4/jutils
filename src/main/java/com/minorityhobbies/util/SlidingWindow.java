/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
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
