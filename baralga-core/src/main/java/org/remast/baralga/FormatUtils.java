package org.remast.baralga;

import org.remast.text.TimeFormat;
import org.remast.util.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/** Utility class for formatting. */
public abstract class FormatUtils {
    
    /** Hide constructor in utility class. */
    private FormatUtils() { }

    // ------------------------------------------------
    // Date Formats
    // ------------------------------------------------
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TimeFormat.HHMM_FORMAT);

    /** Format for one in report. */
	public static DateFormat DAY_FORMAT = new SimpleDateFormat(" EE");

    /**
     * Format the given date as time.
     * @param date the date to format
     * @return the formatted time
     */
    public static String formatTime(final LocalDateTime date) {
    	if (date == null) {
    		return null;
    	}
    	
        return TIME_FORMATTER.format(date);
    }
    
    /**
     * Format the given date as date.
     * @param date the date to format
     * @return the formatted date
     */
    public static String formatDate(final LocalDateTime date) {
    	if (date == null) {
    		return null;
    	}
    	
        return DateFormat.getDateInstance().format(DateUtils.convertToDate(date));
    }
    
    /**
     * Format the given date as day.
     * @param date the date to format
     * @return the formatted date
     */
    public static String formatDay(final LocalDateTime date) {
    	if (date == null) {
    		return null;
    	}
    	
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(date) + DAY_FORMAT.format(DateUtils.convertToDate(date));
    }

}
