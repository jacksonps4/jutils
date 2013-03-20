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
