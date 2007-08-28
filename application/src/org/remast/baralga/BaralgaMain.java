package org.remast.baralga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

import org.jargp.ArgumentProcessor;
import org.jargp.BoolDef;
import org.jargp.ParameterDef;
import org.jargp.StringTracker;
import org.remast.baralga.gui.MainFrame;
import org.remast.baralga.gui.BaralgaTray;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.ProjectStateException;
import org.remast.baralga.model.io.ProTrackReader;
import org.remast.baralga.model.utils.ProTrackUtils;

public class BaralgaMain {
    
    //------------------------------------------------
    // Command line options
    //------------------------------------------------
    
    /** Command line parameter definitions. */
    private static final ParameterDef[] BASE_PARAMETERS = { 
        new BoolDef('m', "minimized", "Start ProTrack in minimized mode.") //$NON-NLS-1$ //$NON-NLS-2$
    };

    /** Property for command line option minimized (-m). */
    private boolean minimized;

    
    //------------------------------------------------
    // User settings
    //------------------------------------------------

    /** User preferences of ProTrack. */
    public static final Preferences USER_PREFERENCES = Preferences.userNodeForPackage(BaralgaMain.class);
    
    /** The Tray icon. */
    private static BaralgaTray tray;

    private static FileLock lock;

    public static void main(String[] args) {
        // parse the command line arguments
        ArgumentProcessor proc = new ArgumentProcessor(BASE_PARAMETERS);
        BaralgaMain inst = new BaralgaMain();
        if (args.length > 0) {
            proc.processArgs(args, inst);
            StringTracker xargs = proc.getArgs();
            while (xargs.hasNext()) {
                System.out.println("extra argument: " + xargs.next()); //$NON-NLS-1$
            }
        } else {
            // print usage information if problem with parameters
            System.out.println("Usage: ProTrack [-options] extra\n" + "Options are:"); //$NON-NLS-1$ //$NON-NLS-2$
            proc.listParameters(80, System.out);
        }

        if (existsLock()) {
            System.out.println(Messages.getString("ProTrackMain.ErrorAlreadyRunning")); //$NON-NLS-1$
            return;
        } else {
            createLock();
        }

        // Initialize with new site
        final PresentationModel model = new PresentationModel();

        model.setData(new ProTrack());
        File file = null;
        String proTrackFileLocation = Settings.getProTrackFileLocation();
        file = new File(proTrackFileLocation);

        // Check for saved data
        if (file != null && file.exists()) {
            ProTrackReader reader;
            try {
                reader = new ProTrackReader(file);
                reader.read();
                model.setData(reader.getProTrack());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Set look & feel
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel"); //$NON-NLS-1$
//             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // ignore
        }

        final MainFrame mainFrame = new MainFrame(model);
        if (!inst.minimized) {
            mainFrame.setVisible(true);
        }

        tray = new BaralgaTray(model, mainFrame);
        tray.show();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {

                if (model.isActive()) {
                    try {
                        model.stop();
                    } catch (ProjectStateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    model.save();
                    releaseLock();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    releaseLock();
                }
            }

        });
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
            throw new RuntimeException(Messages.getString("ProTrackMain.8")); //$NON-NLS-1$
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
            }
        }
        lockFile.delete();
    }

    /**
     * Check whether lock file exists.
     * 
     * @return true if there is a lock file else false
     */
    private static boolean existsLock() {
        ProTrackUtils.checkOrCreateProTrackDir();
        File lockFile = new File(Settings.getLockFileLocation());
        
        try {
            FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel(); //$NON-NLS-1$
            FileLock lock;// = channel.lock();
            
            lock = channel.tryLock();
            
            if(lock == null)
                return true;
            
            lock.release();
        } catch (FileNotFoundException e) {
            return true;
        } catch (IOException e) {
            return true;
        }
        return false;        
    }
}
