package org.remast.baralga.gui;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.lists.MonthFilterList;
import org.remast.baralga.gui.lists.YearFilterList;
import org.remast.baralga.model.filter.Filter;

/**
 * Stores and reads the user settings.
 * @author remast
 */
public class Settings {
    
    /** The logger. */
    private static final Log log = LogFactory.getLog(Settings.class);

    //------------------------------------------------
    // Data locations
    //------------------------------------------------

    /** Default directory of ProTrack. */
    public static final File DEFAULT_DIRECTORY = new File( System.getProperty("user.home") + File.separator + ".ProTrack" ); //$NON-NLS-1$ //$NON-NLS-2$

    /** Default name of the ProTrack data file. */
    public static final String DEFAULT_FILE_NAME = "ProTrack.ptd"; //$NON-NLS-1$

    /**
     * Get the location of the data file.
     * @return the path of the data file
     */
    public static String getProTrackFileLocation() {
        return DEFAULT_DIRECTORY.getPath() + File.separator + DEFAULT_FILE_NAME;
    }

    /**
     * Get the directory of Baralga in the profile of the user.
     * @return the directory for user settings
     */
    public static File getBaralgaDirectory()  {
        return DEFAULT_DIRECTORY;
    }

   
    private static String PROPERTIES_FILENAME = DEFAULT_DIRECTORY + File.separator + "baralga.properties";
    
    /** Node for Baralga user preferences. */
    private PropertiesConfiguration config;
    
    /** The singleton instance. */
    private static Settings instance;
    
    /**
     * Getter for singleton instance.
     * @return the settings singleton
     */
    public static Settings instance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    
    private Settings() {
        final File file = new File(PROPERTIES_FILENAME);
        try {
            config = new PropertiesConfiguration(file);
            config.setAutoSave(true);
        } catch (ConfigurationException e) {
            log.error(e, e);
        }
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

    public void setLastExcelExportLocation(final String excelExportLocation) {
        config.setProperty(LAST_EXCEL_EXPORT_LOCATION, excelExportLocation);
    }

    //------------------------------------------------
    // Description
    //------------------------------------------------

    /** Last description. */
    public static final String LAST_DESCRIPTION = "description"; //$NON-NLS-1$

    public String getLastDescription() {
        return config.getString(LAST_DESCRIPTION, StringUtils.EMPTY);
    }

    public void setLastDescription(final String lastDescription) {
        config.setProperty(LAST_DESCRIPTION, lastDescription);
    }
    
    //------------------------------------------------
    // Filter Settings
    //------------------------------------------------
    
    /** The key for the selected month of filter. */
    public static final String SELECTED_MONTH = "filter.month"; //$NON-NLS-1$

    public String getFilterSelectedMonth() {
        return config.getString(SELECTED_MONTH, null);
    }

    public void setFilterSelectedMonth(String month) {
        config.setProperty(SELECTED_MONTH, month);
    }
    
    /** The key for the selected year of filter. */
    public static final String SELECTED_YEAR = "filter.year"; //$NON-NLS-1$

    public String getFilterSelectedYear() {
        return config.getString(SELECTED_YEAR, null);
    }

    public void setFilterSelectedYear(String year) {
        config.setProperty(SELECTED_YEAR, year);
    }

    /** The key for the selected project id of filter. */
    public static final String SELECTED_PROJECT_ID = "filter.projectId"; //$NON-NLS-1$

    public Long getFilterSelectedProjectId() {
        return config.getLong(SELECTED_PROJECT_ID, null);
    }

    public void setFilterSelectedProjectId(long projectId) {
        config.setProperty(SELECTED_PROJECT_ID, new Long(projectId));
    }

    //------------------------------------------------
    // Shown category
    //------------------------------------------------
    
    /** The key for the shown category. */
    public static final String SHOWN_CATEGORY = "shown.category"; //$NON-NLS-1$

    public String getShownCategory() {
        return config.getString(SHOWN_CATEGORY, "General");
    }

    public void setShownCategory(String shownCategory) {
        config.setProperty(SHOWN_CATEGORY, shownCategory);
    }

    /**
     * Restore the current filter from the user settings.
     * @return the restored filter
     */
    public Filter restoreFromSettings() {
        final Filter filter = new Filter();
        
        // 1. Restore the month
        final String selectedMonth = getFilterSelectedMonth();
        if (StringUtils.isNotBlank(selectedMonth) && !MonthFilterList.ALL_MONTHS_DUMMY.equals(selectedMonth)) {
            try {
                final Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(Calendar.MONTH, Integer.parseInt(selectedMonth) - 1);
                filter.setMonth(calendar.getTime());
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
        
        // 1. Restore the year
        final String selectedYear = Settings.instance().getFilterSelectedYear();
        if (StringUtils.isNotBlank(selectedYear) && !YearFilterList.ALL_YEARS_DUMMY.equals(selectedYear)) {
            try {
                final Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(selectedYear));
                filter.setYear(calendar.getTime());
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
        
        return filter;
    }
}
