package org.remast.baralga.gui;

import java.awt.SystemTray;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.SQLException;
import java.util.Timer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Appender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;
import org.remast.baralga.gui.dialogs.UserInactivityReminderDialog;
import org.remast.baralga.gui.dialogs.StopWatch;
import org.remast.baralga.gui.model.CWMouseHook;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.model.ProjectActivityStateException;
import org.remast.baralga.gui.settings.ApplicationSettings;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.BaralgaDAO;
import org.remast.baralga.model.io.SaveTimer;
import org.remast.swing.util.ExceptionUtils;
import org.remast.util.TextResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Platform;

/**
 * Controls the lifecycle of the application.
 * @author remast
 */
public final class BaralgaMain {

	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

	/** The logger. */
	private static final Logger log = LoggerFactory.getLogger(BaralgaMain.class);

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

	/** The timer to periodically save to disk. */
	private static Timer timer;
	
	/** The absolute path name of the log file. */
	private static String logFileName;

	/** The interval in minutes in which the data is saved to the disk. */
	private static final int SAVE_TIMER_INTERVAL = 3;
	
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

			checkForOldApplicationDirectory();

			initLogger();
			
			initVersion();

			initLookAndFeel();

			initLockFile();

			final PresentationModel model = initModel();

			initShutdownHook(model);

			initTimer(model);

			// Register Exception Handler for EventDispatchThread
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(new ExceptionUtils.ExceptionHandlingEventProcessor());

			final MainFrame mainFrame = initMainFrame(model, mainInstance);
			initStopWatch(model);
			
			initUserInactivityRecognition(model);

			initTrayIcon(mainInstance, model, mainFrame);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			ExceptionUtils.showErrorDialog(e);
			System.exit(1);
		} catch (Error e) {
			log.error(e.getLocalizedMessage(), e);
			ExceptionUtils.showErrorDialog(e);
			System.exit(1);
		}
	}

	private static void initUserInactivityRecognition(PresentationModel model) {
		//TODO: Please add addition classes to get Mouse and Keyboard events from different operating system. I do only have Windows available
		switch(Platform.getOSType()) {
		case Platform.WINDOWS:
			CWMouseHook windowsMouseHook = new CWMouseHook(model);
			windowsMouseHook.setMouseHook();
			break;
		default:
			log.warn("InactivityRecognition not implemented for Operating System. Feature will be disabled!");
			break;
		}
		
		//Creating the dialog. It handles visiblity automaticaly by events from eventHub.
		new UserInactivityReminderDialog(model);
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
	 * Initializes the stop watch.
	 * @param model the model to be displayed
	 * @return the initialized stopwatch
	 */
	private static StopWatch initStopWatch(final PresentationModel model) {
		if (log.isDebugEnabled()) {
			log.debug("Initializing stopwatch ..."); //$NON-NLS-1$
		}

		final StopWatch stopwatch = new StopWatch(model);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				stopwatch.setVisible(UserSettings.instance().isShowStopwatch());
			}
		});

		return stopwatch;
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

		if (tray != null) {
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
							log.error(e.getLocalizedMessage(), e);
						} catch (Error e) {
							log.error(e.getLocalizedMessage(), e);
						}

						try {
							// 2. Save model
							model.getDAO().close();
						} catch (Exception e) {
							log.error(e.getLocalizedMessage(), e);
						} catch (Error e) {
							log.error(e.getLocalizedMessage(), e);
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
	 * Initialize the timer to automatically save the model.
	 * @see #SAVE_TIMER_INTERVAL
	 * @param model the model to be saved
	 */
	private static void initTimer(final PresentationModel model) {
		log.debug("Initializing timer ...");
		timer = new Timer("Baralga save timer.");
		timer.scheduleAtFixedRate(new SaveTimer(model), 1000 * 60 * SAVE_TIMER_INTERVAL, 1000 * 60 * SAVE_TIMER_INTERVAL);
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

	/**
	 * Check for old  application data directory and displays info dialog if present.
	 */
	private static void checkForOldApplicationDirectory() {
		final File oldDefaultDataDirectory = new File(System.getProperty("user.home") + File.separator + ".ProTrack");
		if (oldDefaultDataDirectory.exists() && oldDefaultDataDirectory.canRead()) {
			log.error("Data from version 1.4.x is not migrated any more. Please update to 1.5 or 1.6 first before using this version.");
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

		final org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
		root.addAppender(mainAppender);
	}
	
	private static void initVersion() {
		log.info("Starting Baralga version {}.", BaralgaMain.class.getPackage().getImplementationVersion());
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
			log.error(ex.getLocalizedMessage(), ex);
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
		RandomAccessFile randomAccessFile;
		try {
			if (!lockFile.exists()) {
				lockFile.createNewFile();
			}

			randomAccessFile = new RandomAccessFile(lockFile, "rw"); //$NON-NLS-1$
			final FileChannel channel = randomAccessFile.getChannel();
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
		
		log.info("Using application data directory \"{}\".", baralgaDir.getAbsolutePath()); //$NON-NLS-1$
	}

	/**
	 * Releases the lock file created with {@link #tryLock()}.
	 */
	private static void releaseLock() {
		if (lock == null) {
			return;
		}

		final File lockFile = new File(UserSettings.getLockFileLocation());

		try {
			lock.release();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage(), e);
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
