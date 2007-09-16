package org.remast.baralga.gui;

import java.io.File;
import java.util.prefs.Preferences;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.remast.baralga.BaralgaMain;

public class Settings {
    //------------------------------------------------
    // ProTrack data locations
    //------------------------------------------------

    /** Default directory of ProTrack. */
    public static final String DEFAULT_DIRECTORY = System.getProperty("user.home") + File.separator + ".ProTrack"; //$NON-NLS-1$ //$NON-NLS-2$

    /** Default name of the ProTrack data file. */
    public static final String DEFAULT_FILE_NAME = "ProTrack.ptd"; //$NON-NLS-1$

    public static String getProTrackFileLocation() {
        return DEFAULT_DIRECTORY + File.separator + DEFAULT_FILE_NAME;
    }

    public static File getProTrackDirectory()  {
        return new File(DEFAULT_DIRECTORY);
    }

   
    private static String PROPERTIES_FILENAME = DEFAULT_DIRECTORY + File.separator + "baralga.properties";
    
    /** Node for Baralga user preferences. */
    private PropertiesConfiguration config;
    
    private static Settings instance;
    
    public static Settings instance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    
    private Settings() {
            File file = new File(PROPERTIES_FILENAME);
            try {
                config = new PropertiesConfiguration(file);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }
            config.setAutoSave(true);
    }
    


    
    //------------------------------------------------
    // Lock file Location
    //------------------------------------------------

    /** Name of the lock file. */
    public static final String LOCK_FILE_NAME = "lock"; //$NON-NLS-1$

    public static String getLockFileLocation() {
        return DEFAULT_DIRECTORY + File.separator + LOCK_FILE_NAME;
    }

    //------------------------------------------------
    // Excel Export Location
    //------------------------------------------------

    /** Location of last Excel export. */
    public static final String LAST_EXCEL_EXPORT_LOCATION = "export.excel"; //$NON-NLS-1$

    public String getLastExcelExportLocation() {
        return config.getString(LAST_EXCEL_EXPORT_LOCATION, System.getProperty("user.home"));
    }

    public void setLastExcelExportLocation(String excelExportLocation) {
        config.setProperty(LAST_EXCEL_EXPORT_LOCATION, excelExportLocation);
    }

    
    //------------------------------------------------
    // Filter Settings
    //------------------------------------------------
    
    /** Selected month of filter. */
    public static final String SELECTED_MONTH = "filter.month"; //$NON-NLS-1$

    public String getSelectedMonth() {
        return config.getString(SELECTED_MONTH, null);
    }

    public void setSelectedMonth(String month) {
        config.setProperty(SELECTED_MONTH, month);
    }
    
    /** Selected year of filter. */
    public static final String SELECTED_YEAR = "filter.year"; //$NON-NLS-1$

    public String getSelectedYear() {
        return config.getString(SELECTED_YEAR, null);
    }

    public void setSelectedYear(String year) {
        config.setProperty(SELECTED_YEAR, year);
    }

    /** Selected project id of filter. */
    public static final String SELECTED_PROJECT_ID = "filter.projectId"; //$NON-NLS-1$

    public Long getSelectedProjectId() {
        return config.getLong(SELECTED_PROJECT_ID, null);
    }

    public void setSelectedProjectId(long projectId) {
        config.setProperty(SELECTED_PROJECT_ID, new Long(projectId));
    }

}
