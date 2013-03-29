/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
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