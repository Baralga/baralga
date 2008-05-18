package org.remast.util;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

/**
 * Miscellaneous utility methods for dealing with dates.
 * @author remast
 */
public abstract class DateUtils {
    
    /** Hide constructor. */
    private DateUtils()  {}
    
    /**
     * Get current time rounded to minutes.
     * @return
     */
    public static Date getNow() {
        DateTime now = new DateTime();
        DateTime nowRounded = now.minuteOfDay().roundHalfCeilingCopy();
        return nowRounded.toDate();
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
     * Checks if the given dates are in the same year and month.
     */
    public static boolean isSameMonth(Date date1, Date date2) {
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
     */
    private static boolean isSameMonth(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
    }

    /**
     * Checks if the given dates are in the same year.
     */
    public static boolean isSameYear(Date date1, Date date2) {
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
    private static boolean isSameYear(Calendar calendar1, Calendar calendar2) {
        if (calendar1 == null || calendar2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return (calendar1.get(Calendar.ERA) == calendar2.get(Calendar.ERA) &&
                calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR));
    }

    /**
     * Checks if the given dates are in the similar month. E.g. both dates
     * are in march.
     */
    public static boolean isSimilarMonth(Date date1, Date date2) {
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
    private static boolean isSimilarMonth(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

}
