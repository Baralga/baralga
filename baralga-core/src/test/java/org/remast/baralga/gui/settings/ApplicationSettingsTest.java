package org.remast.baralga.gui.settings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationSettingsTest {

    @BeforeEach
    void before() {
        System.clearProperty("userMode");
        ApplicationSettings applicationSettings = ApplicationSettings.instance();
        applicationSettings.setApplicationConfig(new Properties());
    }

    @Test()
    void defaultValues() {
        // Arrange
        ApplicationSettings applicationSettings = ApplicationSettings.instance();
        applicationSettings.setApplicationConfig(new Properties());

        // Act + Assert
        assertFalse(ApplicationSettings.instance().isMultiUserMode());
        assertEquals("http://localhost:8080", ApplicationSettings.instance().getBackendURL());
        assertEquals("us3r", ApplicationSettings.instance().getPassword());
        assertNotNull(ApplicationSettings.instance().getUser());
    }

    @Test
    void getBackendURLFromProps() {
        // Arrange
        String newBackendURL = "http://xxx.baralga.y:98";
        Properties applicationConfig = new Properties();
        applicationConfig.put("backendURL", newBackendURL);
        ApplicationSettings applicationSettings = ApplicationSettings.instance();
        applicationSettings.setApplicationConfig(applicationConfig);

        // Act
        String backendURL = ApplicationSettings.instance().getBackendURL();

        // Assert
        assertEquals(newBackendURL, backendURL);
    }

    @Test
    void getUserModeFromSystemProperty() {
        // Arrange
        String userMode = "multiuser";
        System.setProperty("userMode", userMode);
        Properties applicationConfig = new Properties();
        ApplicationSettings applicationSettings = ApplicationSettings.instance();
        applicationSettings.setApplicationConfig(applicationConfig);

        // Act
        boolean multiUserMode = ApplicationSettings.instance().isMultiUserMode();

        // Assert
        assertTrue(multiUserMode);
    }

    @Test
    void getUserFromSystemProperty() {
        // Arrange
        String systemUser = "mylogin";
        System.setProperty("user.name", systemUser);
        Properties applicationConfig = new Properties();
        ApplicationSettings applicationSettings = ApplicationSettings.instance();
        applicationSettings.setApplicationConfig(applicationConfig);

        // Act
        String user = ApplicationSettings.instance().getUser();

        // Assert
        assertEquals(user, systemUser);
    }

    @Test
    void getUserFromProps() {
        // Arrange
        String systemUser = "mylogin";
        Properties applicationConfig = new Properties();
        applicationConfig.setProperty("user", systemUser);
        ApplicationSettings applicationSettings = ApplicationSettings.instance();
        applicationSettings.setApplicationConfig(applicationConfig);

        // Act
        String user = ApplicationSettings.instance().getUser();

        // Assert
        assertEquals(user, systemUser);
    }

    @Test
    void getPasswordFromProps() {
        // Arrange
        String configuredPassword = "**sshh**";
        Properties applicationConfig = new Properties();
        applicationConfig.setProperty("password", configuredPassword);
        ApplicationSettings applicationSettings = ApplicationSettings.instance();
        applicationSettings.setApplicationConfig(applicationConfig);

        // Act
        String password = ApplicationSettings.instance().getPassword();

        // Assert
        assertEquals(password, configuredPassword);
    }
}
