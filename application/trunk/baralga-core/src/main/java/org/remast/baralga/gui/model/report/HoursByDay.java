package org.remast.baralga.gui.model.report;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

/**
 * Item of the hours by day report.
 * @author remast
 */
public class HoursByDay {
    
    /** The week of the year. */
    private Date day;
    
    /** The amount of hours worked that week. */
    private double hours;
    
    public HoursByDay(final Date day, final double hours) {
        this.day = day;
        this.hours = hours;
    }

    /**
     * @return the week
     */
    public Date getDay() {
        return day;
    }

    /**
     * @return the hours
     */
    public double getHours() {
        return hours;
    }
    
    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof HoursByDay)) {
            return false;
        }

        final HoursByDay accAct = (HoursByDay) that;
        return DateUtils.isSameDay(this.getDay(), accAct.getDay());
    }

    /**
     * Adds the given hours to the hours on that day.
     * @param additionalHours the hours to add
     */
    public void addHours(final double additionalHours) {
        this.hours += additionalHours;
    }

}
