package org.remast.baralga.gui.settings;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.lists.MonthFilterList;
import org.remast.baralga.gui.lists.WeekOfYearFilterList;
import org.remast.baralga.gui.lists.YearFilterList;
import org.remast.baralga.model.filter.Filter;
import org.remast.util.DateUtils;

/**
 * Stores and reads all settings specific to one user.
 * @author remast
 */
public final class UserSettings {

    /** The logger. */
    private static final Log log = LogFactory.getLog(UserSettings.class);

    /** Default name of the ProTrack data file. */
    public static final String DEFAULT_FILE_NAME = "ProTrack.ptd"; //$NON-NLS-1$

    /**
     * Get the location of the data file.
     * @return the path of the data file
     */
    public String getDataFileLocation() {
        return ApplicationSettings.instance().getApplicationDataDirectory().getPath() + File.separator + DEFAULT_FILE_NAME;
    }

    /** Key for the name of the user properties file. */
    private static String USER_PROPERTIES_FILENAME = "baralga.properties";

    /** Node for Baralga user preferences. */
    private PropertiesConfiguration userConfig;

    /** The singleton instance. */
    private static UserSettings instance = new UserSettings();

    /**
     * Getter for singleton instance.
     * @return the settings singleton
     */
    public static UserSettings instance() {
        return instance;
    }

    /**
     * Constructor for the settings.
     */
    private UserSettings() {
        final File userConfigFile = new File(ApplicationSettings.instance().getApplicationDataDirectory() + File.separator + USER_PROPERTIES_FILENAME);
        try {
            userConfig = new PropertiesConfiguration(userConfigFile);
            userConfig.setAutoSave(true);
        } catch (ConfigurationException e) {
            log.error(e, e);
        }
    }

    //------------------------------------------------
    // Lock file Location
    //------------------------------------------------

    /** Name of the lock file. */
    private static final String LOCK_FILE_NAME = "lock"; //$NON-NLS-1$

    /** 
     * Gets the location of the lock file.
     * @return the location of the lock file
     */
    public static String getLockFileLocation() {
        return ApplicationSettings.instance().getApplicationDataDirectory() + File.separator + LOCK_FILE_NAME;
    }

    //------------------------------------------------
    // Excel Export Location
    //------------------------------------------------

    /** Key for the location of last Excel export. */
    private static final String LAST_EXCEL_EXPORT_LOCATION = "export.excel"; //$NON-NLS-1$

    /**
     * Gets the location of the last Excel export.
     * @return the location of the last Excel export
     */
    public String getLastExcelExportLocation() {
        return userConfig.getString(LAST_EXCEL_EXPORT_LOCATION, System.getProperty("user.home"));
    }

    /**
     * Sets the location of the last Excel export.
     * @param excelExportLocation the location of the last Excel export to set
     */
    public void setLastExcelExportLocation(final String excelExportLocation) {
        userConfig.setProperty(LAST_EXCEL_EXPORT_LOCATION, excelExportLocation);
    }
    
    //------------------------------------------------
    // Data Export Location
    //------------------------------------------------

    /** Key for the location of last Data export. */
    private static final String LAST_DATA_EXPORT_LOCATION = "export.data"; //$NON-NLS-1$

    /**
     * Gets the location of the last Data export.
     * @return the location of the last Data export
     */
    public String getLastDataExportLocation() {
        return userConfig.getString(LAST_DATA_EXPORT_LOCATION, System.getProperty("user.home"));
    }

    /**
     * Sets the location of the last Data export.
     * @param excelExportLocation the location of the last Data export to set
     */
    public void setLastDataExportLocation(final String excelExportLocation) {
        userConfig.setProperty(LAST_DATA_EXPORT_LOCATION, excelExportLocation);
    }

    //------------------------------------------------
    // Description
    //------------------------------------------------

    /** Last description. */
    private static final String LAST_DESCRIPTION = "description"; //$NON-NLS-1$

    public String getLastDescription() {
        return userConfig.getString(LAST_DESCRIPTION, StringUtils.EMPTY);
    }

    public void setLastDescription(final String lastDescription) {
        userConfig.setProperty(LAST_DESCRIPTION, lastDescription);
    }

    //------------------------------------------------
    // Filter Settings
    //------------------------------------------------

    /** The key for the selected month of filter. */
    private static final String SELECTED_MONTH = "filter.month"; //$NON-NLS-1$

    public Integer getFilterSelectedMonth() {
        // -- 
        // :INFO: Migrate from < 1.3 where * was used as dummy value
        if (StringUtils.equals("*", userConfig.getString(SELECTED_MONTH, null))) {
            setFilterSelectedMonth(MonthFilterList.ALL_MONTHS_DUMMY);
        }
        // --
        return userConfig.getInteger(SELECTED_MONTH, null);
    }

