package org.remast.baralga.model.report;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.remast.baralga.model.Project;
import org.remast.util.EqualsUtil;

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
     * @param day
     *            the day to set
     */
    public void setDay(Date day) {
        this.day = day;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project
     *            the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @param time
     *            the time to set
     */
    public void setTime(double time) {
        this.time = time;
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
        if (this == that)
            return true;
        if (!(that instanceof AccumulatedProjectActivity))
            return false;

        // :TODO: Replace by EqualsBuilder
        AccumulatedProjectActivity accAct = (AccumulatedProjectActivity) that;
        return EqualsUtil.areEqual(this.getProject(), accAct.getProject())
                && DateUtils.isSameDay(this.getDay(), accAct.getDay());
    }
}
