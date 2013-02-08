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
