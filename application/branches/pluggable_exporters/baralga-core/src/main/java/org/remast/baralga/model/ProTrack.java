package org.remast.baralga.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Data model of Baralga. This consists mainly of project and activities for these projects.
 * @author remast
 */
@XStreamAlias("proTrack") //$NON-NLS-1$
public class ProTrack implements ReadableBaralgaData, Serializable {

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
	private DateTime startTime;

	/** The currently active project (if any). */
	private Project activeProject;

	public ProTrack() {
	}

	/**
	 * Adds a new project.
	 * @param project the project to add
	 */
	public synchronized void add(final Project project) {
		if (project == null) {
			return;
		}
		
		this.activeProjects.add(project);
	}

	/**
	 * Removes a project.
	 * @param project the project to remove
	 */
	public synchronized void remove(final Project project) {
		if (project == null) {
			return;
		}
		
		this.activeProjects.remove(project);
		this.projectsToBeDeleted.add(project);
	}

	public synchronized void cleanup() {
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
	public synchronized boolean isActive() {
		return active;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public synchronized void start(final DateTime startTime) {
		this.active = true;
		this.startTime = startTime;
	}
	
	public synchronized void stop() {
        this.active = false;
    }

	public synchronized DateTime getStart() {
	    return startTime;
	}
	
    public synchronized void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

	/**
	 * @return the activeProject
	 */
	public synchronized Project getActiveProject() {
		return activeProject;
	}

	/**
	 * @param activeProject the activeProject to set
	 */
	public synchronized void setActiveProject(final Project activeProject) {
		this.activeProject = activeProject;
	}

	/**
	 * @return read-only view of the activities
	 */
	public synchronized List<ProjectActivity> getActivities() {
		return Collections.unmodifiableList(activities);
	}
	
	/**
	 * Adds a new activity.
	 */
	public synchronized void addActivity(final ProjectActivity activity) {
	    this.activities.add(activity);
	}
	
	/**
     * Removes an activity.
     */
    public synchronized boolean removeActivity(final ProjectActivity activity) {
        return this.activities.remove(activity);
    }

	/**
	 * @return read-only view of the projects
	 */
	public synchronized List<Project> getProjects() {
		return Collections.unmodifiableList(activeProjects);
	}

	/**
	 * Replaces an old activity with a new, updated activity.
	 */
    public synchronized void replaceActivity(ProjectActivity oldActivity,
            ProjectActivity newActivity) {
        removeActivity(oldActivity);
        addActivity(newActivity);
    }

    /**
     * Replaces an old project with a new, updated project.
     */
    public synchronized void replaceProject(Project oldProject, Project newProject) {
        remove(oldProject);
        add(newProject);
    }
}
