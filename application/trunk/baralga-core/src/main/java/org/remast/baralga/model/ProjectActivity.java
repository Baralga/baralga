/**
 * 
 */
package org.remast.baralga.model;

import java.io.Serializable;
import java.util.Date;

import org.remast.gui.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author remast
 */
@XStreamAlias("projectActivity")//$NON-NLS-1$
public class ProjectActivity implements Serializable, Comparable<ProjectActivity> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** Start date of this activity. */
    private Date start;

    /** End date of this activity. */
    private Date end;

    /** The project associated with this activity. */
    private Project project;

    /** The description of this activity. */
    private String description;

    public static final String PROPERTY_START = "org.remast.baralga.model.ProjectActivity.start";

    public static final String PROPERTY_END = "org.remast.baralga.model.ProjectActivity.end";

    /** Artificial property if the day in year of the activity changes. */
    public static final String PROPERTY_DATE = "org.remast.baralga.model.ProjectActivity.date";

    public static final String PROPERTY_PROJECT = "org.remast.baralga.model.ProjectActivity.project";

    public static final String PROPERTY_DESCRIPTION = "org.remast.baralga.model.ProjectActivity.description";

    public ProjectActivity(final Date start, final Date end, final Project project) {
        this.start = start;
        this.end = end;
        this.project = project;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * @param end
     *            the end to set
     */
    public void setEnd(final Date end) {
        this.end = end;
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
    public void setProject(final Project project) {
        this.project = project;
    }

    /**
     * @return the start
     */
    public Date getStart() {
        return start;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStart(final Date start) {
        this.start = start;
    }

    @Override
    public String toString() {

        return Constants.dateFormat.format(this.start) + " "
                + Constants.HHmmFormat.format(this.start) + " - " + Constants.HHmmFormat.format(this.end) + " ("
                + Constants.durationFormat.format(this.getDuration()) + "h) " + this.project;
    }

    public int compareTo(ProjectActivity activity) {
        if (activity == null) {
            return 0;
        }

        // Sort by start date.
        return this.getStart().compareTo(activity.getStart());
    }
    
    /**
     * Calculate the duration of the given activity in decimal hours.
     * @return decimal value of the duration (e.g. for 30 minutes, 0.5 and so on)
     */    
    public double getDuration() {
        long timeMilliSec = end.getTime() - start.getTime();
        long timeMin = timeMilliSec / 1000 / 60;
        long hours = timeMin / 60;

        long mins = timeMin % 60;
        double minsD = Math.round(mins * (1 + 2.0 / 3.0)) / 100.0;

        return hours + minsD;
    }
}
