package org.remast.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Miscellaneous utility methods for dealing with dates.
 */
public class DateUtils {

    /**
     * Get current time rounded to minutes.
     * @return  the current time rounded to minutes
     */
    public static Date getNow() {
        return convertToDate(getNowAsDateTime());
    }
    
    /**
     * Get current time rounded to minutes.
     * @return
     */
    public static LocalDateTime getNowAsDateTime() {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime nowRounded = now.withSecond(0).withNano(0);
        return nowRounded;
    }

    /**
     * Sets <code>timeToAdjust</code> to the same year and week-of-year as <code>day</code>.
     * @param day the day to adjust to
     * @param timeToAdjust the time to be adjusted
     * @param midnightOnNextDay if <code>true</code> treats midnight (0:00h) as belonging to the next day.
     *   Otherwise 0:00h is treated as being the start of the current day.
     */
    public static LocalDateTime adjustToSameDay(final LocalDate day, final LocalDateTime timeToAdjust, final boolean midnightOnNextDay) {
        LocalDateTime result = timeToAdjust.withYear(day.getYear()).withDayOfYear(day.getDayOfYear());
        if (midnightOnNextDay && result.getHour() == 0 && result.getMinute() == 0) {
            result = result.plusDays(1);
        }
        return result;
    }

    /**
     * Returns <code>true</code> iff the first time is before or equal to the second time.
     * @param time1 the first time
     * @param time2 the second time
     */
    public static boolean isBeforeOrEqual(final LocalDateTime time1, final LocalDateTime time2) {
    	return time1.isBefore(time2) || time1.isEqual(time2);
    }
    
    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDateTime();
    }
    
    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
    }
    
    public static Date convertToDate(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
          .atZone(ZoneId.systemDefault())
          .toInstant());
    }
    
    public static Date convertToDate(LocalDateTime dateToConvert) {
        return java.util.Date
          .from(dateToConvert.atZone(ZoneId.systemDefault())
          .toInstant());
    }
}
