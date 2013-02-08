package com.minorityhobbies.util;

public abstract class Performer<T> implements Closure<T, Void> {
	public final Void invoke(T val) {
		perform(val);
		return null;
	}
	
	public abstract void perform(T val);
}
