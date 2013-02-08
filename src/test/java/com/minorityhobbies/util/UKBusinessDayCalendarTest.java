package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UKBusinessDayCalendarTest {
	@Test
	public void testChristmasDay2012IsHoliday() throws Exception {
		BusinessDayCalendar calendar = new UKBusinessDayCalendar(Dates.newDate("25/12/2012"));
		assertFalse(calendar.isBusinessDay());
	}

	@Test
	public void testChristmasEve2012IsNotHoliday() throws Exception {
		BusinessDayCalendar calendar = new UKBusinessDayCalendar(Dates.newDate("24/12/2012"));
		assertTrue(calendar.isBusinessDay());
	}
	
	@Test
	public void testChristmasDayRollForwards() throws Exception {
		BusinessDayCalendar calendar = new UKBusinessDayCalendar(Dates.newDate("24/12/2012"));
		assertTrue(calendar.isBusinessDay());
		assertEquals(calendar.next(), Dates.newDate("27/12/2012"));
	}
	
	@Test
	public void testChristmasDayRollBackwards() throws Exception {
		BusinessDayCalendar calendar = new UKBusinessDayCalendar(Dates.newDate("27/12/2012"));
		assertTrue(calendar.isBusinessDay());
		assertEquals(calendar.previous(), Dates.newDate("24/12/2012"));
	}
}
