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
