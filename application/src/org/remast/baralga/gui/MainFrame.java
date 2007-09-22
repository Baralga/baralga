package org.remast.baralga.gui;

import info.clearthought.layout.TableLayout;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.actions.AboutAction;
import org.remast.baralga.gui.actions.AddActivityAction;
import org.remast.baralga.gui.actions.ExcelExportAction;
import org.remast.baralga.gui.actions.ExitAction;
import org.remast.baralga.gui.actions.ManageProjectsAction;
import org.remast.baralga.gui.actions.SaveAction;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.panels.ReportPanel;
import org.remast.baralga.gui.utils.Constants;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;

import ca.odell.glazedlists.swing.EventComboBoxModel;

@SuppressWarnings("serial") //$NON-NLS-1$
public class MainFrame extends JXFrame implements Observer, WindowListener {

    /** The model. */
    private PresentationModel model;

    private JPanel jContentPane = null;

    /** The tool bar. */
    private JToolBar toolBar = null;

    private JPanel currentPanel = null;

    private JButton startStopButton = null;

    private JComboBox projectSelector = null;

    //------------------------------------------------
    // Other stuff
    //------------------------------------------------

    /** The filtered report. */
    private ReportPanel reportPanel;
    
    //------------------------------------------------
    // The menus
    //------------------------------------------------

    private JMenuBar mainMenuBar = null;

    private JMenu fileMenu = null;
    
    private JMenu helpMenu = null;    

    private JMenu editMenu = null;

    private JMenuItem editProjectsMenuItem = null;

    private JMenuItem ExcelExportItem = null;

    private JMenu ExportMenu = null;

    private JMenuItem exitItem = null;

    private JMenuItem saveItem = null;

