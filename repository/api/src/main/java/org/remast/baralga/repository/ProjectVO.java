package org.remast.baralga.repository;

public class ProjectVO {

    private String id;

    /** The title of the project. */
    private String title;

    /** A description of the project. */
    private String description;

    /** Flag that shows whether the project is active or not. */
    private boolean active = true;

    public ProjectVO(final String id, final String title, final String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
