package com.minorityhobbies.util;

public abstract class Condition implements Closure<Void, Boolean> {
	@Override
	public final Boolean invoke(Void val) {
		return check();
	}

	public abstract boolean check();
}
