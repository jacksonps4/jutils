/*
Copyright (c) 2014 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * It is often necessary to convert a Collection of some sort to an Enumeration
 * for the purposes of an older API call. This class enables any Collection to be
 * viewed as an {@link Enumeration} or indeed a {@link NamingEnumeration}. 
 *
 * @param <T>	The type of element in the {@link Enumeration}.
 */
public class CollectionEnumeration<T> implements Enumeration<T>, NamingEnumeration<T> {
	private final Iterator<T> itr;

	public CollectionEnumeration(Collection<T> collection) {
		super();
		this.itr = collection.iterator();
	}

	@Override
	public boolean hasMoreElements() {
		return itr.hasNext();
	}

	@Override
	public T nextElement() {
		return itr.next();
	}

	@Override
	public void close() throws NamingException {
	}

	@Override
	public boolean hasMore() throws NamingException {
		return itr.hasNext();
	}

	@Override
	public T next() throws NamingException {
		return itr.next();
	}
}
