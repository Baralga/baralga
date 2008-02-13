package org.remast.baralga.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("proTrack") //$NON-NLS-1$
public class ProTrack implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<Project> activeProjects;

    private List<Project> projectsToBeDeleted;

    private List<ProjectActivity> activities;

    @XStreamAsAttribute
    private boolean active = false;

    @XStreamAsAttribute
    private Date startTime;
    
    private Project activeProject;

    public ProTrack() {
        this.activeProjects = new ArrayList<Project>();
        this.projectsToBeDeleted = new ArrayList<Project>();
        this.activities = new ArrayList<ProjectActivity>();
    }

    /**
     * @return the activities
     */
    public List<ProjectActivity> getActivities() {
        return activities;
    }

    /**
     * @param activities the activities to set
     */
    public void setActivities(List<ProjectActivity> activities) {
        this.activities = activities;
    }

    /**
     * @return the projects
     */
    public List<Project> getProjects() {
        return activeProjects;
    }

    public void add(Project project) {
        this.activeProjects.add(project);
    }

    public void remove(Project project) {
        this.activeProjects.remove(project);
        if(this.projectsToBeDeleted == null)
            this.projectsToBeDeleted = new ArrayList<Project>();
        this.projectsToBeDeleted.add(project);
    }

    public void cleanup() {
        if(this.projectsToBeDeleted == null)
            return;
        
        List<Project> referencedProjects = new Vector<Project>();        
        for(ProjectActivity projectActivity : this.activities) {
            Project project = projectActivity.getProject();
            referencedProjects.add(project);
        }
        
        List<Project> removeP = new Vector<Project>();
        for (Project oldProject : this.projectsToBeDeleted) {
            if(!referencedProjects.contains(oldProject))
                removeP.add(oldProject);
        }
        this.projectsToBeDeleted.removeAll(removeP);
    }
    
    public Project findProjectById(long id) {
        for (Project project : getProjects()) {
            if (id == project.getId()) {
                return project;
            }
        }
        
        return null;

    }

    
    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    /**
     * @return the activeProject
     */
    public Project getActiveProject() {
        return activeProject;
    }

    /**
     * @param activeProject the activeProject to set
     */
    public void setActiveProject(Project activeProject) {
        this.activeProject = activeProject;
    }
}
