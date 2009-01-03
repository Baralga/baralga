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
import org.remast.baralga.gui.lists.WeekOfYearFilterList;
import org.remast.baralga.gui.lists.YearFilterList;
import org.remast.baralga.model.filter.Filter;
import org.remast.util.DateUtils;

/**
 * Stores and reads the user settings.
 * @author remast
 */
public final class Settings {

    /** The logger. */
    private static final Log log = LogFactory.getLog(Settings.class);

    //------------------------------------------------
    // Data locations
    //------------------------------------------------

    /** Default directory of ProTrack. */
    public static final File DEFAULT_DIRECTORY = new File(System.getProperty("user.home") + File.separator + ".ProTrack"); //$NON-NLS-1$ //$NON-NLS-2$

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

    /** Key for the name of the properties file. */
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

    /**
     * Constructor for the settings.
     */
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

    /** 
     * Gets the location of the lock file.
     * @return the location of the lock file
     */
    public static String getLockFileLocation() {
        return DEFAULT_DIRECTORY + File.separator + LOCK_FILE_NAME;
    }

    //------------------------------------------------
    // Excel Export Location
    //------------------------------------------------

    /** Key for the location of last Excel export. */
    public static final String LAST_EXCEL_EXPORT_LOCATION = "export.excel"; //$NON-NLS-1$

    /**
     * Gets the location of the last Excel export.
     * @return the location of the last Excel export
     */
    public String getLastExcelExportLocation() {
        return config.getString(LAST_EXCEL_EXPORT_LOCATION, System.getProperty("user.home"));
    }

    /**
     * Sets the location of the last Excel export.
     * @param excelExportLocation the location of the last Excel export to set
     */
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
        // -- 
        // :INFO: Migrate from < 1.3 where * was used as dummy value
        if (StringUtils.equals("*", config.getString(SELECTED_MONTH, null))) {
            setFilterSelectedMonth(String.valueOf(MonthFilterList.ALL_MONTHS_DUMMY));
        }
        // --
        return config.getString(SELECTED_MONTH, null);
    }

    public void setFilterSelectedMonth(final String month) {
        config.setProperty(SELECTED_MONTH, month);
    }

    /** The key for the selected week of filter. */
    public static final String SELECTED_WEEK_OF_YEAR = "filter.weekOfYear"; //$NON-NLS-1$

    public String getFilterSelectedWeekOfYear() {
        return config.getString(SELECTED_WEEK_OF_YEAR, null);
    }

    public void setFilterSelectedWeekOfYear(final String weekOfYear) {
        config.setProperty(SELECTED_WEEK_OF_YEAR, weekOfYear);
    }

    /** The key for the selected year of filter. */
    public static final String SELECTED_YEAR = "filter.year"; //$NON-NLS-1$

    public String getFilterSelectedYear() {
        // -- 
        // :INFO: Migrate from < 1.3 where * was used as dummy value
        if (StringUtils.equals("*", config.getString(SELECTED_YEAR, null))) {
            setFilterSelectedYear(String.valueOf(YearFilterList.ALL_YEARS_DUMMY));
        }
        // -- 
        return config.getString(SELECTED_YEAR, null);
    }

    public void setFilterSelectedYear(final String year) {
        config.setProperty(SELECTED_YEAR, year);
    }

    /** The key for the selected project id of filter. */
    public static final String SELECTED_PROJECT_ID = "filter.projectId"; //$NON-NLS-1$

    public Long getFilterSelectedProjectId() {
        return config.getLong(SELECTED_PROJECT_ID, null);
    }

    public void setFilterSelectedProjectId(final long projectId) {
        config.setProperty(SELECTED_PROJECT_ID, Long.valueOf(projectId));
    }

    //------------------------------------------------
    // Shown category
    //------------------------------------------------

    /** The key for the shown category. */
    public static final String SHOWN_CATEGORY = "shown.category"; //$NON-NLS-1$

    public String getShownCategory() {
        return config.getString(SHOWN_CATEGORY, "General");
    }

    public void setShownCategory(final String shownCategory) {
        config.setProperty(SHOWN_CATEGORY, shownCategory);
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
        final String selectedYear = Settings.instance().getFilterSelectedYear();
        if (StringUtils.isNotBlank(selectedYear) && Integer.parseInt(selectedYear) != YearFilterList.ALL_YEARS_DUMMY) {
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

    /**
     * Restores the filter for the month.
     * @param filter the restored filter
     */
    private void restoreMonthFilter(final Filter filter) {
        final String selectedMonth = getFilterSelectedMonth();

        if (StringUtils.isBlank(selectedMonth)) {
            return;
        }
        if (Integer.parseInt(selectedMonth) != MonthFilterList.CURRENT_MONTH_DUMMY) {
            filter.setMonth(DateUtils.getNow());
        } else if (Integer.parseInt(selectedMonth) != MonthFilterList.ALL_MONTHS_DUMMY) {
            try {
                final Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(Calendar.MONTH, Integer.parseInt(selectedMonth) - 1);
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
        final String selectedWeekOfYear = getFilterSelectedWeekOfYear();

        if (StringUtils.isBlank(selectedWeekOfYear)) {
            return;
        }

        if (Integer.parseInt(selectedWeekOfYear) != WeekOfYearFilterList.CURRENT_WEEK_OF_YEAR_DUMMY) {
            filter.setWeekOfYear(DateUtils.getNow());
        } else if (Integer.parseInt(selectedWeekOfYear) != WeekOfYearFilterList.ALL_WEEKS_OF_YEAR_DUMMY) {
            try {
                final Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(selectedWeekOfYear));
                filter.setWeekOfYear(calendar.getTime());
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
    }
}
