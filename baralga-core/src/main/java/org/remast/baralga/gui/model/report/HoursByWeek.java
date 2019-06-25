package org.remast.baralga.gui.model.report;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.remast.util.DateUtils;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

/**
 * Item of the hours by week report.
 * @author remast
 */
public class HoursByWeek implements Comparable<HoursByWeek> {
    
    /** The week of the year. */
    private LocalDateTime week;
    
    /** The amount of hours worked that week. */
    private double hours;
    
    public HoursByWeek(final LocalDateTime week, final double hours) {
        this.week = week;
        this.hours = hours;
    }

    /**
     * @return the week
     */
    public Date getWeek() {
        return DateUtils.convertToDate(week);
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
        eqBuilder.append(this.week.get(WeekFields.of(Locale.getDefault()).weekOfYear()), accAct.week.get(WeekFields.of(Locale.getDefault()).weekOfYear()));
        
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
        compareBuilder.append(this.week.get(WeekFields.of(Locale.getDefault()).weekOfYear()), that.week.get(WeekFields.of(Locale.getDefault()).weekOfYear()));

        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return compareBuilder.toComparison() * -1;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(
                week.getYear(),
                week.get(WeekFields.of(Locale.getDefault()).weekOfYear())
        );
    }

}
