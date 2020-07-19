package org.remast.baralga.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BaralgaDAOTest {

    private BaralgaDAO baralgaDAO = new BaralgaDAO();

    @BeforeEach
    void beforeEach() throws SQLException {
        baralgaDAO.init("mem");
    }

    @Test
    void updateDatabase() throws SQLException {
        baralgaDAO.updateDatabase();
    }

    @Test
    void addProject() {
        // Arrange
        String projectId = UUID.randomUUID().toString();
        Project project = new Project(projectId, "My Title", "My Description");

        // Act
        baralgaDAO.addProject(project);

        // Assert
        assertNotNull(baralgaDAO.findProjectById(projectId));
    }

    @Test
    void addProjectWithoutId() {
        // Arrange
        String projectId = null;
        Project project = new Project(projectId, "My Title", "My Description");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                baralgaDAO.addProject(project)
        );
    }
}
