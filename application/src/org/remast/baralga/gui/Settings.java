package org.remast.baralga.gui;

import java.io.File;
import java.util.prefs.Preferences;

import org.remast.baralga.BaralgaMain;

public class Settings {

    /** Node for ProTrack user preferences. */
    public final static Preferences PREFS = BaralgaMain.USER_PREFERENCES;

    
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

    public static String getLastExcelExportLocation() {
        return PREFS.get(LAST_EXCEL_EXPORT_LOCATION, System.getProperty("user.home")); //$NON-NLS-1$
    }

    public static void setLastExcelExportLocation(String excelExportLocation) {
        PREFS.put(LAST_EXCEL_EXPORT_LOCATION, excelExportLocation);
    }

}
