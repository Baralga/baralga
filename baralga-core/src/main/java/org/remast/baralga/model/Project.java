package org.remast.baralga.model;

import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;

public class Project implements Serializable, Comparable<Project> {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The unique identifier of the project. */
    private long id;
    
    /** The title of the project. */
    private String title;
    public static final String PROPERTY_TITLE = "org.remast.baralga.model.title";
    
    /** A description of the project. */
    private String description;

    /** Flag that shows whether the project is active or not. */
    private boolean active = true;
    public static final String PROPERTY_ACTIVE = "org.remast.baralga.model.active";    

    /**
     * Creates a new project.
     * @param id the unique id
     * @param title the project title
     * @param description the project description
     */
    public Project(final long id, final String title, final String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
    	this.id = id;
    }

    /**
     * Getter for the title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Setter for the title.
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
        
        // Use title also as description for the moment.
        this.description = title;
    }

    /**
     * Getter for the active flage.
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for the active flage
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return getTitle();
    }
    
    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        
        if (!(that instanceof Project)) {
            return false;
        }
        
        final Project project = (Project) that;
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.getId(), project.getId());
        
        return eqBuilder.isEquals();
    }

    @Override
    public int compareTo(final Project project) {
        if (project == null || this.getTitle() == null || project.getTitle() == null) {
            return 0;
        }
        
        // Compare the title only.
        return this.getTitle().compareTo(project.getTitle());
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }
}
