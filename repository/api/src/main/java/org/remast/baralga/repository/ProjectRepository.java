package org.remast.baralga.repository;

import java.util.Collection;
import java.util.List;

interface ProjectRepository {

    /**
     * Adds a new ProjectVO.
     * @param ProjectVO the ProjectVO to add
     */
    void addProject(final ProjectVO ProjectVO);

    void remove(final ProjectVO ProjectVO);

    /**
     * Getter for all active ProjectVOs.
     * @return read-only view of the ProjectVOs
     */
    List<ProjectVO> getActiveProjects();

    /**
     * Getter for all ProjectVOs (both active and inactive).
     * @return read-only view of the ProjectVOs
     */
    List<ProjectVO> getAllProjects();

     /**
     * Adds a bunch of ProjectVOs.
     * @param ProjectVOs the ProjectVOs to add
     */
     void addProjects(final Collection<ProjectVO> ProjectVOs);
    
    /**
     * Updates the ProjectVO in the database. Pending changes will be made persistent.
     * @param ProjectVO the ProjectVO to update
     */
    void updateProject(final ProjectVO ProjectVO) ;


    /**
     * Find a ProjectVO by it's id.
     * @param ProjectVOId the id of the ProjectVO
     * @return the ProjectVO with the given id or <code>null</code> if there is none
     */
    ProjectVO findProjectById(final String ProjectVOId) ;

}
