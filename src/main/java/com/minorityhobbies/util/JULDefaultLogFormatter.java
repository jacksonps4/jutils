/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class JULDefaultLogFormatter extends Formatter {
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String FIELD_SEP = " ";
    private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        }   
    };  

    public final String format(LogRecord record) {
        StringBuilder logEntry = new StringBuilder();
        logEntry.append(dateFormat.get().format(new Date(record.getMillis())));
        logEntry.append(FIELD_SEP);
        logEntry.append("[");
        logEntry.append(Thread.currentThread().getName());
        logEntry.append("]");
        logEntry.append(FIELD_SEP);
        logEntry.append(getShortClassName(record.getLoggerName()));
        logEntry.append(FIELD_SEP);
        logEntry.append(record.getLevel().getName());
        logEntry.append(FIELD_SEP);
        logEntry.append(record.getMessage());
        logEntry.append(LINE_SEP);
        return logEntry.toString();
    }   

    final String getShortClassName(String fullName) {
        return fullName.substring(fullName.lastIndexOf(".") + 1); 
    }   
}