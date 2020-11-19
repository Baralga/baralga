package org.remast.baralga.repository;

import org.joda.time.DateTime;

public class ActivityVO {

    /** The unique identifier of the project. */
    private String id;

    /** Start date of this activity. */
    private DateTime start;

    /** End date of this activity. */
    private DateTime end;

    /** The description of this activity. */
    private String description;

    private ProjectVO project;

    public ActivityVO(final DateTime start, final DateTime end, final ProjectVO project) {
        this(start, end, null, project);
    }

    public ActivityVO(final DateTime start, final DateTime end, final String description, final ProjectVO project) {
        this.start = start;
        this.end = end;
        this.description = description;
        this.project = project;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectVO getProject() {
        return project;
    }

    public void setProject(ProjectVO project) {
        this.project = project;
    }

}
