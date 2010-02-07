package org.remast.baralga.gui.model.report;

import java.util.Date;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

/**
 * Item of the hours by week report.
 * @author remast
 */
public class HoursByWeek implements Comparable<HoursByWeek> {
    
    /** The week of the year. */
    private DateTime week;
    
    /** The amount of hours worked that week. */
    private double hours;
    
    public HoursByWeek(final DateTime week, final double hours) {
        this.week = week;
        this.hours = hours;
    }

    /**
     * @return the week
     */
    public Date getWeek() {
        return week.toDate();
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
        
        eqBuilder.append(this.week.getYear(), accAct.week.getYear());
        eqBuilder.append(this.week.getWeekOfWeekyear(), accAct.week.getWeekOfWeekyear());
        
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
    public int compareTo(final HoursByWeek that) {
        if (that == null) {
            return 0;
        }
        
        if (this.equals(that)) {
            return 0;
        }

        final CompareToBuilder compareBuilder = new CompareToBuilder();

        compareBuilder.append(this.week.getYear(), that.week.getYear());
        compareBuilder.append(this.week.getWeekOfWeekyear(), that.week.getWeekOfWeekyear());

        
        return compareBuilder.toComparison();
    }
    
    @Override
    public int hashCode() {
    	final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
    	
    	hashCodeBuilder.append(week.getYear());
    	hashCodeBuilder.append(week.getWeekOfWeekyear());
    	
        return hashCodeBuilder.toHashCode();
    }

}
