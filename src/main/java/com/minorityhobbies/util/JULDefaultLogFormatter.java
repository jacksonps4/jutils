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