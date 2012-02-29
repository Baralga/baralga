package org.remast.baralga;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.remast.text.TimeFormat;

/** Utility class for formatting. */
public abstract class FormatUtils {
    
    /** Hide constructor in utility class. */
    private FormatUtils() { }

    // ------------------------------------------------
    // Date Formats
    // ------------------------------------------------
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern(TimeFormat.HHMM_FORMAT);

    /** Format for one in report. */
	public static DateFormat DAY_FORMAT = new SimpleDateFormat(DateTimeFormat.patternForStyle("S-", Locale.getDefault()) + " EE");

    /**
     * Format the given date as time.
     * @param date the date to format
     * @return the formatted time
     */
    public static String formatTime(final DateTime date) {
    	if (date == null) {
    		return null;
    	}
    	
        return TIME_FORMATTER.print(date);
    }
    
    /**
     * Format the given date as date.
     * @param date the date to format
     * @return the formatted date
     */
    public static String formatDate(final DateTime date) {
    	if (date == null) {
    		return null;
    	}
    	
        return DateFormat.getDateInstance().format(date.toDate());
    }
    
    /**
     * Format the given date as day.
     * @param date the date to format
     * @return the formatted date
     */
    public static String formatDay(final DateTime date) {
    	if (date == null) {
    		return null;
    	}
    	
        return DAY_FORMAT.format(date.toDate());
    }
    
    
    
    // ------------------------------------------------
    // Number Formats
    // ------------------------------------------------
    
    public static final NumberFormat DURATION_FORMAT = new DecimalFormat("#0.00"); //$NON-NLS-1$
    
}