    /**
     * This is the default constructor
     * 
     * @param model
     */
    public MainFrame(PresentationModel model) {
        super();

        setModel(model);
        getModel().addObserver(this);

        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(510, 600);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/resource/icons/Baralga-Tray.gif"))); //$NON-NLS-1$
        this.setResizable(true);
        this.setJMenuBar(getMainMenuBar());
        this.setContentPane(getJContentPane());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.addWindowListener(this);

        if (getModel().isActive()) {
            this.setTitle(Messages.getString("Global.Title") + " - " + getModel().getSelectedProject() + Messages.getString("MainFrame.9") + Constants.hhMMFormat.format(getModel().getStart())); //$NON-NLS-1$ //$NON-NLS-2$
            getProjectSelector().setSelectedItem(getModel().getSelectedProject());
            
            startStopButton.setAction(new StopAction(getModel()));
        } else {
            this.setTitle(Messages.getString("Global.Title")); //$NON-NLS-1$
            
            if(!getModel().getProjectList().isEmpty()) {
                getProjectSelector().setSelectedItem(getModel().getProjectList().get(0));
            }
            startStopButton.setAction(new StartAction(getModel()));
        }
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
          jContentPane = new JXPanel();
          double size[][] =
          {{TableLayout.FILL},  // Columns
           {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL}}; // Rows
          TableLayout tableLayout = new TableLayout(size);
          jContentPane.setLayout(tableLayout);
          jContentPane.add(getToolBar(), "0, 0");
          jContentPane.add(getCurrentPanel(), "0, 1");
          jContentPane.add(getReportPanel(), "0, 2");
            
        }
        return jContentPane;
    }

    private ReportPanel getReportPanel() {
        if(reportPanel == null) {
            reportPanel = new ReportPanel(getModel());
        }
        return reportPanel;
    }

    /**
     * This method initializes mainMenuBar
     * 
     * @return javax.swing.JMenuBar
     */
    private JMenuBar getMainMenuBar() {
        if (mainMenuBar == null) {
            mainMenuBar = new JMenuBar();
            mainMenuBar.add(getFileMenu());
            mainMenuBar.add(getEditMenu());
            mainMenuBar.add(getHelpMenu());
        }
        return mainMenuBar;
    }

    /**
     * This method initializes toolBar
     * 
     * @return javax.swing.JToolBar
     */
    public JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.setFloatable(false);
        }
        toolBar.add(new SaveAction(getModel()));
        toolBar.add(new ManageProjectsAction(getModel()));
        toolBar.add(new ExcelExportAction(getModel()));
        toolBar.add(new AddActivityAction(getModel()));

        return toolBar;
    }

    /**
     * This method initializes currentPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCurrentPanel() {
        if (currentPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setVgap(6);
            gridLayout.setColumns(2);
            gridLayout.setHgap(6);
            currentPanel = new JPanel();
            currentPanel.setLayout(gridLayout);
            currentPanel.add(getStartStopButton(), null);
            currentPanel.add(getProjectSelector(), null);
        }
        return currentPanel;
    }

    /**
     * This method initializes startStopButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartStopButton() {
        if (startStopButton == null) {
            startStopButton = new JButton(new StartAction(getModel()));
        }
        return startStopButton;
    }

    /**
     * This method initializes projectSelector
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProjectSelector() {
        if (projectSelector == null) {
            projectSelector = new JComboBox();

            projectSelector.setModel(new EventComboBoxModel<Project>(getModel().getProjectList()));

            // Select first entry
            if(!getModel().getProjectList().isEmpty()) {
                Project project = getModel().getProjectList().get(0);
                getModel().changeProject(project);
            }

            projectSelector.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Project selectedProject = (Project) getProjectSelector().getSelectedItem();
                    getModel().changeProject(selectedProject);
                }
            });
        }
        return projectSelector;
    }
    /**
     * This method initializes aboutMenu
     * 
     * @return javax.swing.JMenu
     */
    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu(Messages.getString("MainFrame.HelpMenu.Title"));
            helpMenu.add(new AboutAction());
        }
        return helpMenu;
    }
    
    /**
     * This method initializes fileMenu
     * 
     * @return javax.swing.JMenu
     */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText(Messages.getString("MainFrame.FileMenu.Title")); //$NON-NLS-1$
            fileMenu.add(getExportMenu());
            fileMenu.add(getExitItem());
            fileMenu.add(getSaveItem());
        }
        return fileMenu;
    }

    /**
     * This method initializes editMenu
     * 
     * @return javax.swing.JMenu
     */
    private JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new JMenu();
            editMenu.setText(Messages.getString("MainFrame.EditMenu.Title")); //$NON-NLS-1$
            editMenu.add(getEditProjectsMenuItem());
            editMenu.add(new AddActivityAction(getModel()));
        }
        return editMenu;
    }

    /**
     * This method initializes editProjectsMenuItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getEditProjectsMenuItem() {
        if (editProjectsMenuItem == null) {
            editProjectsMenuItem = new JMenuItem();
            editProjectsMenuItem.setAction(new ManageProjectsAction(getModel()));
        }
        return editProjectsMenuItem;
    }

    /**
     * @return the model
     */
    public PresentationModel getModel() {
        return model;
    }

    /**
     * @param model
     *            the model to set
     */
    public void setModel(PresentationModel model) {
        this.model = model;
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
                this.updateProjectChanged(event);
                break;

            case ProTrackEvent.PROJECT_ADDED:
                break;

            case ProTrackEvent.PROJECT_REMOVED:
                break;
            }
        }
    }

    /**
     * Executed on project changed event.
     */    
    private void updateProjectChanged(ProTrackEvent event) {
        if (getModel().isActive()) {
            this.setTitle(Messages.getString("Global.Title") + " - " + getModel().getSelectedProject() + Messages.getString("MainFrame.9") + Constants.hhMMFormat.format(getModel().getStart())); //$NON-NLS-1$ //$NON-NLS-2$
        }
        getProjectSelector().setSelectedItem((Project) event.getData());
    }

    /**
     * Executed on start event.
     */    
    private void updateStart() {
        this.setTitle(Messages.getString("Global.Title") + " - " + getModel().getSelectedProject() + Messages.getString("MainFrame.11") + Constants.hhMMFormat.format(getModel().getStart())); //$NON-NLS-1$ //$NON-NLS-2$
        startStopButton.setAction(new StopAction(getModel()));
    }

    /**
     * Executed on stop event.
     */
    private void updateStop() {
        this.setTitle(Messages.getString("Global.Title") + " " + Messages.getString("MainFrame.12") + " " + Constants.hhMMFormat.format(getModel().getStop())); //$NON-NLS-1$
        startStopButton.setAction(new StartAction(getModel()));
    }

    /**
     * This method initializes ExcelExportItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExcelExportItem() {
        if (ExcelExportItem == null) {
            ExcelExportItem = new JMenuItem();
            ExcelExportItem.setAction(new ExcelExportAction(getModel()));
            ExcelExportItem.setText(Messages.getString("FileFilters.MicrosoftExcelFile")); //$NON-NLS-1$
        }
        return ExcelExportItem;
    }

    /**
     * This method initializes ExportMenu
     * 
     * @return javax.swing.JMenu
     */
    private JMenu getExportMenu() {
        if (ExportMenu == null) {
            ExportMenu = new JMenu();
            ExportMenu.setText(Messages.getString("MainFrame.ExportMenu.Title")); //$NON-NLS-1$
            ExportMenu.add(getExcelExportItem());
        }
        return ExportMenu;
    }

    public void windowIconified(java.awt.event.WindowEvent e) {
        this.setVisible(false);
    }

    public void windowOpened(java.awt.event.WindowEvent e) {
    }

    public void windowClosing(java.awt.event.WindowEvent e) {
    }

    public void windowClosed(java.awt.event.WindowEvent e) {
    }

    public void windowDeiconified(java.awt.event.WindowEvent e) {
    }

    public void windowActivated(java.awt.event.WindowEvent e) {
    }

    public void windowDeactivated(java.awt.event.WindowEvent e) {
    }

    /**
     * This method initializes exitItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExitItem() {
        if (exitItem == null) {
            exitItem = new JMenuItem(new ExitAction(getModel()));
        }
        return exitItem;
    }

    /**
     * This method initializes saveItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getSaveItem() {
        if (saveItem == null) {
            saveItem = new JMenuItem(new SaveAction(getModel()));
        }
        return saveItem;
    }

}
