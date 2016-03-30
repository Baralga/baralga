package org.remast.baralga.gui.model.report;

import com.google.common.base.Objects;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;

import java.util.Date;

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
        
        final EqualsBuilder equalsBuilder = new EqualsBuilder();
        
        equalsBuilder.append(day.getYear(), accAct.day.getYear());
        equalsBuilder.append(day.getDayOfYear(), accAct.day.getDayOfYear());

        return equalsBuilder.isEquals();
    }

    /**
     * Adds the given hours to the hours on that day.
     * @param additionalHours the hours to add
     */
    public void addHours(final double additionalHours) {
        this.hours += additionalHours;
    }

    @Override
    public int compareTo(final HoursByDay that) {
        if (that == null) {
            return 0;
        }
        
        if (this.equals(that)) {
            return 0;
        }
        
        final CompareToBuilder compareBuilder = new CompareToBuilder();
        
        compareBuilder.append(day.getYear(), that.day.getYear());
        compareBuilder.append(day.getDayOfYear(), that.day.getDayOfYear());
        
        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return compareBuilder.toComparison();
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(
            day.getYear(),
            day.getDayOfYear()
        );
    }

}
