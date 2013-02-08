package com.minorityhobbies.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ClosuresTest {
	@Test
	public void testWaitForConditionFails() {
		long startTime = System.currentTimeMillis();
		assertFalse(Closures.waitForCondition(new Condition() {
			public boolean check() {
				return false;
			}
		}, 100, TimeUnit.MILLISECONDS));
		assertTrue("Expected this to wait until timeout", (System.currentTimeMillis() - startTime) >= 100);
	}

	@Test
	public void testWaitForConditionSucceeds() {
		long startTime = System.currentTimeMillis();
		assertTrue(Closures.waitForCondition(new Condition() {
			public boolean check() {
				return true;
			}
		}, 100, TimeUnit.MILLISECONDS));
		assertTrue("Expected this to return immediately", (System.currentTimeMillis() - startTime) < 100);
	}
}
