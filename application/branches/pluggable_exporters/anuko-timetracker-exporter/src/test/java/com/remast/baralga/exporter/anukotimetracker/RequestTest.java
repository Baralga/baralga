package com.remast.baralga.exporter.anukotimetracker;

import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.joda.time.DateTime;
import org.junit.Test;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.util.DateUtils;

import com.remast.baralga.exporter.anukotimetracker.gui.ExportDialog;

/**
 * Test for the Anuko exporter
 */
public class RequestTest {

    @Test
    public void testApp() throws InterruptedException {
        ProTrack data = new ProTrack();
        Project project1 = new Project(1, "Project 1", "Description of project 1");
        Project project2 = new Project(2, "Project 2", "Description of project 2");
        data.add(project1);
        data.add(project2);
        
        DateTime now = new DateTime();
        ProjectActivity activity1 = new ProjectActivity( now.minusHours(1), now, project1);
        ProjectActivity activity2 = new ProjectActivity( now.minusHours(2), now, project2);
        data.addActivity(activity1);
        data.addActivity(activity2);
        
        DateTime yesterday = now.minusDays(1);
        ProjectActivity activity3 = new ProjectActivity( yesterday.minusHours(3), now, project2);
        data.addActivity(activity3);
        
        Filter filter = new Filter();
        filter.setWeekOfYear(DateUtils.getNowAsDateTime());
        filter.setYear(DateUtils.getNowAsDateTime());
        
        displayExportDialog( data, filter );
    }
    
    private void displayExportDialog(ProTrack data, Filter filter) throws InterruptedException {
        initLookAndFeel();

        
        JDialog exportDialog = new ExportDialog(null,
                "http://timetracker.wrconsulting.com/wginfo.php", data, filter);
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
