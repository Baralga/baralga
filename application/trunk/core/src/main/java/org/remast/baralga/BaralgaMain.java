package org.remast.baralga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channel;
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
import org.remast.baralga.gui.BaralgaTray;
import org.remast.baralga.gui.MainFrame;
import org.remast.baralga.gui.Messages;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.io.DataBackupStrategy;
import org.remast.baralga.model.io.ProTrackReader;
import org.remast.baralga.model.io.SaveTimer;
import org.remast.baralga.model.utils.ProTrackUtils;

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
    private static BaralgaTray tray;

    private static FileLock lock;

    private static Timer timer;

    public static BaralgaTray getTray() {
        return tray;
    }

    public static void main(String[] args) {
        try {
            initLogger();

            BaralgaMain inst = new BaralgaMain();

            // Set look & feel
            initLookAndFeel();

            if (existsLock()) {
                JOptionPane.showMessageDialog(null, Messages.getString("BaralgaMain.ErrorAlreadyRunning"), "Error", JOptionPane.ERROR_MESSAGE);
                log.info(Messages.getString("BaralgaMain.ErrorAlreadyRunning")); //$NON-NLS-1$
                return;
            } else {
                createLock();
            }

            // Initialize with new site
            final PresentationModel model = new PresentationModel();
            model.setDirty(true);
            model.setData(new ProTrack());

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
                DataBackupStrategy.saveCorruptDataFile();

                // Reading data file was not successful so we try the backup files. 
                final List<File> backupFiles = DataBackupStrategy.getBackupFiles();

                if (CollectionUtils.isNotEmpty(backupFiles)) {
                    for (File backupFile : backupFiles) {
                        try {
                            final ProTrack data = readData(backupFile);
                            model.setData(data);

                            final Date backupDate = DataBackupStrategy.getDateOfBackup(backupFile);
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
            tray = new BaralgaTray(model, mainFrame);

            if (!inst.minimized) {
                mainFrame.setVisible(true);
            } else {
                tray.show();
            }


            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    // 1. Stop timer
                    timer.cancel();

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
    private static ProTrack readData(File file) throws IOException {
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
        DOMConfigurator.configure(BaralgaMain.class.getResource("/config/log4j.xml"));
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
        timer = new Timer();
        timer.scheduleAtFixedRate(new SaveTimer(model), 1000 * 60 * SAVE_TIMER_INTERVAL, 1000 * 60 * SAVE_TIMER_INTERVAL);
    }

    /**
     * Create a lock file in the directory <code>${user.home}/.ProTrack/lock</code>.
     */
    private static void createLock() {
        File lockFile = new File(Settings.getLockFileLocation());
        try {
            lockFile.createNewFile();

            FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel(); //$NON-NLS-1$
            lock = channel.lock();
        } catch (IOException e) {
            final String error = Messages.getString("ProTrackMain.8"); //$NON-NLS-1$
            log.error(error, e);
            throw new RuntimeException(error);
        }
    }

    /**
     * Releases the lock file created with {@link #releaseLock()}.
     */
    private static void releaseLock() {
        File lockFile = new File(Settings.getLockFileLocation());

        if(lock != null) {
            try {
                Channel channel = lock.channel();
                lock.release();
                channel.close();
            } catch (IOException e) {
                log.error(e, e);
            }
        }
        lockFile.delete();
    }

    /**
     * Check whether lock file exists.
     * @return true if there is a lock file else false
     */
    private static boolean existsLock() {
        ProTrackUtils.checkOrCreateBaralgaDir();
        File lockFile = new File(Settings.getLockFileLocation());

        try {
            FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel(); //$NON-NLS-1$
            FileLock lock;

            lock = channel.tryLock();

            if (lock == null) {
                return true;
            }

            lock.release();
        } catch (FileNotFoundException e) {
            return true;
        } catch (IOException e) {
            return true;
        }
        return false;        
    }
}
