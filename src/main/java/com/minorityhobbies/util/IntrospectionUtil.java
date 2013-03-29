/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IntrospectionUtil {
	private final Class<?> type;
	private final BeanInfo descriptor;
	private final Map<String, PropertyDescriptor> pds = new HashMap<String, PropertyDescriptor>();

	public IntrospectionUtil(Class<?> type) throws IntrospectionException {
		super();
		this.type = type;
		this.descriptor = Introspector.getBeanInfo(type);
		for (PropertyDescriptor pd : descriptor.getPropertyDescriptors()) {
			if (!"class".equals(pd.getName())) {
				pds.put(pd.getName(), pd);
			}
		}
	}

	public List<String> getPropertyNames() {
		return new ArrayList<String>(pds.keySet());
	}

	public Object getNamedProperty(String name, Object target) {
		if (target == null) {
			throw new NullPointerException();
		}

		PropertyDescriptor pd = pds.get(name);
		if (pd != null) {
			Method getter = pd.getReadMethod();
			try {
				return getter.invoke(target);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new IllegalArgumentException(String.format(
					"Property '%s' not found on instance of type '%s'", name,
					target.getClass().getName()));
		}
	}

	public Map<String, Object> propertiesToMap(Object obj) {
		Map<String, Object> row = new LinkedHashMap<String, Object>();
		for (String name : getPropertyNames()) {
			if (!"class".equals(name)) {
				row.put(name, getNamedProperty(name, obj));
			}
		}
		return row;
	}

	public Class<?> getType() {
		return type;
	}
}
