package org.remast.util;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

public class DateUtils {
    
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
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(day);
        
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(timeToAdjust);
        
        timeCal.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
        timeCal.set(Calendar.DAY_OF_YEAR, cal1.get(Calendar.DAY_OF_YEAR));
        return timeCal.getTime();
    }
    
    public static boolean isSameMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameMonth(cal1, cal2);
    }

    private static boolean isSameMonth(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
    }

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

    private static boolean isSameYear(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
    }

    public static boolean isSimilarMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSimilarMonth(cal1, cal2);
        }

    private static boolean isSimilarMonth(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null"); //$NON-NLS-1$
        }
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

}
