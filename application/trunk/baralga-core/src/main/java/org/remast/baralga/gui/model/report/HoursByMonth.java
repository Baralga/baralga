package org.remast.baralga.gui.model.report;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Item of the hours by month report.
 * @author remast
 */
public class HoursByMonth implements Comparable<HoursByMonth> {
    
    /** The month of the year. */
    private int month;
    
    /** The amount of hours worked that month. */
    private double hours;
    
    public HoursByMonth(final int month, final double hours) {
        this.month = month;
        this.hours = hours;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
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
        if (!(that instanceof HoursByMonth)) {
            return false;
        }

        final HoursByMonth accAct = (HoursByMonth) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.getMonth(), accAct.getMonth());
        return eqBuilder.isEquals();
    }

    /**
     * Adds the given hours to the hours in this month.
     * @param additionalHours the hours to add
     */
    public void addHours(final double additionalHours) {
        this.hours += additionalHours;
    }

    @Override
    public int compareTo(final HoursByMonth that) {
        if (that == null) {
            return 0;
        }
        
        if (this.equals(that)) {
            return 0;
        }
        
        return Integer.valueOf(this.month).compareTo(that.getMonth());
    }
    
    @Override
    public int hashCode() {
        return this.getMonth();
    }

}
