/**
 * 
 */
package org.remast.baralga.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import org.remast.baralga.gui.utils.Constants;
import org.remast.baralga.model.utils.ProTrackUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author remast
 */
@XStreamAlias("projectActivity") //$NON-NLS-1$
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
     * @param description the description to set
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
     * @param end the end to set
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
     * @param project the project to set
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
     * @param start the start to set
     */
    public void setStart(final Date start) {
        this.start = start;
    }
    
    @Override
    public String toString() {
        
        return DateFormat.getDateInstance(DateFormat.SHORT).format(this.start) +  " " + Constants.hhMMFormat.format(this.start) + " - " + Constants.hhMMFormat.format(this.end) + " (" + Constants.durationFormat.format(ProTrackUtils.calculateDuration(this)) + "h) " + this.project;
    }

    @Override
    public int compareTo(ProjectActivity activity) {
        if (activity == null) {
            return 0;
        }
        
        // Sort by start date.
        return this.getStart().compareTo(activity.getStart());
    }
}
