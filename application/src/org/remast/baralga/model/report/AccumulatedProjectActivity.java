package org.remast.baralga.model.report;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.time.DateUtils;
import org.remast.baralga.model.Project;

public class AccumulatedProjectActivity {

    Date day;

    Project project;

    double time;

    public AccumulatedProjectActivity(Project project, Date day, double time) {
        this.project = project;
        this.day = day;
        this.time = time;
    }

    /**
     * @return the day
     */
    public Date getDay() {
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

    public void addTime(double toAdd) {
        this.time += toAdd;
    }

    @Override
    public String toString() {
        return this.project.toString() + " " + this.time; //$NON-NLS-1$
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof AccumulatedProjectActivity)) {
            return false;
        }

        AccumulatedProjectActivity accAct = (AccumulatedProjectActivity) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.getProject(), accAct.getProject());
        
        return eqBuilder.isEquals() && DateUtils.isSameDay(this.getDay(), accAct.getDay());
    }
}
