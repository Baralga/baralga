package org.remast.baralga.model.io;

import org.apache.commons.lang.StringUtils;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.ApplicationSettings;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.export.Exporter;
import org.remast.baralga.model.export.XmlExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Creates and reads backups.
 */
public class DataBackup {

	/** The logger. */
	private static final Logger log = LoggerFactory.getLogger(DataBackup.class);

	/** The date format for dates used in the names of backup files. */
	private static final SimpleDateFormat BACKUP_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

	/** The name of the backed up corrupt data file. */
	private static final String ERROR_FILE_NAME = UserSettings.DEFAULT_FILE_NAME + ".Error";

	/** The number of backup files to keep. */
	private static final int NUMBER_OF_BACKUPS = 3;

	private PresentationModel presentationModel;

	public DataBackup(final PresentationModel presentationModel) {
		this.presentationModel = presentationModel;
	}

	public void createBackup() {
		OutputStream outputStream = null;
		final File backupFile = new File(UserSettings.instance().getDataFileLocation() + "." + BACKUP_DATE_FORMAT.format(new Date()));
		try {
			outputStream = new FileOutputStream(backupFile);

			final Exporter exporter = new XmlExporter();

			// Get activities for export
			Collection<ProjectActivity> activitiesForExport;
			if (exporter.isFullExport()) {
				activitiesForExport = presentationModel.getAllActivitiesList();
			} else {
				activitiesForExport = presentationModel.getActivitiesList();
			}

			synchronized (activitiesForExport) {
				exporter.export(
						activitiesForExport,
						presentationModel.getFilter(),
						outputStream
						);
			}

			// Make sure everything is written.
			outputStream.flush();
		} catch (Throwable t) {
			log.error(t.getLocalizedMessage(), t);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable t) {
					// Ignore
				}
			}

			cleanupBackupFiles();
		}
	}

	/**
	 * Cleans up old backup files so that not more backup files than <code>NUMBER_OF_BACKUPS</code> exist.
	 */
	private void cleanupBackupFiles() {
		final List<File> backupFiles = getBackupFiles();
		if (backupFiles != null && backupFiles.size() > NUMBER_OF_BACKUPS) {
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
	 * @return the list of backup files
	 */
	public List<File> getBackupFiles()  {
		final SortedMap<Date, File> sortedBackupFiles = new TreeMap<>();

		final File dir = ApplicationSettings.instance().getApplicationDataDirectory();
		final String [] backupFiles = dir.list(new FilenameFilter() {

			public boolean accept(final File dir, final String name) {
				return !StringUtils.equals(ERROR_FILE_NAME, name)
						&& !StringUtils.equals(UserSettings.DEFAULT_FILE_NAME, name) 
						&& name.startsWith(UserSettings.DEFAULT_FILE_NAME);
			}

		});

		if (backupFiles == null) {
			return Collections.emptyList();
		}

		for (String backupFile : backupFiles) {
			try {
				final Date backupDate = BACKUP_DATE_FORMAT.parse(backupFile.substring(UserSettings.DEFAULT_FILE_NAME.length() + 1));
				sortedBackupFiles.put(backupDate, new File(ApplicationSettings.instance().getApplicationDataDirectory() + File.separator + backupFile));
			} catch (ParseException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}

		// Order the list by the date of the backup with the latest backup at front.
		final List<File> backupFileList = new ArrayList<>(sortedBackupFiles.size());
		final int numberOfBackups = sortedBackupFiles.size();
		for (int i = 0; i < numberOfBackups; i++) {
			final Date backupDate = sortedBackupFiles.lastKey();

			backupFileList.add(sortedBackupFiles.get(backupDate));
			sortedBackupFiles.remove(backupDate);
		}

		return backupFileList;
	}

}
