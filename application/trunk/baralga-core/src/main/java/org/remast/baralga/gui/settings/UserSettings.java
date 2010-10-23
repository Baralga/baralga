package org.remast.baralga.gui.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.remast.baralga.gui.lists.DayFilterList;
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
        return doGetString(LAST_EXCEL_EXPORT_LOCATION, System.getProperty("user.home"));
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
        return doGetString(LAST_DATA_EXPORT_LOCATION, System.getProperty("user.home"));
    }

    /**
     * Sets the location of the last Data export.
     * @param dataExportLocation the location of the last data export to set
     */
    public void setLastDataExportLocation(final String dataExportLocation) {
        userConfig.setProperty(LAST_DATA_EXPORT_LOCATION, dataExportLocation);
    }

    //------------------------------------------------
    // Csv Export Location
    //------------------------------------------------

    /** Key for the location of last Csv export. */
    private static final String LAST_CSV_EXPORT_LOCATION = "export.csv"; //$NON-NLS-1$

    /**
     * Gets the location of the last Csv export.
     * @return the location of the last Csv export
     */
    public String getLastCsvExportLocation() {
        return doGetString(LAST_CSV_EXPORT_LOCATION, System.getProperty("user.home"));
    }

    /**
     * Sets the location of the last Csv export.
     * @param csvExportLocation the location of the last Csv export to set
     */
    public void setLastCsvExportLocation(final String csvExportLocation) {
        userConfig.setProperty(LAST_CSV_EXPORT_LOCATION, csvExportLocation);
    }

    //------------------------------------------------
    // Description
    //------------------------------------------------

    /** Last description. */
    private static final String LAST_DESCRIPTION = "description"; //$NON-NLS-1$

    public String getLastDescription() {
        return doGetString(LAST_DESCRIPTION, StringUtils.EMPTY);
    }

    public void setLastDescription(final String lastDescription) {
        userConfig.setProperty(LAST_DESCRIPTION, lastDescription);
    }
    
    //------------------------------------------------
    // Active Flag
    //------------------------------------------------
    
    /** Active Flag. */
    private static final String ACTIVE = "active"; //$NON-NLS-1$
    
    public boolean isActive() {
    	return doGetBoolean(ACTIVE, false);
    }
    
    public void setActive(final boolean active) {
    	userConfig.setProperty(ACTIVE, active);
    }
    
    //------------------------------------------------
    // Id of active (selected) project
    //------------------------------------------------
    
    /** Active Flag. */
    private static final String ACTIVE_PROJECT_ID = "activeProjectId"; //$NON-NLS-1$
    
    public Long getActiveProjectId() {
    	return doGetLong(ACTIVE_PROJECT_ID, null);
    }
    
    public void setActiveProjectId(final Long activeProjectId) {
    	userConfig.setProperty(ACTIVE_PROJECT_ID, activeProjectId);
    }
    
    //------------------------------------------------
    // Start time
    //------------------------------------------------
    
    /** Active Flag. */
    private static final String START = "start"; //$NON-NLS-1$
    
    public DateTime getStart() {
    	return doGetDate(START, null);
    }
    
    public void setStart(final DateTime start) {
    	if (start == null) {
    		userConfig.setProperty(START, null);
    	} else {
    		userConfig.setProperty(START, start.getMillis());
    	}
    }
    
    //------------------------------------------------
    // Filter Settings
    //------------------------------------------------

    /** The key for the selected month of filter. */
    private static final String SELECTED_MONTH = "filter.month"; //$NON-NLS-1$

    public Integer getFilterSelectedMonth() {
        // Avoid ConversionException by checking the type of the property
        final Object selectedMonthObject = userConfig.getProperty(SELECTED_MONTH);
        
        // -- 
        // :INFO: Migrate from < 1.3 where * was used as dummy value
        if ((selectedMonthObject instanceof String) && StringUtils.equals("*", (String) selectedMonthObject)) {
            setFilterSelectedMonth(MonthFilterList.ALL_MONTHS_DUMMY);
        }
        // --
        return doGetInteger(SELECTED_MONTH, null);
    }
    
    public void setFilterSelectedMonth(final Integer month) {
        userConfig.setProperty(SELECTED_MONTH, month);
    }
    
    /** The key for the selected day of filter. */
    private static final String SELECTED_DAY = "filter.day"; //$NON-NLS-1$

    public Integer getFilterSelectedDay() {
        return doGetInteger(SELECTED_DAY, null);
    }
    
    public void setFilterSelectedDay(final Integer day) {
        userConfig.setProperty(SELECTED_DAY, day);
    }

    /** The key for the selected week of filter. */
    private static final String SELECTED_WEEK_OF_YEAR = "filter.weekOfYear"; //$NON-NLS-1$

    public Integer getFilterSelectedWeekOfYear() {
        return doGetInteger(SELECTED_WEEK_OF_YEAR, null);
    }

    public void setFilterSelectedWeekOfYear(final Integer weekOfYear) {
        userConfig.setProperty(SELECTED_WEEK_OF_YEAR, weekOfYear);
    }

    /** The key for the selected year of filter. */
    private static final String SELECTED_YEAR = "filter.year"; //$NON-NLS-1$

    public Integer getFilterSelectedYear() {
        // Avoid ConversionException by checking the type of the property
        final Object selectedYearObject = userConfig.getProperty(SELECTED_YEAR);
        
        // -- 
        // :INFO: Migrate from < 1.3 where * was used as dummy value
        if ((selectedYearObject instanceof String) && StringUtils.equals("*", (String) selectedYearObject)) {
            setFilterSelectedYear(YearFilterList.ALL_YEARS_DUMMY);
        }
        // -- 
        return doGetInteger(SELECTED_YEAR, null);
    }

    public void setFilterSelectedYear(final Integer year) {
        userConfig.setProperty(SELECTED_YEAR, year);
    }

    /** The key for the selected project id of filter. */
    private static final String SELECTED_PROJECT_ID = "filter.projectId"; //$NON-NLS-1$

    public Long getFilterSelectedProjectId() {
        return doGetLong(SELECTED_PROJECT_ID, null);
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
        return doGetString(SHOWN_CATEGORY, "General"); //$NON-NLS-1$
    }

    public void setShownCategory(final String shownCategory) {
        userConfig.setProperty(SHOWN_CATEGORY, shownCategory);
    }
    
    //------------------------------------------------
    // Remember window size and location
    //------------------------------------------------

    /** The key for remembering window size and location. */
    public static final String REMEMBER_WINDOWSIZE_LOCATION = "settings.rememberWindowSizeLocation"; //$NON-NLS-1$

    public Boolean isRememberWindowSizeLocation() {
        return doGetBoolean(REMEMBER_WINDOWSIZE_LOCATION, false);
    }

    public void setRememberWindowSizeLocation(final boolean rememberWindowSizeLocation) {
        userConfig.setProperty(REMEMBER_WINDOWSIZE_LOCATION, rememberWindowSizeLocation);
    }
    
    //------------------------------------------------
    // Window size
    //------------------------------------------------

    /** The key for the window size. */
    public static final String WINDOW_SIZE = "settings.windowSize"; //$NON-NLS-1$

    public Dimension getWindowSize() {
        final String encodedSize = doGetString(WINDOW_SIZE, "530.0|720.0"); //$NON-NLS-1$
        final String[] sizeValues = StringUtils.split(encodedSize, '|');
        
        final Dimension size = new Dimension(Double.valueOf(sizeValues[0]).intValue(), Double.valueOf(sizeValues[1]).intValue());
        return size;
    }

    public void setWindowSize(final Dimension size) {
        final String encodedSize = size.getWidth() + "|" + size.getHeight(); //$NON-NLS-1$
        userConfig.setProperty(WINDOW_SIZE, encodedSize);
    }
    
    //------------------------------------------------
    // Window location
    //------------------------------------------------

    /** The key for the shown category. */
    public static final String WINDOW_LOCATION = "settings.windowLocation"; //$NON-NLS-1$

    public Point getWindowLocation() {
        final String encodedLocation = doGetString(WINDOW_LOCATION, "0.0|0.0"); //$NON-NLS-1$
        final String[] locationCoordinates = StringUtils.split(encodedLocation, '|');
        
        final Point location = new Point(Double.valueOf(locationCoordinates[0]).intValue(), Double.valueOf(locationCoordinates[1]).intValue());
        return location;
    }

    public void setWindowLocation(final Point location) {
        final String encodedLocation = location.getX() + "|" + location.getY(); //$NON-NLS-1$
        userConfig.setProperty(WINDOW_LOCATION, encodedLocation);
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

        // Restore the day
        restoreDayFilter(filter);

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
//            filter.setYear(DateUtils.getNowAsDateTime());
            return;
        } 

        if (selectedYear != YearFilterList.ALL_YEARS_DUMMY) {
            try {
                DateTime year = new DateTime().withYear(selectedYear);
//                filter.setYear(year);
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
//            filter.setMonth(DateUtils.getNowAsDateTime());
            return;
        } 

        if (selectedMonth != MonthFilterList.ALL_MONTHS_DUMMY) {
            try {
                DateTime month = new DateTime().withMonthOfYear(selectedMonth);
//                filter.setMonth(month);
            } catch (NumberFormatException e) {
                log.error(e, e);
            }
        }
    }
    
    /**
     * Restores the filter for the day.
     * @param filter the restored filter
     */
    private void restoreDayFilter(final Filter filter) {
        final Integer selectedDay = getFilterSelectedDay();

        if (selectedDay == null) {
            return;
        }

        if (selectedDay == DayFilterList.CURRENT_DAY_DUMMY) {
//            filter.setDay(DateUtils.getNowAsDateTime());
            return;
        } 

        if (selectedDay != DayFilterList.ALL_DAYS_DUMMY) {
            try {
                DateTime day = new DateTime().withDayOfWeek(selectedDay);
//                filter.setDay(day);
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
//            filter.setWeekOfYear(DateUtils.getNowAsDateTime());
            return;
        } 

        if (selectedWeekOfYear != WeekOfYearFilterList.ALL_WEEKS_OF_YEAR_DUMMY) {
            try {
                final DateTime weekOfYear = new DateTime().withWeekOfWeekyear(selectedWeekOfYear);
//                filter.setWeekOfYear(weekOfYear);
            } catch (NumberFormatException e) {
            	log.error(e, e);
            } catch (IllegalFieldValueException e2) {
            	log.error(e2, e2);
            }
        }
    }
    
    /**
     * Resets all settings to their default values.
     */
    public void reset() {
        userConfig.clear();
        try {
            userConfig.save();
        } catch (ConfigurationException e) {
            log.error(e, e);
        }
    }

    //------------------------------------------------
    // Helper methods
    //------------------------------------------------

    /**
     * Getter that handle errors gracefully meaning errors are logged 
     * but applications continues with the default value.
     * @param key the key of the property to get
     * @param defaultValue the default value of the property to get
     * @return the property value if set and correct otherwise the default value
     */
    private String doGetString(final String key, final String defaultValue) {
        try {
            return userConfig.getString(key, defaultValue);
        } catch (Exception e) {
            log.error(e, e);
            return defaultValue;
        } catch (Throwable t) {
            log.error(t, t);
            return defaultValue;
        }
    }

    /**
     * Getter that handle errors gracefully meaning errors are logged 
     * but applications continues with the default value.
     * @param key the key of the property to get
     * @param defaultValue the default value of the property to get
     * @return the property value if set and correct otherwise the default value
     */
    private Long doGetLong(final String key, final Long defaultValue) {
        try {
            return userConfig.getLong(key, defaultValue);
        } catch (Exception e) {
            log.error(e, e);
            return defaultValue;
        } catch (Throwable t) {
            log.error(t, t);
            return defaultValue;
        }
    }

    /**
     * Getter that handle errors gracefully meaning errors are logged 
     * but applications continues with the default value.
     * @param key the key of the property to get
     * @param defaultValue the default value of the property to get
     * @return the property value if set and correct otherwise the default value
     */
    private Integer doGetInteger(final String key, final Integer defaultValue) {
        try {
            return userConfig.getInteger(key, defaultValue);
        } catch (Exception e) {
            log.error(e, e);
            return defaultValue;
        } catch (Throwable t) {
            log.error(t, t);
            return defaultValue;
        }
    }
    
    /**
     * Getter that handle errors gracefully meaning errors are logged 
     * but applications continues with the default value.
     * @param key the key of the property to get
     * @param defaultValue the default value of the property to get
     * @return the property value if set and correct otherwise the default value
     */
    private Boolean doGetBoolean(final String key, final Boolean defaultValue) {
        try {
            return userConfig.getBoolean(key, defaultValue);
        } catch (Exception e) {
            log.error(e, e);
            return defaultValue;
        } catch (Throwable t) {
            log.error(t, t);
            return defaultValue;
        }
    }
    
    /**
     * Getter that handle errors gracefully meaning errors are logged 
     * but applications continues with the default value.
     * @param key the key of the property to get
     * @param defaultValue the default value of the property to get
     * @return the property value if set and correct otherwise the default value
     */
    private DateTime doGetDate(final String key, final DateTime defaultValue) {
    	try {
    		Long defaultMillis = null;
    		if (defaultValue != null) {
    			defaultMillis = defaultValue.getMillis();	
    		}
    		
    		final Long dateMilliseconds = userConfig.getLong(key, defaultMillis);
    		if (dateMilliseconds == null) {
    			return null;
    		}
    		
    		return new DateTime(dateMilliseconds);
    	} catch (Exception e) {
    		log.error(e, e);
    		return defaultValue;
    	} catch (Throwable t) {
    		log.error(t, t);
    		return defaultValue;
    	}
    }
}
