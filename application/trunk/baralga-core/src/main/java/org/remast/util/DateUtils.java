package org.remast.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

/**
 * Miscellaneous utility methods for dealing with dates.
 * 
 * @author remast
 */
public abstract class DateUtils {
    
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

    /**
     * Sets <code>timeToAdjust</code> to the same year and week-of-year as <code>day</code>.
     * 
     * @param midnightOnNextDay if <code>true</code> treats midnight (0:00h) as belonging to the next day.
     *   Otherwise 0:00h is treated as being the start of the current day.
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
     * Returns <code>true</code> iff the first time is before or equal to the second time.
     *
     * @param time1 the first time
     * @param time2 the second time
     */
    public static boolean isBeforeOrEqual(final ReadableInstant time1, final ReadableInstant time2) {
        return time1.compareTo(time2) <= 0;
    }
}
