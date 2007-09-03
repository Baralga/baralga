package org.remast.baralga.gui;

import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.remast.baralga.Messages;
import org.remast.baralga.BaralgaMain;
import org.remast.baralga.gui.actions.ChangeProjectAction;
import org.remast.baralga.gui.actions.ExitAction;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.utils.AWTUtils;
import org.remast.baralga.gui.utils.Constants;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;

public class BaralgaTray implements Observer {
    
    private TrayIcon trayIcon;

    private PresentationModel model;

    private PopupMenu menu;

    private boolean firstBuild = true;

    public BaralgaTray(final PresentationModel model, final MainFrame mainFrame) {
        this.model = model;
        this.model.addObserver(this);
        
        buildMenu();

        final ImageIcon i = new ImageIcon(BaralgaMain.class.getResource("/resource/icons/Baralga-Tray.gif")); //$NON-NLS-1$

        trayIcon = new TrayIcon(i.getImage(), Messages.getString("Global.Title")); //$NON-NLS-1$
        trayIcon.setPopupMenu(getMenu());
        trayIcon.setImageAutoSize(true);
        
        final MainFrame mf = mainFrame;
        trayIcon.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                mf.setVisible(!mf.isVisible());
                mf.setState(JFrame.NORMAL);
                mf.requestFocus();
            }
            
        });


        if (getModel().isActive()) {
            trayIcon.setToolTip(Messages.getString("Global.Title") + " - " + getModel().getSelectedProject() + " since " + Constants.hhMMFormat.format(getModel().getStart()));
        }

    }
        
    private void buildMenu() {
        if(firstBuild) {
            menu = new PopupMenu();
            firstBuild = false;
        } else {
            menu.removeAll();
        }
        
        menu.add(AWTUtils.createFromAction(new ExitAction(getModel())));
        
        // Add separator
        menu.add("-"); //$NON-NLS-1$
        
        for (Project project : model.getData().getProjects()) {
            ChangeProjectAction changeAction = new ChangeProjectAction(getModel(), project);
            menu.add(AWTUtils.createFromAction(changeAction));
        }
        
        // Add separator
        menu.add("-"); //$NON-NLS-1$
        
        if(getModel().isActive()) {
            menu.add(AWTUtils.createFromAction(new StopAction(getModel())));
        } else {
            menu.add(AWTUtils.createFromAction(new StartAction(getModel())));
        }
    }

    public void show() {
        SystemTray tray = SystemTray.getSystemTray(); 
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }        
    }

    public void hide() {
        SystemTray tray = SystemTray.getSystemTray(); 
        tray.remove(trayIcon);        
    }

    public void update(Observable source, Object eventObject) {
        if (eventObject instanceof ProTrackEvent) {
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
        if (getModel().isActive()) {
            trayIcon.setToolTip(Messages.getString("Global.Title") + " - " + getModel().getSelectedProject() + " since " + Constants.hhMMFormat.format(getModel().getStart()));
        }
    }

    /**
     * Executed on start event.
     */    
    private void updateStop() {
        trayIcon.setToolTip(Messages.getString("Global.Title") + " - idle since " + Constants.hhMMFormat.format(getModel().getStop()));
        this.buildMenu();
    }

    /**
     * Executed on start event.
     */    
    private void updateStart() {
        trayIcon.setToolTip(Messages.getString("Global.Title") + " - " + getModel().getSelectedProject() + " since " + Constants.hhMMFormat.format(getModel().getStart())); //$NON-NLS-1$
        this.buildMenu();
    }

    /**
     * @return the menu
     */
    private PopupMenu getMenu() {
        return menu;
    }

    /**
     * @return the model
     */
    private PresentationModel getModel() {
        return model;
    }
}
