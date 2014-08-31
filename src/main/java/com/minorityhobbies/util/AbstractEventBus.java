package com.minorityhobbies.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Stream;


abstract class AbstractEventBus<T> implements AutoCloseable, EventBus<T> {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<EventBusSubscriber<T>> subscribers = new LinkedList<>();

    @Override
    public Closeable subscribe(final EventBusSubscriber<T> subscriber) {
        try {
            lock.writeLock().lock();
            subscribers.add(subscriber);
        } finally {
            lock.writeLock().unlock();
        }
        return () -> {
                try {
                    lock.writeLock().lock();
                    subscribers.remove(subscriber);
                } finally {
                    lock.writeLock().unlock();
                }
            };
    }

    protected Stream<EventBusSubscriber<T>> subscribers() {
        try {
            lock.readLock().lock();
            return subscribers.stream();
        } finally {
            lock.readLock().unlock();
        }
    }

    protected abstract void onClose() throws Exception;

    @Override
    public void close() throws Exception {
        try {
            lock.writeLock().lock();
            subscribers.clear();
        } finally {
            lock.writeLock().unlock();
        }

        onClose();
    }
}
