package org.remast.baralga.gui.model.report;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Item of the hours by week report.
 * @author remast
 */
public class HoursByWeek implements Comparable<HoursByWeek> {
    
    /** The week of the year. */
    private int week;
    
    /** The amount of hours worked that week. */
    private double hours;
    
    public HoursByWeek(final int week, final double hours) {
        this.week = week;
        this.hours = hours;
    }

    /**
     * @return the week
     */
    public int getWeek() {
        return week;
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
        if (!(that instanceof HoursByWeek)) {
            return false;
        }

        final HoursByWeek accAct = (HoursByWeek) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.getWeek(), accAct.getWeek());
        return eqBuilder.isEquals();
    }

    /**
     * Adds the given hours to the hours in this week.
     * @param additionalHours the hours to add
     */
    public void addHours(final double additionalHours) {
        this.hours += additionalHours;
    }

    @Override
    public int compareTo(HoursByWeek that) {
        if (that == null) {
            return 0;
        }
        
        if (this.equals(that)) {
            return 0;
        }
        
        return Integer.valueOf(this.week).compareTo(that.getWeek());
    }
    
    @Override
    public int hashCode() {
        return this.getWeek();
    }

}
