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
    
    private static final DateFormat timeFormat = new SmartTimeFormat();
    
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TimeFormat.HHMM_FORMAT);
       
    public static String formatTime(final DateTime date) {
        return timeFormatter.print(date);
    }
    
    public static DateTime parseTime(final String time) throws ParseException {
        synchronized (timeFormat) {
            return new DateTime( timeFormat.parse(time) );
        }
    }
    
    public static DateFormat createTimeFormat() {
        return new SmartTimeFormat();
    }
    
    // ------------------------------------------------
    // Number Formats
    // ------------------------------------------------
    public static final NumberFormat durationFormat = new DecimalFormat("#0.00"); //$NON-NLS-1$
    
}
