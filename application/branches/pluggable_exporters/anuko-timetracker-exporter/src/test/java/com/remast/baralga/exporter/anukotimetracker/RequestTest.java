package com.remast.baralga.exporter.anukotimetracker;

import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.junit.Test;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;

import com.remast.baralga.exporter.anukotimetracker.gui.ExportDialog;

/**
 * Test for the Anuko exporter
 */
public class RequestTest {

    @Test
    public void testApp() throws InterruptedException {
        displayExportDialog();
    }
    
    private void displayExportDialog() throws InterruptedException {
        initLookAndFeel();
        ProTrack data = new ProTrack();
        data.add(new Project(1, "Project 1", "Description of project 1"));
        data.add(new Project(2, "Project 2", "Description of project 2"));
        
        JDialog exportDialog = new ExportDialog(null,
                "http://timetracker.wrconsulting.com/wginfo.php", data, null);
        exportDialog.setLocationByPlatform(true);
        exportDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        exportDialog.pack();
        exportDialog.setVisible(true);
        
//        final Object lock = new Object();
        
//        exportDialog.addWindowListener( new WindowAdapter() {
//            @Override
//            public void windowClosed(WindowEvent e) {
//                synchronized (lock) {
//                    lock.notify();
//                }
//            }
//        });
//        
//        // wait for dialog to close
//        synchronized (lock) {
//            lock.wait();
//        }
    }

    private static void initLookAndFeel() {
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
                System.err.println(ex);
            }
        }
    }
}
