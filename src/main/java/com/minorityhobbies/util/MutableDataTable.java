/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MutableDataTable<T> implements DataTable {
	private final String name;
	private final String description;
	private final List<Map<String, Object>> data;
	private final DataTableMapper<T> mapper;

	public interface DataTableMapper<T> {
		Map<String, Object> map(T obj);
	}

	public MutableDataTable() {
		this("Data table");
	}

	public MutableDataTable(String name) {
		this(name, "Data table",
				new DataTableMapper<T>() {
					@Override
					public Map<String, Object> map(T obj) {
						throw new UnsupportedOperationException();
					}
				});
	}

	public MutableDataTable(String name,
			DataTableMapper<T> mapper) {
		this(name, "Data table", mapper);
	}

	public MutableDataTable(String name, String description,
			DataTableMapper<T> mapper) {
		super();
		this.data = new ArrayList<Map<String, Object>>();
		this.description = description;
		this.name = name;
		this.mapper = mapper;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public List<Map<String, Object>> getData() {
		return data;
	}

	public void addRow(T row) {
		data.add(mapper.map(row));
	}
	
	public void addRow(Map<String, Object> row) {
		data.add(row);
	}
	
	@Override
	public int getRowCount() {
		return data.size();
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

	@Override
	public Iterator<Map<String, Object>> iterator() {
		return data.iterator();
	}

	public ImmutableDataTable immutableCopy() {
		return new ImmutableDataTable(name, data);
	}
}
