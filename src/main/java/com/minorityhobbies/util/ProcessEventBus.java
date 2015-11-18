package com.minorityhobbies.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class ProcessEventBus<T> extends AbstractEventBus<T> implements Runnable {
    private final Logger logger = Logger.getLogger(getClass().getName());
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
                    try {
                        if (s.matches().test(event)) {
                            s.onEvent(event);
                        }
                    } catch (RuntimeException e) {
                        logger.throwing(ProcessEventBus.class.getName(), "run", e);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (RuntimeException e) {
                logger.throwing(ProcessEventBus.class.getName(), "run", e);
            }
        }
        logger.warning("ProcessEventBus dispatcher thread terminated");
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
