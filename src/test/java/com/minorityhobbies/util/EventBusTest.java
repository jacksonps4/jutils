package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class EventBusTest {
    private ProcessEventBus<String> testBus;

    @Before
    public void setUp() {
        testBus = new ProcessEventBus<>();
    }

    @After
    public void tearDown() throws Exception {
        if (testBus != null) {
            testBus.close();
        }
    }

    @Test
    public void shouldReceivePublishedMessage() throws InterruptedException {
        AtomicReference<String> result = new AtomicReference<>();
        testBus.subscribe(m -> result.set(m));

        testBus.publish("foo");
        for (int i = 0; i < 200; i++) {
            Thread.sleep(10L);
            if (result.get() != null) {
                break;
            }
        }
        assertEquals("foo", result.get());
    }
}
