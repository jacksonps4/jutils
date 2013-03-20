package com.minorityhobbies.util;

import java.util.List;
import java.util.Map;

public interface DataTable extends Iterable<Map<String, Object>> {
	String getName();
	String getDescription();
	List<Map<String, Object>> getData();
	String[] getItemNames();
	int getRowCount();
}
