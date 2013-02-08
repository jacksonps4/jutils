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
