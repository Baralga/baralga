package org.remast.baralga.gui.model.report;

import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

/**
 * Item of the hours by month report.
 * @author remast
 */
public class HoursByMonth implements Comparable<HoursByMonth> {
    
    /** The month of the year. */
    private DateTime month;
    
    /** The amount of hours worked that month. */
    private double hours;
    
    public HoursByMonth(final DateTime month, final double hours) {
        this.month = month;
        this.hours = hours;
    }

    /**
     * @return the month
     */
    public Date getMonth() {
        return month.toDate();
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
        eqBuilder.append(this.month.getYear(), accAct.month.getYear());
        eqBuilder.append(this.month.getMonthOfYear(), accAct.month.getMonthOfYear());
        
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
        
        final CompareToBuilder compareBuilder = new CompareToBuilder();
        
        compareBuilder.append(month.getYear(), that.month.getYear());
        compareBuilder.append(month.getMonthOfYear(), that.month.getMonthOfYear());
        
        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return compareBuilder.toComparison() * -1;
    }
    
    @Override
    public int hashCode() {
    	final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
    	
    	hashCodeBuilder.append(month.getYear());
    	hashCodeBuilder.append(month.getMonthOfYear());
    	
        return hashCodeBuilder.toHashCode();
    }

}
