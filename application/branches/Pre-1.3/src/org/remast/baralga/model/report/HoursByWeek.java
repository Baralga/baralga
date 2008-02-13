package org.remast.baralga.model.report;

import org.apache.commons.lang.builder.EqualsBuilder;

public class HoursByWeek {
    
    /** The week of the year. */
    private int week;
    
    /** The amount of hours worked that week. */
    private double hours;
    
    public HoursByWeek(int week, double hours) {
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
    public boolean equals(Object that) {
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
    public void addHours(double additionalHours) {
        this.hours += additionalHours;
    }

}
