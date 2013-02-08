package com.minorityhobbies.util;

/**
 * A simple closure.
 * 
 * @param <P>
 *            The closure method parameter type.
 * @param <R>
 *            The closure method return type.
 */
public interface Closure<P, R> {
	/**
	 * The closure operation.
	 * 
	 * @param val
	 *            The parameter.
	 * @return The result.
	 */
	R invoke(P val);
}
