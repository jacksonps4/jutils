/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import static java.util.Collections.unmodifiableList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ImmutableDataTable implements DataTable {
	private final String name;
	private final String description;
	private final List<Map<String, Object>> data;
	
	public ImmutableDataTable(List<Map<String, Object>> data) {
		this("Data table", "Data table", data);
	}

	public ImmutableDataTable(String name, List<Map<String, Object>> data) {
		this(name, "Data table", data);
	}

	public ImmutableDataTable(String name, String description, List<Map<String, Object>> data) {
		super();
		this.name = name;
		this.description = description;
		this.data = unmodifiableList(data);
	}

	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String[] getItemNames() {
		Set<String> keys = new HashSet<String>();
		for (Map<String, Object> row : data) {
			for (String key : row.keySet()) {
				if (!keys.contains(key)) {
					keys.add(key);
				}
			}
		}
		return keys.toArray(new String[keys.size()]);
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Iterator<Map<String, Object>> iterator() {
		return data.iterator();
	}
}
