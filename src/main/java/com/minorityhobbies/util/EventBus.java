package com.minorityhobbies.util;

import java.io.Closeable;

public interface EventBus<T> {
    void publish(T event);

    Closeable subscribe(EventBusSubscriber<T> subscriber);
}
