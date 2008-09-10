package org.remast.baralga.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swinghelper.tray.JXTrayIcon;
import org.remast.baralga.BaralgaMain;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.actions.ChangeProjectAction;
import org.remast.baralga.gui.actions.ExitAction;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.swing.util.GuiConstants;

/**
 * Tray icon for quick start, stop and switching of project activities.
 * @author remast
 */
public class BaralgaTray implements Observer {

    /** The logger. */
    private static final Log log = LogFactory.getLog(BaralgaTray.class);

    /** The standard icon image. */
    private static final Image NORMAL_ICON = new ImageIcon(BaralgaMain.class.getResource("/icons/Baralga-Tray.gif")).getImage();

    /** The icon image when an activity is running. */
    private static final Image ACTIVE_ICON = new ImageIcon(BaralgaMain.class.getResource("/icons/Baralga-Tray-Green.png")).getImage();

    /** The model. */
    private PresentationModel model;

    /** The tray icon. */
    private JXTrayIcon trayIcon;

    /** The menu of the tray icon. */
    private JPopupMenu menu = new JPopupMenu();

    public BaralgaTray(final PresentationModel model, final MainFrame mainFrame) {
        this.model = model;
        this.model.addObserver(this);

        buildMenu();

        trayIcon = new JXTrayIcon(NORMAL_ICON); //$NON-NLS-1$
        trayIcon.setToolTip(Messages.getString("Global.Title"));
        trayIcon.setJPopupMenu(menu);
        trayIcon.setImageAutoSize(true);

        trayIcon.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                mainFrame.setVisible(!mainFrame.isVisible());
                mainFrame.setState(JFrame.NORMAL);
                mainFrame.requestFocus();
                
                if (BaralgaMain.getTray() != null) {                    
                    BaralgaMain.getTray().hide();
                }
            }

        });

        if (model.isActive()) {
            trayIcon.setImage(ACTIVE_ICON);
            trayIcon.setToolTip(Messages.getString("Global.Title") + " - " + model.getSelectedProject() + Messages.getString("MainFrame.9") + GuiConstants.timeFormat.format(model.getStart()));
        }

    }

    /**
     * Build the context menu of the tray icon.
     */
    private void buildMenu() {
        menu.removeAll();
        final ExitAction exitAction = new ExitAction(null, model);
        exitAction.putValue(AbstractAction.SMALL_ICON, null);
        menu.add(exitAction);

        // Add separator
        menu.add(new JSeparator());

        for (Project project : model.getData().getProjects()) {
        	final ChangeProjectAction changeAction = new ChangeProjectAction(model, project);
            menu.add(changeAction);
        }

        // Add separator
        menu.add(new JSeparator());

        if (model.isActive()) {
        	final StopAction stopAction = new StopAction(model);
        	stopAction.putValue(AbstractAction.SMALL_ICON, null);
            menu.add(stopAction);
        } else {
        	final StartAction startAction = new StartAction(model);
        	startAction.putValue(AbstractAction.SMALL_ICON, null);
            menu.add(startAction);
        }
    }

    /**
     * Show the tray icon.
     */
    public void show() {
    	if (SystemTray.isSupported()) {
    		SystemTray tray = SystemTray.getSystemTray(); 
    		try {
    			tray.add(trayIcon);
    		} catch (AWTException e) {
    			log.error(e, e);
    		}
    	}
    }

    /**
     * Hide the tray icon.
     */
    public void hide() {
    	if (SystemTray.isSupported()) {
    		SystemTray.getSystemTray().remove(trayIcon);        
    	}
    }

    public void update(Observable source, Object eventObject) {
        if (eventObject != null && eventObject instanceof BaralgaEvent) {
            BaralgaEvent event = (BaralgaEvent) eventObject;

            switch (event.getType()) {

                case BaralgaEvent.PROJECT_ACTIVITY_STARTED:
                    this.updateStart();
                    break;

                case BaralgaEvent.PROJECT_ACTIVITY_STOPPED:
                    this.updateStop();
                    break;

                case BaralgaEvent.PROJECT_CHANGED:
                    this.updateProjectChanged();
                    this.buildMenu();
                    break;

                case BaralgaEvent.PROJECT_ADDED:
                    this.buildMenu();
                    break;

                case BaralgaEvent.PROJECT_REMOVED:
                    this.buildMenu();
                    break;
            }
        }
    }

    /**
     * Executed on project changed event.
     */    
    private void updateProjectChanged() {
        if (model.isActive()) {
            trayIcon.setToolTip(Messages.getString("Global.Title") + " - " + model.getSelectedProject() + Messages.getString("MainFrame.9") + GuiConstants.timeFormat.format(model.getStart()));
        }
    }

    /**
     * Executed on start event.
     */    
    private void updateStop() {
        this.trayIcon.setImage(NORMAL_ICON);
        trayIcon.setToolTip(Messages.getString("Global.Title") + " - "+ Messages.getString("MainFrame.12") + GuiConstants.timeFormat.format(model.getStop()));
        this.buildMenu();
    }

    /**
     * Executed on start event.
     */    
    private void updateStart() {
        this.trayIcon.setImage(ACTIVE_ICON);
        trayIcon.setToolTip(Messages.getString("Global.Title") + " - " + model.getSelectedProject() + Messages.getString("MainFrame.9") + GuiConstants.timeFormat.format(model.getStart())); //$NON-NLS-1$
        this.buildMenu();
    }

}
