package org.remast.baralga.gui.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.filter.SpanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

/**
 * Stores and reads all settings specific to one user.
 * @author remast
 */
public final class UserSettings {

	/** The logger. */
	private static final Logger log = LoggerFactory.getLogger(UserSettings.class);

	/** Default name of the ProTrack data file. */
	public static final String DEFAULT_FILE_NAME = "Data.baralga.xml"; //$NON-NLS-1$

	public static final long DEFAULT_INACTIVITY_THRESHOLD = 1000 * 60 * 5; // 5 minutes
	
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
	private Properties userConfig;

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
		userConfigFile = new File(ApplicationSettings.instance().getApplicationDataDirectory() + File.separator + USER_PROPERTIES_FILENAME);
		try {
			userConfig = new Properties();
			if (userConfigFile.exists()) {
			    userConfig.load(new FileInputStream(userConfigFile));
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
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
		
		// Auto save change
		save();
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
		
		// Auto save change
		save();
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
		
		// Auto save change
		save();
	}


	//------------------------------------------------
	// iCal Export Location
	//------------------------------------------------

	/** Key for the location of last iCal export. */
	private static final String LAST_ICAL_EXPORT_LOCATION = "export.iCal"; //$NON-NLS-1$

	/**
	 * Gets the location of the last iCal export.
	 * @return the location of the last iCal export
	 */
	public String getLastICalExportLocation() {
		return doGetString(LAST_ICAL_EXPORT_LOCATION, System.getProperty("user.home"));
	}

	/**
	 * Sets the location of the last iCal export.
	 * @param iCalExportLocation the location of the last iCal export to set
	 */
	public void setLastICalExportLocation(final String iCalExportLocation) {
		userConfig.setProperty(LAST_ICAL_EXPORT_LOCATION, iCalExportLocation);
		
		// Auto save change
		save();
	}


	//------------------------------------------------
	// Description
	//------------------------------------------------

	/** Last description. */
	private static final String LAST_DESCRIPTION = "description"; //$NON-NLS-1$

	public String getLastDescription() {
		return doGetString(LAST_DESCRIPTION, "");
	}

	public void setLastDescription(final String lastDescription) {
		userConfig.setProperty(LAST_DESCRIPTION, lastDescription);
		
		// Auto save change
		save();
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
		userConfig.setProperty(ACTIVE, String.valueOf(active));
		
		// Auto save change
		save();
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
		userConfig.setProperty(ACTIVE_PROJECT_ID, String.valueOf(activeProjectId));
		
		// Auto save change
		save();
	}


	//------------------------------------------------
	// Start time
	//------------------------------------------------

	/** Active Flag. */
	private static final String START = "start"; //$NON-NLS-1$

	public LocalDateTime getStart() {
		return doGetDate(START, null);
	}

	public void setStart(final LocalDateTime start) {
		if (start == null) {
			userConfig.setProperty(START, String.valueOf(null));
		} else {
			userConfig.setProperty(START, String.valueOf(start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		}
		
		// Auto save change
		save();
	}


	//------------------------------------------------
	// Filter Settings
	//------------------------------------------------

	/** The key for the selected month of filter. */
	private static final String SELECTED_SPAN_TYPE = "filter.spanType"; //$NON-NLS-1$

	public void setFilterSelectedSpanType(final SpanType spanType) {
		userConfig.setProperty(SELECTED_SPAN_TYPE, spanType.name());
		
		// Auto save change
		save();
	}

	public SpanType getFilterSelectedSpanType() {
		final String spanTypeName = userConfig.getProperty(SELECTED_SPAN_TYPE);
		if (spanTypeName == null) {
			return null;
		}

		return SpanType.valueOf(spanTypeName);
	}

	/** The key for the selected project id of filter. */
	private static final String SELECTED_PROJECT_ID = "filter.projectId"; //$NON-NLS-1$

	public Long getFilterSelectedProjectId() {
		return doGetLong(SELECTED_PROJECT_ID, null);
	}

	public void setFilterSelectedProjectId(final long projectId) {
		userConfig.setProperty(SELECTED_PROJECT_ID, String.valueOf(Long.valueOf(projectId)));
		
		// Auto save change
		save();
	}

	/**
	 * Restore the current filter from the user settings.
	 * @return the restored filter
	 */
	public Filter restoreFromSettings() {
		final Filter filter = new Filter();

		// Restore span type
		final SpanType spanType = getFilterSelectedSpanType();
		if (spanType != null) {
			filter.setSpanType(spanType);
			filter.initTimeInterval();
		}

		return filter;
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
		
		// Auto save change
		save();
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
		userConfig.setProperty(REMEMBER_WINDOWSIZE_LOCATION, String.valueOf(rememberWindowSizeLocation));
		
		// Auto save change
		save();
	}


	//------------------------------------------------
	// Show Stopwatch
	//------------------------------------------------

	/** The key for show stopwatch. */
	public static final String SHOW_STOPWATCH = "settings.showStopwatch"; //$NON-NLS-1$

	public Boolean isShowStopwatch() {
		return doGetBoolean(SHOW_STOPWATCH, false);
	}

	public void setShowStopwatch(final boolean showStopwatch) {
		userConfig.setProperty(SHOW_STOPWATCH, String.valueOf(showStopwatch));
		
		// Auto save change
		save();
	}


	//------------------------------------------------
	// Window size
	//------------------------------------------------

	/** The key for the window size. */
	public static final String WINDOW_SIZE = "settings.windowSize"; //$NON-NLS-1$

	public Dimension getWindowSize() {
		final String encodedSize = doGetString(WINDOW_SIZE, "530.0|720.0"); //$NON-NLS-1$
		final Iterable<String> sizeValues = Splitter.on('|').split(encodedSize);
		final Iterator<String> sizeValuesIterator = sizeValues.iterator();

		final Dimension size = new Dimension(Double.valueOf(sizeValuesIterator.next()).intValue(), Double.valueOf(sizeValuesIterator.next()).intValue());
		return size;
	}

	public void setWindowSize(final Dimension size) {
		final String encodedSize = size.getWidth() + "|" + size.getHeight(); //$NON-NLS-1$
		userConfig.setProperty(WINDOW_SIZE, encodedSize);
		
		// Auto save change
		save();
	}


	//------------------------------------------------
	// Window location
	//------------------------------------------------

	/** The key for the shown category. */
	public static final String WINDOW_LOCATION = "settings.windowLocation"; //$NON-NLS-1$

	private File userConfigFile;

	public Point getWindowLocation() {
		final String encodedLocation = doGetString(WINDOW_LOCATION, "0.0|0.0"); //$NON-NLS-1$
		final Iterable<String> locationCoordinates = Splitter.on('|').split(encodedLocation);
		final Iterator<String> locationCoordinatesIterator = locationCoordinates.iterator();

		final Point location = new Point(Double.valueOf(locationCoordinatesIterator.next()).intValue(), Double.valueOf(locationCoordinatesIterator.next()).intValue());
		return location;
	}

	public void setWindowLocation(final Point location) {
		final String encodedLocation = location.getX() + "|" + location.getY(); //$NON-NLS-1$
		userConfig.setProperty(WINDOW_LOCATION, encodedLocation);
		
		// Auto save change
		save();
	}
	
	
	//------------------------------------------------
	// Stopwatch Window size
	//------------------------------------------------

	/** The key for the window size. */
	public static final String STOPWATCH_WINDOW_SIZE = "settings.stopwatchwindowSize"; //$NON-NLS-1$

	public Dimension getStopwatchWindowSize() {
		final String encodedSize = doGetString(STOPWATCH_WINDOW_SIZE, "450|35"); //$NON-NLS-1$
		final Iterable<String> sizeValues = Splitter.on('|').split(encodedSize);
		final Iterator<String> sizeValuesIterator = sizeValues.iterator();

		final Dimension size = new Dimension(Double.valueOf(sizeValuesIterator.next()).intValue(), Double.valueOf(sizeValuesIterator.next()).intValue());
		return size;
	}

	public void setStopwatchWindowSize(final Dimension size) {
		final String encodedSize = size.getWidth() + "|" + size.getHeight(); //$NON-NLS-1$
		userConfig.setProperty(STOPWATCH_WINDOW_SIZE, encodedSize);
		
		// Auto save change
		save();
	}


	//------------------------------------------------
	// Window location
	//------------------------------------------------

	/** The key for the shown category. */
	public static final String STOPWATCH_WINDOW_LOCATION = "settings.stopwatchwindowLocation"; //$NON-NLS-1$

	public Point getStopwatchWindowLocation() {
		final String encodedLocation = doGetString(STOPWATCH_WINDOW_LOCATION, "0.0|0.0"); //$NON-NLS-1$
		final Iterable<String> locationCoordinates = Splitter.on('|').split(encodedLocation);
		final Iterator<String> locationCoordinatesIterator = locationCoordinates.iterator();

		final Point location = new Point(Double.valueOf(locationCoordinatesIterator.next()).intValue(), Double.valueOf(locationCoordinatesIterator.next()).intValue());
		return location;
	}

	public void setStopwatchWindowLocation(final Point location) {
		final String encodedLocation = location.getX() + "|" + location.getY(); //$NON-NLS-1$
		userConfig.setProperty(STOPWATCH_WINDOW_LOCATION, encodedLocation);
		
		// Auto save change
		save();
	}


    //------------------------------------------------
    // Duration format
    //------------------------------------------------

    public enum DurationFormat {
        DECIMAL,
        HOURS_AND_MINUTES
    }

    /** The key for duration format. */
    public static final String DURATION_FORMAT = "settings.durationFormat"; //$NON-NLS-1$

    public DurationFormat getDurationFormat() {
        return DurationFormat.valueOf(doGetString(DURATION_FORMAT, DurationFormat.DECIMAL.name()));
    }

    public void setDurationFormat(final DurationFormat durationFormat) {
        userConfig.setProperty(DURATION_FORMAT, durationFormat.name());

        // Auto save change
        save();
    }


	//------------------------------------------------
	// Helper methods
	//------------------------------------------------

	/**
	 * Resets all settings to their default values.
	 */
	public void reset() {
		userConfig.clear();
		save();
	}

	/**
	 * Getter that handle errors gracefully meaning errors are logged 
	 * but applications continues with the default value.
	 * @param key the key of the property to get
	 * @param defaultValue the default value of the property to get
	 * @return the property value if set and correct otherwise the default value
	 */
	private String doGetString(final String key, final String defaultValue) {
		try {
			return userConfig.getProperty(key, defaultValue);
		} catch (Throwable t) {
			log.error(t.getLocalizedMessage(), t);
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
		    final String value = userConfig.getProperty(key, defaultValue == null ? null : String.valueOf(defaultValue));
			return value == null ? null : Long.valueOf(value);
		} catch (Throwable t) {
			log.error(t.getLocalizedMessage(), t);
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
			return Boolean.valueOf(userConfig.getProperty(key, String.valueOf(defaultValue)));
		} catch (Throwable t) {
			log.error(t.getLocalizedMessage(), t);
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
	private LocalDateTime doGetDate(final String key, final LocalDateTime defaultValue) {
		try {
			Long defaultMillis = null;
			if (defaultValue != null) {
				defaultMillis = defaultValue.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();	
			}

            final String dateMillisecondsString = userConfig.getProperty(key, defaultMillis != null ? String.valueOf(defaultMillis) : null);
            if (dateMillisecondsString == null) {
                return null;
            }

			final Long dateMilliseconds = Long.valueOf(dateMillisecondsString);
			if (dateMilliseconds == null) {
				return null;
			}

			return Instant.ofEpochMilli(dateMilliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (Throwable t) {
			log.error(t.getLocalizedMessage(), t);
			return defaultValue;
		}
	}
	
    public void save() {
        OutputStream out = null;
        try
        {
            out = new FileOutputStream(userConfigFile);
            userConfig.store(out, "Created at " + new Date());
        } catch (IOException e) {
        	log.error(e.getLocalizedMessage(), e);
        } finally {
        	if (out != null) {
        		try {
					out.close();
				} catch (IOException e) {
					// Ignore
				}
        	}
        }
    }

	public long getInactivityThreshold() {
		return doGetLong("settings.userInactivityThreshold", DEFAULT_INACTIVITY_THRESHOLD);
	}
}
