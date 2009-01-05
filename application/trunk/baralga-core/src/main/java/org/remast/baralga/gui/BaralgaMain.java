package org.remast.baralga.gui;

import java.awt.SystemTray;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.model.ProjectActivityStateException;
import org.remast.baralga.gui.model.io.DataBackup;
import org.remast.baralga.gui.model.io.SaveTimer;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.io.ProTrackReader;
import org.remast.util.TextResourceBundle;

/**
 * Controls the lifecycle of the application.
 * @author remast
 */
public class BaralgaMain {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

    /** The logger. */
    private static final Log log = LogFactory.getLog(BaralgaMain.class);

    //------------------------------------------------
    // Command line options
    //------------------------------------------------

    /** Property for command line option minimized (-m). */
    private boolean minimized = false;

    /** The interval in minutes in which the data is saved to the disk. */
    private static final int SAVE_TIMER_INTERVAL = 3;


    //------------------------------------------------
    // Application resources
    //------------------------------------------------

    /** The Tray icon. */
    private static TrayIcon tray;

    /** The lock file to avoid multiple instances of the application. */
    private static FileLock lock;

    /** The timer to periodically save to disk. */
    private static Timer timer;

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
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        try {
            final BaralgaMain mainInstance = new BaralgaMain();

            initLogger();

            initLookAndFeel();

            initLockFile();

            final PresentationModel model = initModel();

            initTimer(model);

            initShutdownHook(model);

            final MainFrame mainFrame = initMainFrame(model, mainInstance);

            initTrayIcon(mainInstance, model, mainFrame);
        } catch (Exception e) {
            log.error(e, e);
        } catch (Throwable t) {
            log.error(t, t);
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
        log.debug("Initializing main frame ...");
        final MainFrame mainFrame = new MainFrame(model);
        mainFrame.setVisible(!mainInstance.minimized);
        return mainFrame;
    }

    /**
     * Initializes the lock file.
     */
    private static void initLockFile() {
        log.debug("Initializing lock file ...");
        if (!tryLock()) {
            JOptionPane.showMessageDialog(null, textBundle.textFor("BaralgaMain.ErrorAlreadyRunning"), "Error", JOptionPane.ERROR_MESSAGE);
            log.info(textBundle.textFor("BaralgaMain.ErrorAlreadyRunning")); //$NON-NLS-1$
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
        log.debug("Initializing tray icon ...");

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
        log.debug("Initializing shutdown hook ...");

        Runtime.getRuntime().addShutdownHook(
                new Thread("Baralga shutdown ...") {

                    @Override
                    public void run() {
                        // 1. Stop current activity (if any)
                        if (model.isActive()) {
                            try {
                                model.stop(false);
                            } catch (ProjectActivityStateException e) {
                                // ignore
                            }
                        }

                        // 2. Save model
                        try {
                            model.save();
                        } catch (Exception e) {
                            log.error(e, e);
                        } catch (Throwable t) {
                            log.error(t, t);
                        } finally {
                            // 3. Release lock
                            releaseLock();
                        }
                    }

                }
        );
    }

    /**
     * Initializes the model from the stored data file or creates a new one.
     * @return the model
     */
    private static PresentationModel initModel() {
        log.debug("Initializing model...");

        // Initialize with new site
        final PresentationModel model = new PresentationModel();

        final String dataFileLocation = Settings.getDataFileLocation();
        final File file = new File(dataFileLocation);

        try {
            if (file.exists()) {
                final ProTrack data = readData(file);

                // Reading data file was successful.
                model.setData(data);
            }
        } catch (IOException dataFileIOException) {
            // Make a backup copy of the corrupt file
            DataBackup.saveCorruptDataFile();

            // Reading data file was not successful so we try the backup files. 
            final List<File> backupFiles = DataBackup.getBackupFiles();

            if (CollectionUtils.isNotEmpty(backupFiles)) {
                for (File backupFile : backupFiles) {
                    try {
                        final ProTrack data = readData(backupFile);
                        model.setData(data);

                        final Date backupDate = DataBackup.getDateOfBackup(backupFile);
                        String backupDateString = backupFile.getName();
                        if (backupDate != null)  {
                            backupDateString = DateFormat.getDateTimeInstance().format(backupDate);
                        }

                        JOptionPane.showMessageDialog(null, 
                                textBundle.textFor("BaralgaMain.DataLoading.ErrorText", backupDateString), //$NON-NLS-1$
                                textBundle.textFor("BaralgaMain.DataLoading.ErrorTitle"), //$NON-NLS-1$
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        break;
                    } catch (IOException backupFileIOException) {
                        log.error(backupFileIOException, backupFileIOException);
                    }
                }
            } else {
                // Data corrupt and no backup file found
                JOptionPane.showMessageDialog(null, 
                        textBundle.textFor("BaralgaMain.DataLoading.ErrorTextNoBackup"), //$NON-NLS-1$
                        textBundle.textFor("BaralgaMain.DataLoading.ErrorTitle"), //$NON-NLS-1$
                        JOptionPane.ERROR_MESSAGE
                );
            }

        }
        return model;
    }

    /**
     * Read ProTrack data from the given file.
     * @param file the file to be read
     * @return the data read from file or null if the file is null or doesn't exist
     * @throws IOException on error reading file
     */
    private static ProTrack readData(final File file) throws IOException {
        // Check for saved data
        if (file != null && file.exists()) {
            ProTrackReader reader;
            try {
                reader = new ProTrackReader();
                reader.read(file);
                return reader.getData();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        return null;
    }

    /**
     * Initialize the logger of the application.
     */
    private static void initLogger() {
        log.debug("Initializing logger ...");
        DOMConfigurator.configure(BaralgaMain.class.getResource("/log4j.xml"));
    }

    /**
     * Initialize the look & feel of the application.
     */
    private static void initLookAndFeel() {
        log.debug("Initializing look and feel ...");
        try {
            // a) Try windows
            UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel"); //$NON-NLS-1$
        } catch (Exception e) {
            // b) Try system look & feel
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        }
    }

    /**
     * Initialize the timer to automatically save the model.
     * @see #SAVE_TIMER_INTERVAL
     * @param model the model to be saved
     */
    private static void initTimer(final PresentationModel model) {
        log.debug("Initializing timer ...");
        timer = new Timer();
        timer.scheduleAtFixedRate(new SaveTimer(model), 1000 * 60 * SAVE_TIMER_INTERVAL, 1000 * 60 * SAVE_TIMER_INTERVAL);
    }

    /**
     * Tries to create and lock a lock file at <code>${user.home}/.ProTrack/lock</code>.
     * @return <code>true</code> if the lock could be acquired. <code>false</code> if
     *   the lock is held by another program
     * @throws RuntimeException if an I/O error occurred
     */
    private static boolean tryLock() {
        checkOrCreateBaralgaDir();
        final File lockFile = new File(Settings.getLockFileLocation());
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
        final File baralgaDir = Settings.getBaralgaDirectory();
        if (!baralgaDir.exists()) {
            baralgaDir.mkdir();
        }
    }


    /**
     * Releases the lock file created with {@link #createLock()}.
     */
    private static void releaseLock() {
        if (lock == null) {
            return;
        }

        final File lockFile = new File(Settings.getLockFileLocation());

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

        lockFile.delete();
    }
}
