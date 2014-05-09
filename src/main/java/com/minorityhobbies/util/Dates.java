/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Dates {
	private Dates() {
	}

	public static Date newDate(String ddmmyyyy) {
		String[] components = ddmmyyyy.split("[-/\\.]");
		if (components.length != 3) {
			throw new IllegalArgumentException(String.format(
					"Cannot parse '%s' as date in form dd-mm-yyyy", ddmmyyyy));
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		int[] dateParts = new int[3];
		for (int i = 0; i < 3; i++) {
			dateParts[i] = Integer.parseInt(components[i]);
		}
		
		cal.set(Calendar.YEAR, dateParts[2]);
		cal.set(Calendar.MONTH, dateParts[1] - 1);
		cal.set(Calendar.DAY_OF_MONTH, dateParts[0]);
		
		return cal.getTime();
	}
	
	public static Date newDateMidnightToday() {
		return getCalendarWithoutTimePortion().getTime();
	}

	public static Date newDateMidnightTomorrrow() {
		Calendar cal = getCalendarWithoutTimePortion();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return cal.getTime();
	}
	
	public static Date newDateMidnightYesterday() {
		Calendar cal = getCalendarWithoutTimePortion();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		return cal.getTime();
	}

	private static Calendar getCalendarWithoutTimePortion() {
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public static Timestamp newTimestampMidnightToday() {
		Calendar cal = getCalendarWithoutTimePortion();
		return new Timestamp(cal.getTime().getTime());
	}
	
	public static Date removeTimePortionOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}
	
	public static boolean isUKBankHoliday(Date dateToTest) {
		try {
			Date testDate = removeTimePortionOfDate(dateToTest);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String ukBankHolidaysList = ClasspathUtils.readFile("calendars/UKBankHolidays.txt");
			BufferedReader reader = new BufferedReader(new StringReader(ukBankHolidaysList));
			for (String date = null; (date = reader.readLine()) != null; ) {
				Date holiday = dateFormat.parse(date);
				if (holiday.equals(testDate)) {
					return true;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
		return false;
	}

	public static Date startOfCurrentMonth() {
		Calendar cal = getCalendarWithoutTimePortion();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	public static Date startOfCurrentYear() {
		Calendar cal = getCalendarWithoutTimePortion();
		cal.set(Calendar.DAY_OF_YEAR, 1);
		return cal.getTime();
	}
}
