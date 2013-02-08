package com.minorityhobbies.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {
	public static <R, T> R marshallFromProperty(String name, String parameters,
			Class<T> type) throws NoSuchMethodException,
			IllegalArgumentException {
		String[] args = parameters.split("[,\\s]");
		Class<?>[] parameterTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			parameterTypes[i] = String.class;
		}
		Method method = type.getMethod(name, parameterTypes);
		try {
			@SuppressWarnings("unchecked")
			R selector = (R) method.invoke(null, (Object[]) args);
			return selector;
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
