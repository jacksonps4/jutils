package com.minorityhobbies.util;

public abstract class Selector<T> implements Closure<T, Boolean> {
	public final Boolean invoke(T val) {
		return select(val);
	}
	
	public abstract boolean select(T val);
}
