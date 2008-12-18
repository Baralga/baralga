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
import org.remast.baralga.Messages;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.model.ProjectActivityStateException;
import org.remast.baralga.gui.model.io.DataBackup;
import org.remast.baralga.gui.model.io.SaveTimer;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.io.ProTrackReader;

public class BaralgaMain {

    /** The logger. */
    private static final Log log = LogFactory.getLog(BaralgaMain.class);

    //------------------------------------------------
    // Command line options
    //------------------------------------------------

    /** Property for command line option minimized (-m). */
    private boolean minimized = false;

    /** The interval in minutes in which the data is saved to the disk. */
    private static final int SAVE_TIMER_INTERVAL = 5;


    //------------------------------------------------
    // User settings
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

    /**
     * Main method that starts the application.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        try {
            initLogger();

            BaralgaMain inst = new BaralgaMain();

            // Set look & feel
            initLookAndFeel();

            if (!tryLock()) {
                JOptionPane.showMessageDialog(null, Messages.getString("BaralgaMain.ErrorAlreadyRunning"), "Error", JOptionPane.ERROR_MESSAGE);
                log.info(Messages.getString("BaralgaMain.ErrorAlreadyRunning")); //$NON-NLS-1$
                return;
            }

            // Initialize with new site
            final PresentationModel model = new PresentationModel();
            model.setDirty(true);

            File file = null;
            final String proTrackFileLocation = Settings.getProTrackFileLocation();
            file = new File(proTrackFileLocation);

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
                                    Messages.getString("BaralgaMain.DataLoading.ErrorText", backupDateString), //$NON-NLS-1$
                                    Messages.getString("BaralgaMain.DataLoading.ErrorTitle"), //$NON-NLS-1$
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
                            Messages.getString("BaralgaMain.DataLoading.ErrorTextNoBackup"), //$NON-NLS-1$
                            Messages.getString("BaralgaMain.DataLoading.ErrorTitle"), //$NON-NLS-1$
                            JOptionPane.ERROR_MESSAGE
                    );
                }

            }

            // Start timer
            initTimer(model);

            final MainFrame mainFrame = new MainFrame(model);
            
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

            if (!inst.minimized) {
                mainFrame.setVisible(true);
            } else {
                if (tray != null) {
                    tray.show();
                }
            }


            Runtime.getRuntime().addShutdownHook(new Thread("Baralga shutdown ...") {

                @Override
                public void run() {
                    // 1. Stop current activity, if any.
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
                    } finally {
                        // 3. Release lock
                        releaseLock();
                    }
                }

            });
        } catch (Exception e) {
            log.error(e, e);
        } catch (Throwable t) {
            log.error(t, t);
        }
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
        DOMConfigurator.configure(BaralgaMain.class.getResource("/log4j.xml"));
    }

    /**
     * Initialize the look & feel of the application.
     */
    private static void initLookAndFeel() {
        try {
            // a) Try windows
            UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel"); //$NON-NLS-1$
        } catch (Exception e) {
            // b) Try system look & feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    /**
     * Initialize the timer to automatically save the model.
     * @see #SAVE_TIMER_INTERVAL
     * @param model the model to be saved
     */
    private static void initTimer(final PresentationModel model) {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new SaveTimer(model), 1000 * 60 * SAVE_TIMER_INTERVAL, 1000 * 60 * SAVE_TIMER_INTERVAL);
    }

    /**
     * Tries to create and lock a lock file at <code>${user.home}/.ProTrack/lock</code>.
     * 
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
            if (lock != null) {
              return true;
            } else {
              return false;
            }
        } catch (IOException e) {
            final String error = Messages.getString("ProTrackMain.8"); //$NON-NLS-1$
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
        File lockFile = new File(Settings.getLockFileLocation());

        if(lock != null) {
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
        }
        lockFile.delete();
    }
}
