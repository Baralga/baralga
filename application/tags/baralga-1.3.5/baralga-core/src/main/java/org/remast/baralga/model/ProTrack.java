package org.remast.baralga.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Data model of Baralga. This consists mainly of project and activities for these projects.
 * @author remast
 */
@XStreamAlias("proTrack") //$NON-NLS-1$
public class ProTrack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** All active projects. */
	private final List<Project> activeProjects = new ArrayList<Project>();

	/** All projects that are not active any more but may be referenced in project activities. */
	private final List<Project> projectsToBeDeleted = new ArrayList<Project>();

	/** All project activities. */
	private final List<ProjectActivity> activities = new ArrayList<ProjectActivity>();

	/** Flag indicating a currently active activity. */
	@XStreamAsAttribute
	private boolean active = false;

	/** The start time of the current activity (if any). */
	@XStreamAsAttribute
	private Date startTime;

	/** The currently active project (if any). */
	private Project activeProject;

	public ProTrack() {
	}

	/**
	 * Adds a new project.
	 * @param project the project to add
	 */
	public void add(final Project project) {
		if (project == null) {
			return;
		}
		
		this.activeProjects.add(project);
	}

	/**
	 * Removes a project.
	 * @param project the project to remove
	 */
	public void remove(final Project project) {
		if (project == null) {
			return;
		}
		
		this.activeProjects.remove(project);
		this.projectsToBeDeleted.add(project);
	}

	public void cleanup() {
		if (this.projectsToBeDeleted == null) {
			return;
		}

		final List<Project> referencedProjects = new ArrayList<Project>();        
		for(ProjectActivity projectActivity : this.activities) {
			Project project = projectActivity.getProject();
			referencedProjects.add(project);
		}

		final List<Project> removableProjects = new ArrayList<Project>();
		for (Project oldProject : this.projectsToBeDeleted) {
			if (!referencedProjects.contains(oldProject)) {
				removableProjects.add(oldProject);
			}
		}

		this.projectsToBeDeleted.removeAll(removableProjects);
	}

	/**
	 * Looks for a project with given id.
	 * @param id the id of the project to look for
	 * @return the project or <code>null</code>
	 */
	public Project findProjectById(final long id) {
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
	public void setActive(final boolean active) {
		this.active = active;
	}

	public DateTime getStart() {
	    return new DateTime(startTime);
	}

	/**
     * @param startTime the startTime to set
     */
    public void setStartTime(final DateTime startTime) {
        this.startTime = startTime.toDate();
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
	public void setActiveProject(final Project activeProject) {
		this.activeProject = activeProject;
	}

	/**
	 * @return the activities
	 */
	public List<ProjectActivity> getActivities() {
		return activities;
	}

	/**
	 * @return the projects
	 */
	public List<Project> getProjects() {
		return activeProjects;
	}
}
