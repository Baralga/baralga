package org.remast.baralga.gui.settings;

import java.io.File;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Stores and reads all settings specific to the whole application.
 * @author remast
 */
public class ApplicationSettings {

    /** The logger. */
    private static final Log log = LogFactory.getLog(ApplicationSettings.class);

    /** The singleton instance. */
    private static ApplicationSettings instance;

    /** Key for the name of the application properties file. */
    private static String APPLICATION_PROPERTIES_FILENAME = "application.properties";

    /** Node for Baralga application preferences. */
    private PropertiesConfiguration applicationConfig;

    //------------------------------------------------
    // Data locations
    //------------------------------------------------

    /** Default directory of ProTrack. */
    public static final File DATA_DIRECTORY_DEFAULT = new File(System.getProperty("user.home") + File.separator + ".ProTrack"); //$NON-NLS-1$ //$NON-NLS-2$

    public File DATA_DIRECTORY_APPLICATION_RELATIVE = null;

    /**
     * Getter for singleton instance.
     * @return the settings singleton
     */
    public static ApplicationSettings instance() {
        if (instance == null) {
            instance = new ApplicationSettings();
        }
        return instance;
    }

    private ApplicationSettings() {
        try {
            DATA_DIRECTORY_APPLICATION_RELATIVE = new File(new File(UserSettings.class.getResource("/").toURI()).getParentFile(), "data");

            final File dataDir = new File(new File(UserSettings.class.getResource("/").toURI()).getParentFile(), "data");
            if (!dataDir.exists())
                dataDir.mkdir();

            final File file = new File(
                    dataDir,
                    APPLICATION_PROPERTIES_FILENAME);
            applicationConfig = new PropertiesConfiguration(file);
            applicationConfig.setAutoSave(true);
        } catch (ConfigurationException e) {
            log.error(e, e);
        } catch (URISyntaxException e) {
            log.error(e, e);
        }
    }

    private static final String STORE_DATA_IN_APPLICATION_DIRECTORY = "storeDataInApplicationDirectory";

    public boolean isStoreDataInApplicationDirectory() {
        try {
            return applicationConfig.getBoolean(STORE_DATA_IN_APPLICATION_DIRECTORY);
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void setStoreDataInApplicationDirectory(boolean storeDataInApplicationDirectory) {
        applicationConfig.setProperty(STORE_DATA_IN_APPLICATION_DIRECTORY, storeDataInApplicationDirectory);
    }
    /**
     * Get the directory of the application in the profile of the user.
     * @return the directory for user settings
     */
    public File getApplicationDataDirectory()  {
        if (isStoreDataInApplicationDirectory()) {
            return DATA_DIRECTORY_APPLICATION_RELATIVE;
        } else {
            return DATA_DIRECTORY_DEFAULT;
        }
    }
}
