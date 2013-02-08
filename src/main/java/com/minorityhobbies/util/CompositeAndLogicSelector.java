package com.minorityhobbies.util;

import java.util.ArrayList;
import java.util.List;

public class CompositeAndLogicSelector<T> extends Selector<T> {
	private final List<Selector<T>> selectors;

	public CompositeAndLogicSelector(List<Selector<T>> selectors) {
		super();
		this.selectors = new ArrayList<Selector<T>>(selectors);
	}

	@Override
	public boolean select(T val) {
		for (Selector<T> selector : selectors) {
			if (!selector.select(val)) {
				return false;
			}
		}
		return true;
	}
}
