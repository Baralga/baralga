package org.remast.baralga.gui;

import java.awt.SystemTray;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;
import org.remast.baralga.gui.model.BaralgaDAO;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.model.ProjectActivityStateException;
import org.remast.baralga.gui.settings.ApplicationSettings;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.io.ProTrackReader;
import org.remast.swing.util.ExceptionUtils;
import org.remast.util.TextResourceBundle;

/**
 * Controls the lifecycle of the application.
 * @author remast
 */
public final class BaralgaMain {

	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

	/** The logger. */
	private static final Log log = LogFactory.getLog(BaralgaMain.class);

	//------------------------------------------------
	// Command line options
	//------------------------------------------------

	/** Property for command line option minimized (-m). */
	private boolean minimized = false;


	//------------------------------------------------
	// Application resources
	//------------------------------------------------

	/** The Tray icon. */
	private static TrayIcon tray;

	/** The lock file to avoid multiple instances of the application. */
	private static FileLock lock;

	/** The absolute path name of the log file. */
	private static String logFileName;

	public static String getLogFileName() {
		return logFileName;
	}

	/**
	 * Gets the tray icon. 
	 * @return The tray icon or <code>null</code> if a tray icon
	 * is not supported by the platform.
	 */
	public static TrayIcon getTray() {
		return tray;
	}

	/** Hide constructor. */
	private BaralgaMain() { }

	/**
	 * Main method that starts the application.
	 * @param arguments the command line arguments
	 */
	public static void main(final String[] arguments) {
		try {
			// Register general Exception Handler
			Thread.setDefaultUncaughtExceptionHandler(new ExceptionUtils.ExceptionHandler());

			final BaralgaMain mainInstance = new BaralgaMain();
			mainInstance.parseCommandLineArguments(arguments);

			migrateApplicationDirectory();

			initLogger();

			initLookAndFeel();

			initLockFile();

			final PresentationModel model = initModel();

			migrateModel(model, mainInstance);

			initShutdownHook(model);

			// Register Exception Handler for EventDispatchThread
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(new ExceptionUtils.ExceptionHandlingEventProcessor());

			final MainFrame mainFrame = initMainFrame(model, mainInstance);

			initTrayIcon(mainInstance, model, mainFrame);
		} catch (Exception e) {
			log.error(e, e);
			ExceptionUtils.showErrorDialog(e);
			System.exit(1);
		} catch (Error e) {
			log.error(e, e);
			ExceptionUtils.showErrorDialog(e);
			System.exit(1);
		}
	}

	/**
	 * Parses the parameters from the given command line arguments.
	 * @param arguments the command line arguments to parse
	 */
	private void parseCommandLineArguments(final String[] arguments) {
		if (arguments == null || arguments.length == 0) {
			return;
		}

		for (String argument : arguments) {
			if (argument.startsWith("-m=")) { //$NON-NLS-1$
				this.minimized = Boolean.parseBoolean(argument.substring("-m=".length())); //$NON-NLS-1$
			}
		}

	}

