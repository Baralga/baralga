package org.remast.baralga.gui.model.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.gui.Settings;

/**
 * @author remast
 */
public class DataBackupStrategy {

    /** The logger. */
    private static final Log log = LogFactory.getLog(DataBackupStrategy.class);

    /** The date format for dates used in the names of backup files. */
    private static final SimpleDateFormat BACKUP_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /** The full path of the backed up corrupt data file. */
    private static final String ERROR_FILE_PATH = Settings.getProTrackFileLocation() + ".Error";

    /** The name of the backed up corrupt data file. */
    private static final String ERROR_FILE_NAME = Settings.DEFAULT_FILE_NAME + ".Error";

    /** The number of backup files to keep. */
    private static final int NUMBER_OF_BACKUPS = 3;

    /**
     * Create a backup from given file.
     * @param toBackup the file to be backed up
     */
    public static void createBackup(final File toBackup) {
        if (toBackup == null || !toBackup.exists()) {
            return;
        }

        try {
            FileUtils.copyFile(
                    toBackup, 
                    new File(Settings.getProTrackFileLocation() + "." + BACKUP_DATE_FORMAT.format(new Date()))
            );
            cleanupBackupFiles();
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /**
     * Cleans up old backup files so that not more backup files than <code>NUMBER_OF_BACKUPS</code> exist.
     */
    private static void cleanupBackupFiles() {
        List<File> backupFiles = getBackupFiles();
        if (backupFiles.size() > NUMBER_OF_BACKUPS) {
            final int numberOfFilesToDelete = backupFiles.size() - NUMBER_OF_BACKUPS;

            for (int i = 1; i <= numberOfFilesToDelete; i++) {
                final File toDelete = backupFiles.get(backupFiles.size() - i);
                final boolean successfull = toDelete.delete();
                if (!successfull) {
                    log.error("Could not delete file " + toDelete.getAbsolutePath() + ".");
                }
            }
        }
    }

    /**
     * Get a list of all backup files in order of the backup date (with the latest backup as first). If 
     * there there are no backups <code>Collections.EMPTY_LIST</code> is returned.
     * @return
     */
    public static List<File> getBackupFiles()  {
        final SortedMap<Date, File> sortedBackupFiles = new TreeMap<Date, File>();

        File dir = Settings.DEFAULT_DIRECTORY;
        final String [] backupFiles = dir.list(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (!StringUtils.equals(ERROR_FILE_NAME, name) 
                        && !StringUtils.equals(Settings.DEFAULT_FILE_NAME, name) 
                        && name.startsWith(Settings.DEFAULT_FILE_NAME)) {
                    return true;
                }

                return false;
            }

        });

        if (backupFiles == null) {
            return Collections.emptyList();
        }

        for (String backupFile : backupFiles) {
            try {
                Date backupDate = BACKUP_DATE_FORMAT.parse(backupFile.substring(Settings.DEFAULT_FILE_NAME.length()+1));
                sortedBackupFiles.put(backupDate, new File(Settings.DEFAULT_DIRECTORY + File.separator + backupFile));
            } catch (ParseException e) {
                log.error(e, e);
            }
        }

        // Order the list by the date of the backup with the latest backup at front.
        final List<File> backupFileList = new ArrayList<File>(sortedBackupFiles.size());
        int numberOfBackups = sortedBackupFiles.size();
        for (int i = 0; i < numberOfBackups; i++) {
            final Date backupDate = sortedBackupFiles.lastKey();

            backupFileList.add(sortedBackupFiles.get(backupDate));
            sortedBackupFiles.remove(backupDate);
        }

        return backupFileList;
    }

    /**
     * Get the date on which the backup file has been created. 
     * @param backupFile
     * @return The date on which the backup file has been created. If no date could be inferred <code>null</code> is returned.
     */
    public static Date getDateOfBackup(final File backupFile) {
        try {
            return BACKUP_DATE_FORMAT.parse(backupFile.getName().substring(Settings.DEFAULT_FILE_NAME.length()+1));
        } catch (Exception e) {
            log.error(e, e);
            return null;
        }
    }

    /**
     * Make a backup copy of the corrupt file.
     */
    public static void saveCorruptDataFile() {
        try {
            FileUtils.copyFile(new File(Settings.getProTrackFileLocation()), new File(ERROR_FILE_PATH));
        } catch (IOException e) {
            log.error(e, e);
        }
    }
}
