package org.remast.baralga;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

import org.remast.text.SmartTimeFormat;

/** Utility class for formatting. */
public abstract class FormatUtils {
    
    /** Hide constructor in utility class. */
    private FormatUtils() { }

    // ------------------------------------------------
    // Date Formats
    // ------------------------------------------------

    private static final DateFormat timeFormat = new SmartTimeFormat();
       
    public static String formatTime(final Date date) {
        synchronized (timeFormat) {
            return timeFormat.format(date);
        }
    }
    
    public static Date parseTime(final String time) throws ParseException {
        synchronized (timeFormat) {
            return timeFormat.parse(time);
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
