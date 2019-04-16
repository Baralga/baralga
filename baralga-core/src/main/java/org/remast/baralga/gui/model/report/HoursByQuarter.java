package org.remast.baralga.gui.model.report;

import com.google.common.base.Objects;

import java.util.Date;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jfree.data.time.Quarter;
import org.joda.time.DateTime;

/**
 * Item of the hours by quater report.
 * @author remast
 */
public class HoursByQuarter implements Comparable<HoursByQuarter> {
    
    private DateTime date;
    
    /** The quarter of the year. */
    private Quarter quarter;
    
    /** The amount of hours worked that month. */
    private double hours;
    
    public HoursByQuarter(final DateTime date, final double hours) {
	this.date = date;
        this.quarter = new Quarter(date.toDate());
        this.hours = hours;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date.toDate();
    }

    /**
     * @return the quarter
     */
    public Quarter getQuarter() {
        return quarter;
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
        if (!(that instanceof HoursByQuarter)) {
            return false;
        }

        final HoursByQuarter accAct = (HoursByQuarter) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.date.getYear(), accAct.date.getYear());
        eqBuilder.append(this.quarter.getQuarter(), accAct.quarter.getQuarter());
        
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
    public int compareTo(final HoursByQuarter that) {
        if (that == null) {
            return 0;
        }
        
        if (this.equals(that)) {
            return 0;
        }
        
        final CompareToBuilder compareBuilder = new CompareToBuilder();
        
        compareBuilder.append(date.getYear(), that.date.getYear());
        compareBuilder.append(quarter.getQuarter(), that.quarter.getQuarter());
        
        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return compareBuilder.toComparison() * -1;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(
                date.getYear(),
                quarter.getQuarter()
        );
    }

}
