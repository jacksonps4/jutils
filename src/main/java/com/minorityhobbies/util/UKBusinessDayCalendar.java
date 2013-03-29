/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.util.Calendar;
import java.util.Date;

public class UKBusinessDayCalendar implements BusinessDayCalendar {
	private Calendar now;

	public UKBusinessDayCalendar() {
		now = Calendar.getInstance();
		now.setTime(Dates.newDateMidnightToday());
	}

	public UKBusinessDayCalendar(Date specificDate) {
		now = Calendar.getInstance();
		now.setTime(Dates.removeTimePortionOfDate(specificDate));
	}
	
	@Override
	public Date next() {
		do {
			now.add(Calendar.DAY_OF_YEAR, 1);
		} while (!isBusinessDay());
		return now.getTime();
	}

	@Override
	public boolean isBusinessDay() {
		return now.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
				&& now.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
				&& !Dates.isUKBankHoliday(now.getTime());
	}

	@Override
	public Date previous() {
		do {
			now.add(Calendar.DAY_OF_YEAR, -1);
		} while (!isBusinessDay());
		return now.getTime();
	}
}