    public void setFilterSelectedMonth(final Integer month) {
        userConfig.setProperty(SELECTED_MONTH, month);
    }

    /** The key for the selected week of filter. */
    private static final String SELECTED_WEEK_OF_YEAR = "filter.weekOfYear"; //$NON-NLS-1$

    public Integer getFilterSelectedWeekOfYear() {
        return userConfig.getInteger(SELECTED_WEEK_OF_YEAR, null);
    }

    public void setFilterSelectedWeekOfYear(final Integer weekOfYear) {
        userConfig.setProperty(SELECTED_WEEK_OF_YEAR, weekOfYear);
    }

    /** The key for the selected year of filter. */
    private static final String SELECTED_YEAR = "filter.year"; //$NON-NLS-1$

    public Integer getFilterSelectedYear() {
        // -- 
        // :INFO: Migrate from < 1.3 where * was used as dummy value
        if (StringUtils.equals("*", userConfig.getString(SELECTED_YEAR, null))) {
            setFilterSelectedYear(YearFilterList.ALL_YEARS_DUMMY);
        }
        // -- 
        return userConfig.getInteger(SELECTED_YEAR, null);
    }

    public void setFilterSelectedYear(final Integer year) {
        userConfig.setProperty(SELECTED_YEAR, year);
    }

    /** The key for the selected project id of filter. */
    private static final String SELECTED_PROJECT_ID = "filter.projectId"; //$NON-NLS-1$

    public Long getFilterSelectedProjectId() {
        return userConfig.getLong(SELECTED_PROJECT_ID, null);
    }

    public void setFilterSelectedProjectId(final long projectId) {
        userConfig.setProperty(SELECTED_PROJECT_ID, Long.valueOf(projectId));
    }

    //------------------------------------------------
    // Shown category
    //------------------------------------------------

    /** The key for the shown category. */
    public static final String SHOWN_CATEGORY = "shown.category"; //$NON-NLS-1$

    public String getShownCategory() {
        return userConfig.getString(SHOWN_CATEGORY, "General");
    }

    public void setShownCategory(final String shownCategory) {
        userConfig.setProperty(SHOWN_CATEGORY, shownCategory);
    }

    /**
     * Restore the current filter from the user settings.
     * @return the restored filter
     */
    public Filter restoreFromSettings() {
        final Filter filter = new Filter();

        // Restore the week of the year
        restoreWeekOfYearFilter(filter);

        // Restore the month
        restoreMonthFilter(filter);

        // Restore the year
        restoreYearFilter(filter);

        return filter;
    }

    /**
     * Restores the filter for the year.
     * @param filter the restored filter
     */
    private void restoreYearFilter(final Filter filter) {
        final Integer selectedYear = UserSettings.instance().getFilterSelectedYear();

        if (selectedYear == null) {
            return;
        }

        if (selectedYear == YearFilterList.CURRENT_YEAR_DUMMY) {
            filter.setYear(DateUtils.getNow());
            return;
        } 

        if (selectedYear != YearFilterList.ALL_YEARS_DUMMY) {
            try {
                final Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(Calendar.YEAR, selectedYear);
                filter.setYear(calendar.getTime());
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
    }

    /**
     * Restores the filter for the month.
     * @param filter the restored filter
     */
    private void restoreMonthFilter(final Filter filter) {
        final Integer selectedMonth = getFilterSelectedMonth();

        if (selectedMonth == null) {
            return;
        }

        if (selectedMonth == MonthFilterList.CURRENT_MONTH_DUMMY) {
            filter.setMonth(DateUtils.getNow());
            return;
        } 

        if (selectedMonth != MonthFilterList.ALL_MONTHS_DUMMY) {
            try {
                final Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(Calendar.MONTH, selectedMonth - 1);
                filter.setMonth(calendar.getTime());
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
    }

    /**
     * Restores the filter for the week of year.
     * @param filter the restored filter
     */
    private void restoreWeekOfYearFilter(final Filter filter) {
        final Integer selectedWeekOfYear = getFilterSelectedWeekOfYear();

        if (selectedWeekOfYear == null) {
            return;
        }

        if (selectedWeekOfYear == WeekOfYearFilterList.CURRENT_WEEK_OF_YEAR_DUMMY) {
            filter.setWeekOfYear(DateUtils.getNow());
            return;
        } 

        if (selectedWeekOfYear != WeekOfYearFilterList.ALL_WEEKS_OF_YEAR_DUMMY) {
            try {
                final Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(Calendar.WEEK_OF_YEAR, selectedWeekOfYear);
                filter.setWeekOfYear(calendar.getTime());
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
    }
}
