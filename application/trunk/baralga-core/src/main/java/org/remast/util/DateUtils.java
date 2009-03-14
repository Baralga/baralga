package org.remast.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

/**
 * Miscellaneous utility methods for dealing with dates.
 * 
 * @author remast
 */
public abstract class DateUtils {
    
    /**
     * Definition of the time format to parse.
     */
    private static final String HHMM_FORMAT = "HH:mm"; //$NON-NLS-1$

    /** The format for a time with hours and minutes. */
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(HHMM_FORMAT);

    /** Hide constructor. */
    private DateUtils() {
    }

    /**
     * Get current time rounded to minutes.
     * @return
     */
    public static Date getNow() {
        return getNowAsDateTime().toDate();
    }
    
    /**
     * Get current time rounded to minutes.
     * @return
     */
    public static DateTime getNowAsDateTime() {
        DateTime now = new DateTime();
        DateTime nowRounded = now.minuteOfDay().roundHalfCeilingCopy();
        return nowRounded;
    }

    public static Date adjustToSameDay(final Date day, final Date timeToAdjust) {
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(day);

        final Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(timeToAdjust);

        timeCal.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
        timeCal.set(Calendar.DAY_OF_YEAR, cal1.get(Calendar.DAY_OF_YEAR));

        return timeCal.getTime();
    }
    
    /**
     * Sets <code>timeToAdjust</code> to the same year and week-of-year as <code>day</code>.
     * 
     * @param midnightOnNextDay iff <code>true</code> treats midnight (0:00h) as belonging to the next day
     */
    public static DateTime adjustToSameDay(final DateTime day, final DateTime timeToAdjust,
            boolean midnightOnNextDay) {
        DateTime result = timeToAdjust.withYear(day.getYear()).withDayOfYear(day.getDayOfYear());
        if( midnightOnNextDay && result.getHourOfDay() == 0 && result.getMinuteOfHour() == 0 ) {
            result = result.plusDays(1);
        }
        return result;
    }

    /**
     * Checks if the given dates are in the same year and month.
     */
    public static boolean isSameMonth(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }

        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return isSameMonth(cal1, cal2);
    }

    /**
     * Checks if the given calendars are in the same year and month.
     * @return whether year and month of the given calendars match
     */
    private static boolean isSameMonth(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
                .get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
    }

    /**
     * Checks if the given dates are in the same year.
     */
    public static boolean isSameYear(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameYear(cal1, cal2);
    }

    /**
     * Checks if the given calendars are in the same year.
     */
    private static boolean isSameYear(final Calendar calendar1, final Calendar calendar2) {
        if (calendar1 == null || calendar2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return (calendar1.get(Calendar.ERA) == calendar2.get(Calendar.ERA) && calendar1
                .get(Calendar.YEAR) == calendar2.get(Calendar.YEAR));
    }

    /**
     * Checks if the given dates are in the similar week of the year. E.g. both
     * dates are in march.
     */
    public static boolean isSimilarWeekOfYear(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return isSimilarWeekOfYear(cal1, cal2);
    }

    /**
     * Checks if the given calendars are in the similar month. E.g. both dates
     * are in march.
     */
    private static boolean isSimilarWeekOfYear(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return cal1.get(Calendar.WEEK_OF_YEAR) == cal2
        .get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Checks if the given dates are in the similar month. E.g. both dates are
     * in march.
     */
    public static boolean isSimilarMonth(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return isSimilarMonth(cal1, cal2);
    }

    /**
     * Checks if the given calendars are in the similar month. E.g. both dates
     * are in march.
     */
    private static boolean isSimilarMonth(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    /**
     * Parses a time from the given string. This is done smartly for easier use.
     * <h3>Examples</h3> 12 parsed as 12:00
     * @param time the string to be parsed
     * @throws ParseException on parse error
     * @return parsed time as {@link Date} object
     */
    public static Date parseTimeSmart(final String time) throws ParseException {
        if (StringUtils.isBlank(time)) {
            return null;
        }

        String timeToParse = time;

        if (timeToParse.length() == 1) {
            timeToParse = "0" + time;
        }

        if (timeToParse.length() == 2) {
            timeToParse = time + ":00";
        }

        synchronized (timeFormat) {
            try {
                return timeFormat.parse(timeToParse);
            } catch (ParseException e) {
                // Ignore
            }
        }

        throw new ParseException("Could not parse time from " + time + ".", -1);
    }

}
