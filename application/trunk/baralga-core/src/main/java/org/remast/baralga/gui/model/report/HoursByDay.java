package org.remast.baralga.gui.model.report;

import java.util.Date;

import org.joda.time.DateTime;

/**
 * Item of the hours by day report.
 * @author remast
 */
public class HoursByDay implements Comparable<HoursByDay> {
    
    /** The day of the year. */
    private DateTime day;
    
    /** The amount of hours worked that week. */
    private double hours;
    
    public HoursByDay(final DateTime day, final double hours) {
        this.day = day;
        this.hours = hours;
    }

    /**
     * @return the week
     */
    public Date getDay() {
        return day.toDate();
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
        return this.day.getDayOfYear() == accAct.day.getDayOfYear();
    }

    /**
     * Adds the given hours to the hours on that day.
     * @param additionalHours the hours to add
     */
    public void addHours(final double additionalHours) {
        this.hours += additionalHours;
    }

    @Override
    public int compareTo(HoursByDay that) {
        if (that == null) {
            return 0;
        }
        
        if (this.equals(that)) {
            return 0;
        }
        
        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return this.getDay().compareTo(that.getDay()) * -1;
    }
    
    @Override
    public int hashCode() {
        return this.getDay().hashCode();
    }

}
