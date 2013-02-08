package com.minorityhobbies.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Closures utility class. Provides a succinct method of applying operations to
 * entire collections or selecting from them.
 * 
 */
public class Closures {
	private Closures() {
	}

	/**
	 * Selects on the elements that match the specified selection closure from
	 * the specified set.
	 * 
	 * @param <T>
	 *            The type of element in the collection
	 * @param set
	 *            The collection from which to select elements.
	 * @param fun
	 *            The closure which defines whether or not an element should be
	 *            selected.
	 * @return The elements from the specified collection which match the
	 *         specified closure.
	 */
	public static <T> List<T> select(Collection<T> set, Closure<T, Boolean> fun) {
		List<T> tasks = new LinkedList<T>();
		for (T t : set) {
			if (fun.invoke(t)) {
				tasks.add(t);
			}
		}
		return tasks;
	}

	/**
	 * Maps the elements of the specified collection to their image using the
	 * specified mapper.
	 * 
	 * @param set
	 *            The collection to be mapped
	 * @param mapper
	 *            The map function.
	 * @return The result
	 */
	public static <T, M> List<M> map(Collection<T> set, Closure<T, M> mapper) {
		List<M> mapped = new LinkedList<M>();
		for (T t : set) {
			mapped.add(mapper.invoke(t));
		}
		return mapped;
	}

	/**
	 * Reduces a collection to a single object.
	 * 
	 * @param set
	 *            The collection
	 * @param reducer
	 *            The reduce function.
	 * @return The result
	 */
	public static <M, R> R reduce(Collection<M> set,
			Closure<Collection<M>, R> reducer) {
		return reducer.invoke(set);
	}

	public static <T> Closure<T, T> identity() {
		return new Closure<T, T>() {
			@Override
			public T invoke(T val) {
				return val;
			}
		};
	}

	/**
	 * Performs the specified operation on all elements on the specified
	 * collection.
	 * 
	 * @param <T>
	 *            The type of element in the collection
	 * @param set
	 *            The elements on which to perform the specified operation
	 * @param fun
	 *            The operation to be performed
	 */
	public static <T> void perform(Collection<T> set, Closure<T, Void> fun) {
		map(set, fun);
	}

	/**
	 * Waits for the specified condition function to return true for the
	 * specified period of time.
	 * 
	 * @param function
	 *            The condition function
	 * @param time
	 *            The time to wait
	 * @param unit
	 *            The unit for the above time.
	 * @return true immediately if the function returns true. Returns false if
	 *         the function has never returned true when the specified time
	 *         period has elapsed.
	 */
	public static boolean waitForCondition(Closure<Void, Boolean> function,
			long time, TimeUnit unit) {
		long timeToWaitInMillis = TimeUnit.MILLISECONDS.convert(time, unit);
		long timeWaited = 0L;
		boolean result = true;
		for (timeWaited = 0; timeWaited < timeToWaitInMillis; timeWaited++) {
			result = function.invoke(null);
			if (result) {
				break;
			}
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				return false;
			}
		}
		if (timeWaited <= timeToWaitInMillis && result) {
			return true;
		}
		return false;
	}
}
