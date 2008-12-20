package org.remast.text;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class TimeFormat extends DateFormat {

    /**
     * Definition of the time format to parse.
     */
    private static final String HHMM_FORMAT = "HH:mm"; //$NON-NLS-1$

    private static final DateFormat timeFormat = new SimpleDateFormat(HHMM_FORMAT);

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
