package org.remast.baralga.model.report;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.remast.baralga.model.Project;
import org.remast.util.DateUtils;

import java.time.LocalDateTime;
import java.util.Date;

public class AccumulatedProjectActivity implements Comparable<AccumulatedProjectActivity> {

    private final LocalDateTime day;

    private final Project project;

    private double time;

    public AccumulatedProjectActivity(final Project project, final LocalDateTime day, final double time) {
        this.project = project;
        this.day = day;
        this.time = time;
    }

    /**
     * @return the day
     */
    public Date getDay() {
        return DateUtils.convertToDate(day);
    }
    
    /**
     * @return the day
     */
    public LocalDateTime getDayDateTime() {
    	return day;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /** Adds the given time to this accumulated activity. */
    public void addTime(final double toAdd) {
        this.time += toAdd;
    }

    @Override
    public String toString() {
        return this.project.toString() + " " + this.time; //$NON-NLS-1$
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        
        if (!(that instanceof AccumulatedProjectActivity)) {
            return false;
        }

        final AccumulatedProjectActivity accAct = (AccumulatedProjectActivity) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();

        eqBuilder.append(this.getProject(), accAct.getProject());
        eqBuilder.append(this.day.getDayOfYear(), accAct.day.getDayOfYear());
        eqBuilder.append(this.day.getYear(), accAct.day.getYear());
        
        return eqBuilder.isEquals();
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(
                this.getProject(),
                this.day.getDayOfYear(),
                this.day.getYear()
        );
    }
    
    @Override
    public int compareTo(final AccumulatedProjectActivity activity) {
        if (activity == null) {
            return 0;
        }
        
        final CompareToBuilder compareBuilder = new CompareToBuilder();

        // :INFO: Don't use project for comparison as this corrupts ordering by day.
        // compareBuilder.append(this.getProject(), activity.getProject());
        compareBuilder.append(this.day.getDayOfYear(), activity.day.getDayOfYear());
        compareBuilder.append(this.day.getYear(), activity.day.getYear());
        
        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return compareBuilder.toComparison() * -1;
    }
}
