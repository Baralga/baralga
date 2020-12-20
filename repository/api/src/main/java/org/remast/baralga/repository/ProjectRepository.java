package org.remast.baralga.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface ProjectRepository {

    /**
     * Adds a new project.
     * @param project the project to add
     */
    ProjectVO addProject(final ProjectVO project);

    void remove(final ProjectVO project);

    /**
     * Getter for all active projects.
     * @return read-only view of the ProjectVOs
     */
    List<ProjectVO> getActiveProjects();

    /**
     * Getter for all projects (both active and inactive).
     * @return read-only view of the ProjectVOs
     */
    List<ProjectVO> getAllProjects();

     /**
     * Adds a bunch of projects.
     * @param projects the ProjectVOs to add
     */
     void addProjects(final Collection<ProjectVO> projects);
    
    /**
     * Updates the project in the database. Pending changes will be made persistent.
     * @param project the ProjectVO to update
     */
    void updateProject(final ProjectVO project) ;


    /**
     * Find a project by it's id.
     * @param projectId the id of the ProjectVO
     * @return the ProjectVO with the given id or <code>null</code> if there is none
     */
    Optional<ProjectVO> findProjectById(final String projectId);

    /**
     * Check if project adminstration is allowed.
     */
    boolean isProjectAdministrationAllowed();

}
