package com.minorityhobbies.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProcessEventBus<T> extends AbstractEventBus<T> implements Runnable {
    private final LinkedBlockingDeque<T> eventQueue = new LinkedBlockingDeque<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ProcessEventBus() {
        executor.submit(this);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                T event = eventQueue.take();
                subscribers().forEach(s -> {
                    if (s.matches().test(event)) {
                        s.onEvent(event);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void publish(T event) {
        eventQueue.add(event);
    }

    @Override
    protected void onClose() throws Exception {
        executor.shutdownNow();
    }
}
