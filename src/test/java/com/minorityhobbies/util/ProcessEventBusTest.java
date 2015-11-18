package com.minorityhobbies.util;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProcessEventBusTest {
    private ProcessEventBus<String> eventBus;

    @Before
    public void setUp() {
        eventBus = new ProcessEventBus<>();
    }

    @After
    public void tearDown() throws Exception {
        if (eventBus != null) {
            eventBus.close();
        }
    }

    private void publishMessageAndTest(String msg) throws InterruptedException, IOException {
        Closeable handle = null;
        try {
            AtomicReference<String> event = new AtomicReference<>();
            handle = eventBus.subscribe(event::set);
            eventBus.publish(msg);
            for (int i = 0; event.get() == null && i < 100; i++) {
                Thread.sleep(10L);
            }
            assertEquals(msg, event.get());
        } finally {
            if (handle != null) {
                handle.close();
            }
        }
    }

    @Test
    public void publishedEventIsReceivedBySubscriber() throws InterruptedException, IOException {
        publishMessageAndTest("test");
    }

    @Test
    public void predicateExceptionIsHandled() throws IOException, InterruptedException {
        Closeable handle = null;
        try {
            AtomicReference<String> event = new AtomicReference<>();
            handle = eventBus.subscribe(new EventBusSubscriber<String>() {
                @Override
                public Predicate<String> matches() {
                    throw new RuntimeException();
                }

                @Override
                public void onEvent(String e) {
                    event.set(e);
                }
            });
            eventBus.publish("message1");
            for (int i = 0; event.get() == null && i < 100; i++) {
                Thread.sleep(10L);
            }
            assertNull(event.get());
        } finally {
            if (handle != null) {
                handle.close();
            }
        }

        publishMessageAndTest("message2");
    }

    @Test
    public void handlerExceptionIsHandled() throws IOException, InterruptedException {
        Closeable handle = null;
        try {
            AtomicReference<String> event = new AtomicReference<>();
            handle = eventBus.subscribe(new EventBusSubscriber<String>() {
                @Override
                public void onEvent(String e) {
                    throw new RuntimeException();
                }
            });
            eventBus.publish("message1");
            for (int i = 0; event.get() == null && i < 100; i++) {
                Thread.sleep(10L);
            }
            assertNull(event.get());
        } finally {
            if (handle != null) {
                handle.close();
            }
        }

        publishMessageAndTest("message2");
    }
}
