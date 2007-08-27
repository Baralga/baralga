/**
 * 
 */
package org.remast.baralga.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("projectActivity") //$NON-NLS-1$
public class ProjectActivity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Date start;
    
    private Date end;
    
    private Project project;
    
    private String description;

    public ProjectActivity(Date start, Date end, Project project) {
        assert(DateUtils.isSameDay(start, end));

        setStart(start);
        setEnd(end);
        setProject(project);
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
    public void setDescription(String description) {
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
    public void setEnd(Date end) {
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
    public void setProject(Project project) {
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
    public void setStart(Date start) {
        this.start = start;
    }

    public Date getDay() {
        return org.remast.util.DateUtils.adjustToSameDay(start, new Date(0));
    }
}
