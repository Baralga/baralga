package org.remast.baralga.repository.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        ProjectVO project = new ProjectVO(null, "My Title", "My Description", true);

        // Act
        fileRepository.addProject(project);

        // Assert
        assertNotNull(project.getId());
        assertNotNull(fileRepository.findProjectById(project.getId()));
    }

}
