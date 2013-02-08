package com.minorityhobbies.util;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.JUNE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.OCTOBER;
import static java.util.Calendar.YEAR;
import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class DatesTest {
	@Test
	public void testNewDate1() {
		Date date = Dates.newDate("15-06-2012");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		assertEquals(15, cal.get(DAY_OF_MONTH));
		assertEquals(JUNE, cal.get(MONTH));
		assertEquals(2012, cal.get(YEAR));
	}
	
	@Test
	public void testNewDate2() {
		Date date = Dates.newDate("01-12-1990");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		assertEquals(1, cal.get(DAY_OF_MONTH));
		assertEquals(DECEMBER, cal.get(MONTH));
		assertEquals(1990, cal.get(YEAR));
	}
	
	@Test
	public void testNewDate3() {
		Date date = Dates.newDate("31-10-2015");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		assertEquals(31, cal.get(DAY_OF_MONTH));
		assertEquals(OCTOBER, cal.get(MONTH));
		assertEquals(2015, cal.get(YEAR));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidDate() {
		Dates.newDate("31-04-2012");
	}
}
