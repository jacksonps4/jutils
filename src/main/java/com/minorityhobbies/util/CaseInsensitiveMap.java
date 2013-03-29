/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A map implementation where the case of the key is not taken into account when
 * checking for equality. For example:
 * 
 * <code>
 *   map.put("key", "value");
 *   String v = map.get("KEY"); // will return "value"
 *   map.put("KEY", "new value"); // will return "value"
 *  </code>
 * 
 * @param <T>
 *            The value type for this map.
 */
public class CaseInsensitiveMap<T> implements Map<String, T> {
	private final Map<String, T> map;

	public CaseInsensitiveMap(Map<String, T> map) {
		super();
		this.map = map;
	}

	public void clear() {
		map.clear();
	}

	private String getStringKey(Object key) {
		return ((String) key).toLowerCase();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(getStringKey(key));
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<String, T>> entrySet() {
		return map.entrySet();
	}

	public boolean equals(Object o) {
		return map.equals(o);
	}

	public T get(Object key) {
		return map.get(getStringKey(key));
	}

	public int hashCode() {
		return map.hashCode();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<String> keySet() {
		return map.keySet();
	}

	public T put(String key, T value) {
		return map.put(getStringKey(key), value);
	}

	public void putAll(Map<? extends String, ? extends T> m) {
		for (Map.Entry<? extends String, ? extends T> entry : m.entrySet()) {
			map.put(getStringKey(entry.getKey()), entry.getValue());
		}
	}

	public T remove(Object key) {
		return map.remove(getStringKey(key));
	}

	public int size() {
		return map.size();
	}

	public Collection<T> values() {
		return map.values();
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
