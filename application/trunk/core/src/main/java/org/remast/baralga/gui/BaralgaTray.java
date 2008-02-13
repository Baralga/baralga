package org.remast.baralga.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.remast.baralga.BaralgaMain;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.actions.ChangeProjectAction;
import org.remast.baralga.gui.actions.ExitAction;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.utils.AWTUtils;
import org.remast.baralga.gui.utils.Constants;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;

/**
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
    private TrayIcon trayIcon;

    /** The menu of the tray icon. */
    private PopupMenu menu = new PopupMenu();

    public BaralgaTray(final PresentationModel model, final MainFrame mainFrame) {
        this.model = model;
        this.model.addObserver(this);

        buildMenu();

        trayIcon = new TrayIcon(NORMAL_ICON, Messages.getString("Global.Title")); //$NON-NLS-1$
        trayIcon.setPopupMenu(menu);
        trayIcon.setImageAutoSize(true);

        trayIcon.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                mainFrame.setVisible(!mainFrame.isVisible());
                mainFrame.setState(JFrame.NORMAL);
                mainFrame.requestFocus();
                BaralgaMain.getTray().hide();
            }

        });

        if (model.isActive()) {
            trayIcon.setImage(ACTIVE_ICON);
            trayIcon.setToolTip(Messages.getString("Global.Title") + " - " + model.getSelectedProject() + " since " + Constants.hhMMFormat.format(model.getStart()));
        }

    }

    private void buildMenu() {
        menu.removeAll();
        menu.add(AWTUtils.createFromAction(new ExitAction(model)));

        // Add separator
        menu.add("-"); //$NON-NLS-1$

        for (Project project : model.getData().getProjects()) {
            ChangeProjectAction changeAction = new ChangeProjectAction(model, project);
            menu.add(AWTUtils.createFromAction(changeAction));
        }

        // Add separator
        menu.add("-"); //$NON-NLS-1$

        if(model.isActive()) {
            menu.add(AWTUtils.createFromAction(new StopAction(model)));
        } else {
            menu.add(AWTUtils.createFromAction(new StartAction(model)));
        }
    }

    /**
     * Show the tray icon.
     */
    public void show() {
        SystemTray tray = SystemTray.getSystemTray(); 
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            log.error(e, e);
        }
    }

    /**
     * Hide the tray icon.
     */
    public void hide() {
        SystemTray.getSystemTray().remove(trayIcon);        
    }

    public void update(Observable source, Object eventObject) {
        if (eventObject != null && eventObject instanceof ProTrackEvent) {
            ProTrackEvent event = (ProTrackEvent) eventObject;

            switch (event.getType()) {

                case ProTrackEvent.START:
                    this.updateStart();
                    break;

                case ProTrackEvent.STOP:
                    this.updateStop();
                    break;

                case ProTrackEvent.PROJECT_CHANGED:
                    this.updateProjectChanged();
                    this.buildMenu();
                    break;

                case ProTrackEvent.PROJECT_ADDED:
                    this.buildMenu();
                    break;

                case ProTrackEvent.PROJECT_REMOVED:
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
            trayIcon.setToolTip(Messages.getString("Global.Title") + " - " + model.getSelectedProject() + " since " + Constants.hhMMFormat.format(model.getStart()));
        }
    }

    /**
     * Executed on start event.
     */    
    private void updateStop() {
        this.trayIcon.setImage(NORMAL_ICON);
        trayIcon.setToolTip(Messages.getString("Global.Title") + " - idle since " + Constants.hhMMFormat.format(model.getStop()));
        this.buildMenu();
    }

    /**
     * Executed on start event.
     */    
    private void updateStart() {
        this.trayIcon.setImage(ACTIVE_ICON);
        trayIcon.setToolTip(Messages.getString("Global.Title") + " - " + model.getSelectedProject() + " since " + Constants.hhMMFormat.format(model.getStart())); //$NON-NLS-1$
        this.buildMenu();
    }

}
