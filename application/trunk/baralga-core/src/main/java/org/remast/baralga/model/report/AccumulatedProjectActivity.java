package org.remast.baralga.model.report;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.remast.baralga.model.Project;

public class AccumulatedProjectActivity implements Comparable<AccumulatedProjectActivity> {

    private final DateTime day;

    private final Project project;

    private double time;

    public AccumulatedProjectActivity(final Project project, final DateTime day, final double time) {
        this.project = project;
        this.day = day;
        this.time = time;
    }

    /**
     * @return the day
     */
    public Date getDay() {
        return day.toDate();
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
        // Unique for each project so use hash code of project
        return this.getProject().hashCode();
    }
    
    @Override
    public int compareTo(final AccumulatedProjectActivity activity) {
        if (activity == null) {
            return 0;
        }
        
        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return getDay().compareTo(activity.getDay()) * -1;
    }
}
