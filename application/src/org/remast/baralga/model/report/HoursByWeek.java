package org.remast.baralga.model.report;

import org.remast.util.EqualsUtil;

public class HoursByWeek {
    
    private int week;
    
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
     * @param week the week to set
     */
    public void setWeek(int week) {
        this.week = week;
    }

    /**
     * @return the hours
     */
    public double getHours() {
        return hours;
    }

    /**
     * @param hours the hours to set
     */
    public void setHours(double hours) {
        this.hours = hours;
    }
    
    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (!(that instanceof HoursByWeek))
            return false;

        // :TODO: Replace by EqualsBuilder
        HoursByWeek accAct = (HoursByWeek) that;
        return EqualsUtil.areEqual(this.getWeek(), accAct.getWeek());
    }

    public void addHours(double additionalHours) {
        this.hours += additionalHours;
    }

}
