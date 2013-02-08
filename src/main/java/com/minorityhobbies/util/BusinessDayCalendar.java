package com.minorityhobbies.util;

import java.util.Date;

/**
 * A simple business day calendar.
 * 
 */
public interface BusinessDayCalendar {
	/**
	 * Gets the business day in the future that is closest to today. If today is
	 * a business day this will return today. If today is not a business day,
	 * this will return the next valid business day.
	 * 
	 * @return The current business day if today is a business day or the next
	 *         business day if not.
	 */
	Date next();

	/**
	 * Returns true if today is a business day.
	 * 
	 * @return True if today is a business day. False otherwise.
	 */
	boolean isBusinessDay();

	/**
	 * Gets the previous business day.
	 * 
	 * @return	The previous business day.
	 */
	Date previous();

}