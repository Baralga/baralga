package org.remast.baralga;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.remast.text.SmartTimeFormat;
import org.remast.text.TimeFormat;

/** Utility class for formatting. */
public abstract class FormatUtils {
    
    /** Hide constructor in utility class. */
    private FormatUtils() { }

    // ------------------------------------------------
    // Date Formats
    // ------------------------------------------------
    
    private static final ThreadLocal<DateFormat> timeFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SmartTimeFormat();
        } 
    };
    
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TimeFormat.HHMM_FORMAT);
       
    public static String formatTime(final DateTime date) {
        return timeFormatter.print(date);
    }
    
    public static DateTime parseTime(final String time) throws ParseException {
        return new DateTime( timeFormat.get().parse(time) );
    }
    
    /**
     * Returns a {@link DateFormat} instance which is safe to use in the current thread.
     */
    public static DateFormat getTimeFormat() {
        return timeFormat.get();
    }
    
    // ------------------------------------------------
    // Number Formats
    // ------------------------------------------------
    public static NumberFormat getDurationFormat() {
        return durationFormat.get();
    }

    private static final ThreadLocal<NumberFormat> durationFormat = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            return new DecimalFormat("#0.00"); //$NON-NLS-1$
        }
    };
}
