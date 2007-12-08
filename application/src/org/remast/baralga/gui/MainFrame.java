package org.remast.baralga.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTitledSeparator;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.actions.AboutAction;
import org.remast.baralga.gui.actions.AddActivityAction;
import org.remast.baralga.gui.actions.ExcelExportAction;
import org.remast.baralga.gui.actions.ExitAction;
import org.remast.baralga.gui.actions.ManageProjectsAction;
import org.remast.baralga.gui.actions.RedoAction;
import org.remast.baralga.gui.actions.SaveAction;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.actions.UndoAction;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.panels.ReportPanel;
import org.remast.baralga.gui.panels.TextEditor;
import org.remast.baralga.gui.utils.Constants;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;

import ca.odell.glazedlists.swing.EventComboBoxModel;

@SuppressWarnings("serial")//$NON-NLS-1$
public class MainFrame extends JXFrame implements Observer, WindowListener {

    /** The model. */
    private PresentationModel model;

    /** The tool bar. */
    private JToolBar toolBar = null;

    private JPanel currentPanel = null;

    private JButton startStopButton = null;

    /** The list of projects. */
    private JComboBox projectSelector = null;

    /** The description editor. */
    private TextEditor descriptionEditor;

    // ------------------------------------------------
    // Other stuff
    // ------------------------------------------------

    /** The filtered report. */
    private ReportPanel reportPanel;

    // ------------------------------------------------
    // The menu
    // ------------------------------------------------

    /** The menu bar containing all menus. */
    private JMenuBar mainMenuBar = null;

    /** The file menu. */
    private JMenu fileMenu = null;

    /** The help menu. */
    private JMenu helpMenu = null;

    /** The export menu. */
    private JMenu exportMenu = null;

    /** The edit menu. */
    private JMenu editMenu = null;

    // The menu items

    private JMenuItem editProjectsMenuItem = null;

