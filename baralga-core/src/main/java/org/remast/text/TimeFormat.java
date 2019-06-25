package org.remast.text;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Custom format for time in format HH:mm e.g. 12:00.
 * @author remast
 */
@SuppressWarnings("serial")
public class TimeFormat extends DateFormat {

    /**
     * Definition of the time format to parse.
     */
    public static final String HHMM_FORMAT = "HH:mm"; //$NON-NLS-1$

    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(HHMM_FORMAT);
    
    private static final DateFormat timeFormat = new SimpleDateFormat(HHMM_FORMAT);


    public static LocalTime parseTime(final String time) throws ParseException {
        return LocalTime.parse(time, dateTimeFormat);
    }

    /**
     * @param date
     * @param toAppendTo
     * @param fieldPosition
     * @return
     * @see java.text.DateFormat#format(java.util.Date, java.lang.StringBuffer, java.text.FieldPosition)
     */
    public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition fieldPosition) {
        synchronized (timeFormat) {
            return timeFormat.format(date, toAppendTo, fieldPosition);
        }
    }

    /**
     * @param source
     * @param pos
     * @return
     * @see java.text.DateFormat#parse(java.lang.String, java.text.ParsePosition)
     */
    public Date parse(final String source, final ParsePosition pos) {
        synchronized (timeFormat) {
            return timeFormat.parse(source, pos);
        }
    }

}
