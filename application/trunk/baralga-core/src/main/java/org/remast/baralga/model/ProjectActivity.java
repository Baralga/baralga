package org.remast.baralga.model;

import java.io.Serializable;
import java.util.Date;

import org.remast.baralga.FormatUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * An activity for a project.
 * @author remast
 */
@XStreamAlias("projectActivity")//$NON-NLS-1$
@SuppressWarnings(value={"EI_EXPOSE_REP","EI_EXPOSE_REP2"},
        justification="We trust callers that they won't change Dates we receive or hand out")
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
        return FormatUtils.getDateFormat().format(this.start) + " "
                + FormatUtils.timeFormat.format(this.start) + " - " + FormatUtils.timeFormat.format(this.end) + " ("
                + FormatUtils.durationFormat.format(this.getDuration()) + "h) " + this.project;
    }

    @Override
    public int compareTo(final ProjectActivity activity) {
        if (activity == null) {
            return 0;
        }

        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return this.getStart().compareTo(activity.getStart()) * -1;
    }
    
    /**
     * Calculate the duration of the given activity in decimal hours.
     * @return decimal value of the duration (e.g. for 30 minutes, 0.5 and so on)
     */    
    public final double getDuration() {
        final long timeMilliSec = end.getTime() - start.getTime();
        final long timeMin = timeMilliSec / 1000 / 60;
        final long hours = timeMin / 60;

        final long mins = timeMin % 60;
        final double minsD = Math.round(mins * (1 + 2.0 / 3.0)) / 100.0;

        return hours + minsD;
    }
}