    private JMenuItem excelExportItem = null;

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
     * Set up GUI components.
     */
    private void initialize() {
        this.setSize(530, 720);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/resource/icons/Baralga-Tray.gif"))); //$NON-NLS-1$
        this.setResizable(true);
        this.setJMenuBar(getMainMenuBar());

        this.addWindowListener(this);

        // 1. Init start-/stop-Buttons
        if (getModel().isActive()) {
            this
                    .setTitle(Messages.getString("Global.Title") + " - " + getModel().getSelectedProject() + Messages.getString("MainFrame.9") + Constants.hhMMFormat.format(getModel().getStart())); //$NON-NLS-1$ //$NON-NLS-2$
            getStartStopButton().setAction(new StopAction(getModel()));
        } else {
            this.setTitle(Messages.getString("Global.Title")); //$NON-NLS-1$

            getStartStopButton().setAction(new StartAction(getModel()));
        }

        // 2. Restore selected project if set.
        if (getModel().getData().getActiveProject() != null) {
            this.getProjectSelector().setSelectedItem(getModel().getData().getActiveProject());
        } else {
            // If not set initially select first project
            if (!getModel().getProjectList().isEmpty()) {
                getProjectSelector().setSelectedItem(getModel().getProjectList().get(0));
            }
        }

        // 3. Set layout
        double size[][] = { { TableLayout.FILL }, // Columns
                { TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL } }; // Rows

        TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);
        this.add(getToolBar(), "0, 0");
        this.add(getCurrentPanel(), "0, 1");
        this.add(getReportPanel(), "0, 2");
    }

    private ReportPanel getReportPanel() {
        if (reportPanel == null) {
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
        toolBar.add(new ManageProjectsAction(this, getModel()));
        toolBar.add(new ExcelExportAction(getModel()));
        toolBar.add(new AddActivityAction(this, getModel()));

        return toolBar;
    }

    /**
     * This method initializes currentPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCurrentPanel() {
        if (currentPanel == null) {
            double border = 5;
            double size[][] = {
                    { border, TableLayout.FILL, border, TableLayout.FILL, border }, // Columns
                    { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED,
                            border, TableLayout.PREFERRED, border } }; // Rows

            TableLayout tableLayout = new TableLayout(size);

            currentPanel = new JPanel();
            currentPanel.setLayout(tableLayout);
            currentPanel.add(getStartStopButton(), "1, 1");
            currentPanel.add(getProjectSelector(), "3, 1");

            descriptionEditor = new TextEditor(true);
            descriptionEditor.setBorder(BorderFactory.createLineBorder(Constants.VERY_LIGHT_GREY));
            descriptionEditor.setPreferredSize(new Dimension(200, 100));
            descriptionEditor.setCollapseEditToolbar(false);
            descriptionEditor.addTextObserver(new TextEditor.TextChangeObserver() {

                @Override
                public void onTextChange() {
                    final String description = descriptionEditor.getText();

                    // Store in model
                    model.setDescription(description);

                    // Save description in settings.
                    Settings.instance().setLastDescription(description);
                }
            });

            descriptionEditor.setText(model.getDescription());
            descriptionEditor.setEditable(model.isActive());

            currentPanel.add(new JXTitledSeparator(Messages.getString("MainFrame.DescriptionLabel")), "1, 5, 3, 5");
            currentPanel.add(descriptionEditor, "1, 7, 3, 7");
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

            projectSelector.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final Project selectedProject = (Project) projectSelector.getSelectedItem();
                    getModel().changeProject(selectedProject);

                    if (descriptionEditor != null) {
                        descriptionEditor.setText(StringUtils.EMPTY);
                    }
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
            helpMenu.add(new AboutAction(this));
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
            fileMenu.addSeparator();
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
            editMenu.add(getModel().getEditStack().getRedoAction());
            editMenu.add(getModel().getEditStack().getUndoAction());
            editMenu.addSeparator();
            editMenu.setText(Messages.getString("MainFrame.EditMenu.Title")); //$NON-NLS-1$
            editMenu.add(getEditProjectsMenuItem());
            editMenu.add(new AddActivityAction(this, getModel()));
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
            editProjectsMenuItem.setAction(new ManageProjectsAction(this, getModel()));
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
        if (eventObject != null && eventObject instanceof ProTrackEvent) {
            final ProTrackEvent event = (ProTrackEvent) eventObject;

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
    private void updateProjectChanged(final ProTrackEvent event) {
        if (getModel().isActive()) {
            this
                    .setTitle(Messages.getString("Global.Title") + " - " + getModel().getSelectedProject() + Messages.getString("MainFrame.9") + Constants.hhMMFormat.format(getModel().getStart())); //$NON-NLS-1$ //$NON-NLS-2$
        }
        getProjectSelector().setSelectedItem((Project) event.getData());
    }

    /**
     * Executed on start event.
     */
    private void updateStart() {
        descriptionEditor.setText(StringUtils.EMPTY);
        descriptionEditor.setEditable(true);

        // Clear description in settings.
        Settings.instance().setLastDescription(StringUtils.EMPTY);

        this
                .setTitle(Messages.getString("Global.Title") + " - " + getModel().getSelectedProject() + Messages.getString("MainFrame.11") + Constants.hhMMFormat.format(getModel().getStart())); //$NON-NLS-1$ //$NON-NLS-2$
        getStartStopButton().setAction(new StopAction(getModel()));
    }

    /**
     * Executed on stop event.
     */
    private void updateStop() {
        descriptionEditor.setText(StringUtils.EMPTY);
        descriptionEditor.setEditable(false);

        // Clear description in settings.
        Settings.instance().setLastDescription(StringUtils.EMPTY);

        this
                .setTitle(Messages.getString("Global.Title") + " " + Messages.getString("MainFrame.12") + " " + Constants.hhMMFormat.format(getModel().getStop())); //$NON-NLS-1$
        getStartStopButton().setAction(new StartAction(getModel()));
    }

    /**
     * This method initializes ExcelExportItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExcelExportItem() {
        if (excelExportItem == null) {
            excelExportItem = new JMenuItem();
            excelExportItem.setAction(new ExcelExportAction(getModel()));
        }
        return excelExportItem;
    }

    /**
     * This method initializes ExportMenu
     * 
     * @return javax.swing.JMenu
     */
    private JMenu getExportMenu() {
        if (exportMenu == null) {
            exportMenu = new JMenu();
            exportMenu.setText(Messages.getString("MainFrame.ExportMenu.Title")); //$NON-NLS-1$
            exportMenu.add(getExcelExportItem());
        }
        return exportMenu;
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