	/**
	 * Initializes the main frame.
	 * @param model the model to be displayed
	 * @param mainInstance the main instance
	 * @return the initialized main frame
	 */
	private static MainFrame initMainFrame(final PresentationModel model,
			final BaralgaMain mainInstance) {
		if (log.isDebugEnabled()) {
			log.debug("Initializing main frame ..."); //$NON-NLS-1$
		}

		final MainFrame mainFrame = new MainFrame(model);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				mainFrame.setVisible(!mainInstance.minimized);
			}
		});

		return mainFrame;
	}

	/**
	 * Initializes the lock file.
	 */
	private static void initLockFile() {
		if (log.isDebugEnabled()) {
			log.debug("Initializing lock file ..."); //$NON-NLS-1$
		}

		if (!tryLock()) {
			JOptionPane.showMessageDialog(
					null, 
					textBundle.textFor("BaralgaMain.ErrorAlreadyRunning.Message"),  //$NON-NLS-1$
					textBundle.textFor("BaralgaMain.ErrorAlreadyRunning.Title"),  //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE
			);
			log.info(textBundle.textFor("BaralgaMain.ErrorAlreadyRunning.Message")); //$NON-NLS-1$
			System.exit(0);
		}
	}

	/**
	 * Initializes the lock file.
	 * @param model the model to be displayed
	 * @param mainInstance the main instance
	 * @param mainFrame
	 */
	private static void initTrayIcon(final BaralgaMain mainInstance,
			final PresentationModel model, final MainFrame mainFrame) {
		if (log.isDebugEnabled()) {
			log.debug("Initializing tray icon ..."); //$NON-NLS-1$
		}

		// Create try icon.
		try {
			if (SystemTray.isSupported()) {
				tray = new TrayIcon(model, mainFrame);
			} else {
				tray = null;
			}
		} catch (UnsupportedOperationException e) {
			// Tray icon not supported on the current platform.
			tray = null;
		}

		if (tray != null && mainInstance.minimized) {
			tray.show();
		}
	}

	/**
	 * Initializes the shutdown hook process.
	 * @param model
	 */
	private static void initShutdownHook(final PresentationModel model) {
		if (log.isDebugEnabled()) {
			log.debug("Initializing shutdown hook ..."); //$NON-NLS-1$
		}

		Runtime.getRuntime().addShutdownHook(
				new Thread("Baralga shutdown ...") { //$NON-NLS-1$

					@Override
					public void run() {
						if (log.isDebugEnabled()) {
							log.debug("Shutdown started."); //$NON-NLS-1$
						}

						try {
							// 1. Stop current activity (if any)
							if (model.isActive()) {
								try {
									model.stop(false);
								} catch (ProjectActivityStateException e) {
									// ignore
								}
							}
						} catch (Exception e) {
							log.error(e, e);
						} catch (Error e) {
							log.error(e, e);
						}

						try {
							// 2. Save model
							model.getDAO().close();
						} catch (Exception e) {
							log.error(e, e);
						} catch (Error e) {
							log.error(e, e);
						} finally {
							// 3. Release lock
							releaseLock();
						}

						if (log.isDebugEnabled()) {
							log.debug("Shutdown finished."); //$NON-NLS-1$
						}
					}

				}
		);
	}

	/**
	 * Initializes the model from the stored data file or creates a new one.
	 * @return the model
	 * @throws SQLException 
	 */
	private static PresentationModel initModel() throws SQLException {
		log.debug("Initializing model..."); //$NON-NLS-1$

		// Initialize with new site
		final PresentationModel model = new PresentationModel();

		final BaralgaDAO dao = new BaralgaDAO();
		dao.init();

		model.setDAO(dao);

		model.initialize();
		return model;
	}

	private static void migrateModel(final PresentationModel model, BaralgaMain mainInstance) {
		final File dataFile = new File(UserSettings.instance().getDataFileLocation());
		if (dataFile.exists() && dataFile.canRead()) {
			ProTrackReader reader = new ProTrackReader();
			try {
				reader.read(dataFile);

				final ProTrack data = reader.getData();

				// 1. Add projects
				// -- Add normal projects
				for (Project project : data.getProjects()) {
					model.addProject(project, mainInstance);
				}

				// -- Add deleted projects
				for (Project project : data.getDeletedProjects()) {
					model.addProject(project, mainInstance);
				}

				// 2. Add activities
				model.addActivities(data.getActivities(), mainInstance);

				// 3. Remove deleted projects including all associated activities
				for (Project project : data.getDeletedProjects()) {
					model.removeProject(project, mainInstance);
				}

				// 4. Rename data file as backup
				final File dataFileBackup = new File(UserSettings.instance().getDataFileLocation() + ".pre15Backup");
				final boolean renameSuccessfull = dataFile.renameTo(dataFileBackup);
				if (!renameSuccessfull) {
					throw new RuntimeException("Could not rename data file " + 
							dataFile.getAbsolutePath() + " to " + 
							dataFileBackup.getAbsolutePath() + "." +
							"Please rename manually."
					);
				} else {
					log.info("Successfully renamed data file from " +
							dataFile.getAbsolutePath() + " to " + 
							dataFileBackup.getAbsolutePath() + "."
					);
				}

				log.info("Successfully migrated the model to version 1.5 and following.");
			} catch (IOException e) {
				log.error(e, e);
			}
		}
	}

	/**
	 * Migrates the application data directory. Up to version 1.5 the data directory used to
	 * be <code>${user.home}/.ProTrack</code>. From version 1.5 on the data directory is located
	 * at <code>${user.home}/.Baralga</code>.
	 */
	private static void migrateApplicationDirectory() {
		final File oldDefaultDataDirectory = new File(System.getProperty("user.home") + File.separator + ".ProTrack");
		final File defaultDataDirectory = new File(System.getProperty("user.home") + File.separator + ".Baralga");

		if (oldDefaultDataDirectory.exists() && oldDefaultDataDirectory.isDirectory() && !defaultDataDirectory.exists()) {
			final boolean renameSuccessfull = oldDefaultDataDirectory.renameTo(defaultDataDirectory);

			if (!renameSuccessfull) {
				throw new RuntimeException("Could not rename application directory " + 
						oldDefaultDataDirectory.getAbsolutePath() + " to " + 
						defaultDataDirectory.getAbsolutePath() + "." +
						"Please rename manually."
				);
			} else {
				log.info("Successfully renamed application directory from " +
						oldDefaultDataDirectory.getAbsolutePath() + " to " + 
						defaultDataDirectory.getAbsolutePath() + "."
				);
			}
		}

	}

	/**
	 * Initialize the logger of the application.
	 * @throws IOException 
	 */
	private static void initLogger() throws IOException {
		log.debug("Initializing logger ..."); //$NON-NLS-1$
		DOMConfigurator.configure(BaralgaMain.class.getResource("/log4j.xml")); //$NON-NLS-1$

		logFileName = ApplicationSettings.instance().getApplicationDataDirectory().getAbsolutePath() + File.separator + "log" + File.separator + "baralga.log";
		final Appender mainAppender = new DailyRollingFileAppender(new PatternLayout("%d{ISO8601} %-5p [%t] %c: %m%n"), logFileName, "'.'yyyy-MM-dd");

		final Logger root = Logger.getRootLogger();
		root.addAppender(mainAppender);
	}

	/**
	 * Initialize the look & feel of the application.
	 */
	private static void initLookAndFeel() {
		log.debug("Initializing look and feel ..."); //$NON-NLS-1$
		// b) Try system look & feel
		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName()
			);
		} catch (Exception ex) {
			log.error(ex, ex);
		}
	}

	/**
	 * Tries to create and lock a lock file at <code>${user.home}/.ProTrack/lock</code>.
	 * @return <code>true</code> if the lock could be acquired. <code>false</code> if
	 *   the lock is held by another program
	 * @throws RuntimeException if an I/O error occurred
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", justification = "Its irrelevant if lock file already existed or not.")
	private static boolean tryLock() {
		checkOrCreateBaralgaDir();
		final File lockFile = new File(UserSettings.getLockFileLocation());
		try {
			if (!lockFile.exists()) {
				lockFile.createNewFile();
			}

			final FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel(); //$NON-NLS-1$
			lock = channel.tryLock();

			return lock != null;
		} catch (IOException e) {
			final String error = textBundle.textFor("ProTrackMain.8"); //$NON-NLS-1$
			log.error(error, e);
			throw new RuntimeException(error);
		}
	}

	/**
	 * Checks whether the Baralga directory exists and creates it if necessary.
	 */
	private static void checkOrCreateBaralgaDir() {
		final File baralgaDir = ApplicationSettings.instance().getApplicationDataDirectory();

		boolean baralgaDirCreated = true;
		if (!baralgaDir.exists()) {
			baralgaDirCreated = baralgaDir.mkdir();
		}

		if (!baralgaDirCreated) {
			throw new RuntimeException("Could not create directory at " + baralgaDir.getAbsolutePath() + ".");
		}
	}

	/**
	 * Releases the lock file created with {@link #createLock()}.
	 */
	private static void releaseLock() {
		if (lock == null) {
			return;
		}

		final File lockFile = new File(UserSettings.getLockFileLocation());

		try {
			lock.release();
		} catch (IOException e) {
			log.error(e, e);
		} finally {
			try {
				lock.channel().close();
			} catch (Exception e) {
				// ignore
			}
		}

		final boolean deleteSuccessfull = lockFile.delete();
		if (!deleteSuccessfull) {
			log.warn("Could not delete lock file at " + lockFile.getAbsolutePath() + ". Please delete manually.");
		}
	}
}
