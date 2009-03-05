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
public final class ApplicationSettings {

    /** The logger. */
    private static final Log log = LogFactory.getLog(ApplicationSettings.class);

    /** The singleton instance. */
    private static ApplicationSettings instance;

    /** Key for the name of the application properties file. */
    private static String APPLICATION_PROPERTIES_FILENAME = "application.properties"; //$NON-NLS-1$

    /** Node for Baralga application preferences. */
    private PropertiesConfiguration applicationConfig;

    //------------------------------------------------
    // Data locations
    //------------------------------------------------

    /** Default data directory. */
    public static final File dataDirectoryDefault = new File(System.getProperty("user.home") + File.separator + ".ProTrack"); //$NON-NLS-1$ //$NON-NLS-2$

    /** Data directory relative to application installation. */
    public File dataDirectoryApplicationRelative = null;

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
            dataDirectoryApplicationRelative = new File(
                    new File(UserSettings.class.getResource("/").toURI()).getParentFile(), //$NON-NLS-1$
                    "data" //$NON-NLS-1$
            );

            final File dataDir = new File(
                    new File(UserSettings.class.getResource("/").toURI()).getParentFile(), //$NON-NLS-1$
                    "data" //$NON-NLS-1$
            );
            
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }

            final File file = new File(
                    dataDir,
                    APPLICATION_PROPERTIES_FILENAME
            );

            applicationConfig = new PropertiesConfiguration(file);
            applicationConfig.setAutoSave(true);
        } catch (ConfigurationException e) {
            log.error(e, e);
        } catch (URISyntaxException e) {
            log.error(e, e);
        }
    }

    /** Key for storage mode. */
    private static final String STORE_DATA_IN_APPLICATION_DIRECTORY = "storeDataInApplicationDirectory"; //$NON-NLS-1$

    /**
     * Getter for storage mode. This can either be the default directory 
     * (user specific) or a directory relative to the application installation.
     * @return <code>true</code> if data is stored in application installation 
     * directory or <code>false</code> if data is stored in default directory
     */
    public boolean isStoreDataInApplicationDirectory() {
        try {
            return applicationConfig.getBoolean(STORE_DATA_IN_APPLICATION_DIRECTORY);
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Sets the storage mode of the application.
     * @param storeDataInApplicationDirectory the new storage mode
     */
    public void setStoreDataInApplicationDirectory(final boolean storeDataInApplicationDirectory) {
        applicationConfig.setProperty(STORE_DATA_IN_APPLICATION_DIRECTORY, storeDataInApplicationDirectory);
    }

    /**
     * Get the directory of the application in the profile of the user.
     * @return the directory for user settings
     */
    public File getApplicationDataDirectory()  {
        if (isStoreDataInApplicationDirectory()) {
            return dataDirectoryApplicationRelative;
        } else {
            return dataDirectoryDefault;
        }
    }
}
