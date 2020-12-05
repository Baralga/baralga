package org.remast.baralga.model;

import org.junit.jupiter.api.Test;
import org.remast.baralga.repository.ProjectVO;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void toVO() {
        // Arrange
        String id = "UUID-1";
        String title = "My Title";
        String description = "My Description";
        Project project = new Project(id, title, description);

        // Act
        ProjectVO projectVO = project.toVO();

        // Assert
        assertEquals(project.getId(), projectVO.getId());
        assertEquals(project.getTitle(), projectVO.getTitle());
        assertEquals(project.getDescription(), projectVO.getDescription());
    }

    @Test
    void fromVO() {
        // Arrange
        String id = "UUID-1";
        String title = "My Title";
        String description = "My Description";
        ProjectVO projectVO = new ProjectVO(id, title, description);

        // Act
        Project project = new Project(projectVO);

        // Assert
        assertEquals(projectVO.getId(), project.getId());
        assertEquals(projectVO.getTitle(), project.getTitle());
        assertEquals(projectVO.getDescription(), project.getDescription());
    }
}
