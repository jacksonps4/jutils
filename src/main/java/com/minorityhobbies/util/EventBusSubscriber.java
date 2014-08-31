package com.minorityhobbies.util;

import java.util.function.Predicate;

@FunctionalInterface
public interface EventBusSubscriber<T> {
    void onEvent(T event);
    default Predicate<T> matches() {
        return p -> true;
    }
}
