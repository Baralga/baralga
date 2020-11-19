package org.remast.baralga.repository.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.remast.baralga.model.Project;
import org.remast.baralga.repository.ProjectVO;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BaralgaFileRepositoryTest {

    private BaralgaFileRepository fileRepository = new BaralgaFileRepository();

    @BeforeEach
    void beforeEach() throws SQLException {
        fileRepository.init("mem");
    }

    @Test
    void updateDatabase() throws SQLException {
        fileRepository.updateDatabase();
    }

    @Test
    void addProject() {
        // Arrange
        String projectId = UUID.randomUUID().toString();
        ProjectVO project = new ProjectVO(projectId, "My Title", "My Description");

        // Act
        fileRepository.addProject(project);

        // Assert
        assertNotNull(fileRepository.findProjectById(projectId));
    }

    @Test
    void addProjectWithoutId() {
        // Arrange
        String projectId = null;
        ProjectVO project = new ProjectVO(projectId, "My Title", "My Description");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                fileRepository.addProject(project)
        );
    }
}
