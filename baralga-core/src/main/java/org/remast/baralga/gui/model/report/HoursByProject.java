package org.remast.baralga.gui.model.report;

import org.remast.baralga.model.Project;

import java.util.Objects;

/**
 * Item of the hours by project report.
 * @author remast
 */
public class HoursByProject implements Comparable<HoursByProject> {
    
    /** The project. */
    private Project project;
    
    /** The amount of hours worked that week. */
    private double hours;
    
    public HoursByProject(final Project project, final double hours) {
        this.project = project;
        this.hours = hours;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
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
        if (!(that instanceof HoursByProject)) {
            return false;
        }

        final HoursByProject accAct = (HoursByProject) that;
        return Objects.equals(this.getProject(), accAct.getProject());
    }

    /**
     * Adds the given hours to the hours on that day.
     * @param that the hours to add
     */
    public void addHours(final double that) {
        this.hours += that;
    }

    @Override
    public int compareTo(final HoursByProject hoursByProject) {
        if (hoursByProject == null) {
            return 0;
        }
        
        if (this.equals(hoursByProject)) {
            return 0;
        }

        return this.getProject().compareTo(hoursByProject.getProject());
    }

    @Override
    public int hashCode() {
        return this.getProject().hashCode();
    }
}
