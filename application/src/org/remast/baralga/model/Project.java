package org.remast.baralga.model;

import java.io.Serializable;

import org.remast.baralga.model.report.AccumulatedProjectActivity;
import org.remast.util.EqualsUtil;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("project") //$NON-NLS-1$
public class Project implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private long id;
    
    private String title;
    
    private String description;
    
    public Project(long id, String title, String description) {
        setId(id);
        setTitle(title);
        setDescription(description);
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
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return getTitle();
    }
    
    @Override
    public boolean equals(Object that) {
        if ( this == that ) return true;
        if ( !(that instanceof AccumulatedProjectActivity) ) return false;
        
        Project project = (Project) that;
        return EqualsUtil.areEqual(this.getId(), project.getId());
    }
}
