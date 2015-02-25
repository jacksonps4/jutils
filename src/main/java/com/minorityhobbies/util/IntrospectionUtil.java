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
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntrospectionUtil {
	private final Class<?> type;
	private final Map<String, PropertyDescriptor> pds = new HashMap<String, PropertyDescriptor>();
	private final Pattern numeric = Pattern.compile("[0-9]+");
	
	public IntrospectionUtil(Class<?> type) throws IntrospectionException {
		super();
		this.type = type;
		Class<?> currentType = type;
		while (currentType != null && currentType != Object.class) {
			BeanInfo descriptor = Introspector.getBeanInfo(currentType);
			for (PropertyDescriptor pd : descriptor.getPropertyDescriptors()) {
				if (!"class".equals(pd.getName())) {
					pds.put(pd.getName(), pd);
				}
			}
			currentType = currentType.getSuperclass();
		}
	}

	public List<String> getPropertyNames() {
		return new ArrayList<String>(pds.keySet());
	}

	PropertyDescriptor getPropertyDescriptor(String name) {
		PropertyDescriptor pd = pds.get(name);
		if (pd == null) {
			// try underscores -> camelcase
			pd = pds.get(StringUtils.convertSnakeCaseToCamelCase(name));
		}
		if (pd != null) {
			return pd;
		} else {
			throw new IllegalArgumentException(String.format(
					"Property '%s' not found on instance of type '%s'", name,
					type.getName()));
		}
	}

	public Object getNamedProperty(String name, Object target) {
		if (target == null) {
			throw new NullPointerException();
		}

		Method getter = getPropertyDescriptor(name).getReadMethod();
		try {
			return getter.invoke(target);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	public void setNamedProperty(String name, Object value, Object target) {
		if (target == null) {
			throw new NullPointerException();
		}

		Method setter = getPropertyDescriptor(name).getWriteMethod();
		try {
			setter.invoke(
					target,
					convertValueIfNecessary(value,
							setter.getParameterTypes()[0]));
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(String.format(
					"Failed to set field '%s' " + " to value '%s' on object of type '%s'",
					name, value, type), e);
		}
	}

	private Object convertValueIfNecessary(Object value,
			Class<? extends Object> targetType) {
		if (value == null) {
			return null;
		}

		if (value instanceof String) {
			String v = (String) value;
			if (v.trim().length() == 0) {
				return null;
			}

			if (String.class == targetType) {
				return v;
			}
			if (Boolean.class == targetType || boolean.class == targetType) {
				String b = v.trim();
				if ("0".equals(b) || "no".equals(b.toLowerCase())) {
					return false;
				} else if ("1".equals(b) || "yes".equals(b.toLowerCase())) {
					return true;
				} else {
					return Boolean.parseBoolean(b);
				}
			}
			if (Byte.class == targetType || byte.class == targetType) {
				return Byte.parseByte(v);
			}
			if (Short.class == targetType || short.class == targetType) {
				return Short.parseShort(v);
			}
			if (Integer.class == targetType || int.class == targetType) {
				return Integer.parseInt(v);
			}
			if (Long.class == targetType || long.class == targetType) {
				return Long.parseLong(v);
			}
			if (Float.class == targetType || float.class == targetType) {
				return Float.parseFloat(v);
			}
			if (Double.class == targetType || double.class == targetType) {
				return Double.parseDouble(v);
			}
			if (Date.class == targetType) {
				Matcher nm = numeric.matcher(v);
				if (nm.matches()) {
					return new Date(Long.parseLong(v));
				} else {
					return Dates.parseCommonDateFormats(v);
				}
			}
			if (TemporalAccessor.class.isAssignableFrom(targetType)) {
				try {
					Method parseMethod = getMethod(targetType, "parse", CharSequence.class);
					return parseMethod.invoke(null, v);
				} catch (NoSuchMethodException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
			if (Enum.class.isAssignableFrom(targetType)) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				Class<? extends Enum> e = (Class<? extends Enum>) targetType;
				try {
					Method parseMethod = getMethod(e, "parse", String.class);
					return parseMethod.invoke(null, v);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e1) {
					// failed to find parse method
				}
				
				@SuppressWarnings("unchecked")
				Object result = Enum.valueOf(e, v);
				return result;
			}
		}
		return value;
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

	public void mapToProperties(Object obj, Map<String, Object> properties) {
		for (Map.Entry<String, Object> property : properties.entrySet()) {
			String propertyName = property.getKey();
			Object propertyValue = property.getValue();

			setNamedProperty(propertyName, propertyValue, obj);
		}
	}

	public Class<?> getType() {
		return type;
	}
	
	public static Method getMethod(Class<?> type, String methodName, Class<?>... args) throws NoSuchMethodException {
		Method[] methods = type.getDeclaredMethods();
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				if (args == null || args.length == 0 || Arrays.equals(args, m.getParameterTypes())) {
					return m;
				}
			}
		}
		
		throw new NoSuchMethodException();
	}
}
