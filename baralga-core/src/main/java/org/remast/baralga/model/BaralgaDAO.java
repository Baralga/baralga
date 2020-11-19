package org.remast.baralga.model;

import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.repository.BaralgaRepository;
import org.remast.baralga.repository.FilterVO;
import org.remast.baralga.repository.ProjectVO;
import org.remast.baralga.repository.file.BaralgaFileRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads and writes all objects to the database and maintains the database connection.
 * @author remast
 */
public class BaralgaDAO {

	private BaralgaRepository repository = new BaralgaFileRepository();

	public void initialize() {
		repository.initialize();
	}

	/**
	 * Closes the database by closing the only connection to it.
	 */
	public void close() {
		repository.close();
	}

	/**
	 * Removes a project.
	 * @param project the project to remove
	 */
	public void remove(final Project project) {
		if (project == null) {
			return;
		}

		repository.remove(project.toVO());
	}

	/**
	 * Adds a new project.
	 * @param project the project to add
	 */
	public void addProject(final Project project) {
		if (project == null) {
			return;
		}

		if (project.getId() == null) {
			throw new IllegalArgumentException("Cannot add project without id.");
		}

		repository.addProject(project.toVO());
	}

	/**
	 * Getter for all active projects.
	 * @return read-only view of the projects
	 */
	public List<Project> getActiveProjects() {
		return repository.getActiveProjects().stream()
				.map(Project::new)
				.collect(Collectors.toList());
	}

	/**
	 * Getter for all projects (both active and inactive).
	 * @return read-only view of the projects
	 */
	public List<Project> getAllProjects() {
		return repository.getAllProjects().stream()
				.map(Project::new)
				.collect(Collectors.toList());
	}

	/**
	 * Provides all activities.
	 * @return read-only view of the activities
	 */
	public List<ProjectActivity> getActivities() {
		return getActivities(null);
	}

	/**
	 * Adds a new activity.
	 * @param activity the activity to add
	 */
	public void addActivity(final ProjectActivity activity) {
		if (activity == null) {
			return;
		}

		repository.addActivity(activity.toVO());
	}

	/**
	 * Removes an activity.
	 * @param activity the activity to remove
	 */
	public void removeActivity(final ProjectActivity activity) {
		if (activity == null) {
			return;
		}

		repository.removeActivity(activity.toVO());
	}
	
	/**
	 * Adds a bunch of projects.
	 * @param projects the projects to add
	 */
	public void addProjects(final Collection<Project> projects) {
		if (projects == null || projects.size() == 0) {
			return;
		}
		
		for (Project project : projects) {
			addProject(project);
		}
	}

	/**
	 * Adds a bunch of activities.
	 * @param activities the activities to add
	 */
	public void addActivities(final Collection<ProjectActivity> activities) {
		if (activities == null || activities.size() == 0) {
			return;
		}
		
		for (ProjectActivity activity : activities) {
			addActivity(activity);
		}
	}

	/**
	 * Removes a bunch of activities.
	 * @param activities the activities to remove
	 */
	public void removeActivities(final Collection<ProjectActivity> activities) {
		if (activities == null || activities.size() == 0) {
			return;
		}

		for (ProjectActivity activity : activities) {
			removeActivity(activity);
		}
	}

	/**
	 * Updates the project in the database. Pending changes will be made persistent.
	 * @param project the project to update
	 */
	public void updateProject(final Project project) {
		if (project == null) {
			return;
		}

		repository.updateProject(project.toVO());
	}

	/**
	 * Updates the activity in the database. Pending changes will be made persistent.
	 * @param activity the activity to update
	 */
	public void updateActivity(final ProjectActivity activity) {
		if (activity == null) {
			return;
		}

		repository.updateActivity(activity.toVO());
	}

	/**
	 * Find a project by it's id.
	 * @param projectId the id of the project
	 * @return the project with the given id or <code>null</code> if there is none
	 */
	public Project findProjectById(final String projectId) {
		if (projectId == null) {
			return null;
		}


		ProjectVO projectVO = repository.findProjectById(projectId);
		if (projectVO == null) {
			return null;
		}

		return new Project(projectVO);
	}
	
	/**
	 * Provides a list of all months with activities.
	 */
	public List<Integer> getMonthList() {
		return repository.getMonthList();
	}

	/**
	 * Provides all activities satisfying the given filter.
	 * @param filter the filter for activities
	 * @return read-only view of the activities
	 */
	public List<ProjectActivity> getActivities(final Filter filter) {
		FilterVO filterVO = filter != null ? filter.toVO() : null;
		return repository.getActivities(filterVO).stream()
				.map(ProjectActivity::new)
				.collect(Collectors.toList());
	}
	
	/**
	 * Gathers some statistics about the tracked activities.
	 */
	public void gatherStatistics() {
		repository.gatherStatistics();
	}

	/**
	 * Removes all projects and activities from the database.
	 */
	public void clearData() {
		repository.clearData();
	}

}
