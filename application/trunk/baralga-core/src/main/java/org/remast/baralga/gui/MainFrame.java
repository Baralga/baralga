package org.remast.baralga.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXTitledSeparator;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.actions.AboutAction;
import org.remast.baralga.gui.actions.AbstractBaralgaAction;
import org.remast.baralga.gui.actions.AddActivityAction;
import org.remast.baralga.gui.actions.ExitAction;
import org.remast.baralga.gui.actions.ExportCsvAction;
import org.remast.baralga.gui.actions.ExportDataAction;
import org.remast.baralga.gui.actions.ExportExcelAction;
import org.remast.baralga.gui.actions.ImportDataAction;
import org.remast.baralga.gui.actions.ManageProjectsAction;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.ReportPanel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.swing.text.TextEditor;
import org.remast.swing.util.GuiConstants;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.swing.EventComboBoxModel;

/**
 * The main frame of the application.
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class MainFrame extends JXFrame implements Observer, WindowListener {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MainFrame.class);

    /** The standard icon image. */
    private static final Image NORMAL_ICON = new ImageIcon(BaralgaMain.class.getResource("/icons/Baralga-Tray.gif")).getImage(); //$NON-NLS-1$

    /** The icon image when an activity is running. */
    private static final Image ACTIVE_ICON = new ImageIcon(BaralgaMain.class.getResource("/icons/Baralga-Tray-Green.png")).getImage(); //$NON-NLS-1$

    /** The model. */
    private PresentationModel model;

    /** The tool bar. */
    private JToolBar toolBar = null;

    /**
     * The panel with details about the current activity. Like the current project and description.
     */
    private JPanel currentActivityPanel = null;

    /** Starts/stops the active project. */
    private JButton startStopButton = null;

    /** The list of projects. The selected project is the currently active project. */
    private JComboBox projectSelector = null;

    /** The description editor. */
    private TextEditor descriptionEditor;


    // ------------------------------------------------
    // Other stuff
    // ------------------------------------------------

    /** The filtered report. */
    private ReportPanel reportPanel;


    // ------------------------------------------------
    // The menus
    // ------------------------------------------------

    /** The menu bar containing all menus. */
    private JMenuBar mainMenuBar = null;

    /** The file menu. */
    private JMenu fileMenu = null;

    /** The help menu. */
    private JMenu helpMenu = null;

    /** The export menu. */
    private JMenu exportMenu = null;

    /** The import menu. */
    private JMenu importMenu = null;

    /** The edit menu. */
    private JMenu editMenu = null;


    // ------------------------------------------------
    // The menu items
    // ------------------------------------------------

    private JMenuItem aboutMenuItem = null;

    private JMenuItem addActivityMenuItem = null;

    private JMenuItem editProjectsMenuItem = null;

    private JMenuItem exportExcelItem = null;
    
    private JMenuItem exportCsvItem = null;

    private JMenuItem exportDataItem = null;

    private JMenuItem exitItem = null;

    private JMenuItem importItem = null;

    /**
     * This is the default constructor.
     * @param model the model
     */
    public MainFrame(final PresentationModel model) {
        super();

        this.model = model;
        this.model.addObserver(this);

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        this.setSize(530, 720);
        this.setIconImage(NORMAL_ICON);
        this.setResizable(true);
        this.setJMenuBar(getMainMenuBar());

        this.addWindowListener(this);

        // 1. Init start-/stop-Buttons
        if (this.model.isActive()) {
            this.setTitle(
                    textBundle.textFor("Global.Title") + " - " + this.model.getSelectedProject() + textBundle.textFor("MainFrame.9") + FormatUtils.timeFormat.format(this.model.getStart()) //$NON-NLS-1$ //$NON-NLS-2$
            );
            getStartStopButton().setAction(new StopAction(this.model));
        } else {
            this.setTitle(textBundle.textFor("Global.Title")); //$NON-NLS-1$

            getStartStopButton().setAction(new StartAction(this, this.model));
        }

        // 2. Restore selected project if set.
        if (this.model.getData().getActiveProject() != null) {
            this.getProjectSelector().setSelectedItem(
                    this.model.getData().getActiveProject()
            );
        } else {
            // If not set initially select first project
            if (!this.model.getProjectList().isEmpty()) {
                getProjectSelector().setSelectedItem(
                        this.model.getProjectList().get(0)
                );
            }
        }

        // 3. Set layout
        final double[][] size = { 
                {TableLayout.FILL }, // Columns
                {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL}
        }; // Rows

        TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);
        this.add(getToolBar(), "0, 0");
        this.add(getCurrentActivityPanel(), "0, 1");
        this.add(getReportPanel(), "0, 2");
    }

    private ReportPanel getReportPanel() {
        if (reportPanel == null) {
            reportPanel = new ReportPanel(this.model);
        }
        return reportPanel;
    }

    /**
     * This method initializes mainMenuBar.
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
     * This method initializes toolBar.
     * @return javax.swing.JToolBar
     */
    public JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.setFloatable(false);
        }
        
        toolBar.add(new ManageProjectsAction(this, this.model));
        toolBar.add(new ExportExcelAction(this, this.model));
        toolBar.add(new AddActivityAction(this, this.model));
        toolBar.add(new JToolBar.Separator());
        toolBar.add(this.model.getEditStack().getUndoAction());
        toolBar.add(this.model.getEditStack().getRedoAction());

        return toolBar;
    }

    /**
     * This method initializes currentPanel.
     * @return javax.swing.JPanel
     */
    private JPanel getCurrentActivityPanel() {
        if (currentActivityPanel == null) {
            double border = 5;
            double size[][] = {
                    { border, TableLayout.FILL, border, TableLayout.FILL, border }, // Columns
                    { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED,
                        border, TableLayout.PREFERRED, border } }; // Rows

            TableLayout tableLayout = new TableLayout(size);

            currentActivityPanel = new JPanel();
            currentActivityPanel.setLayout(tableLayout);
            currentActivityPanel.add(getStartStopButton(), "1, 1");
            currentActivityPanel.add(getProjectSelector(), "3, 1");

            descriptionEditor = new TextEditor(true);
            descriptionEditor.setBorder(BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY));
            descriptionEditor.setPreferredSize(new Dimension(200, 100));
            descriptionEditor.setCollapseEditToolbar(false);
            descriptionEditor.addTextObserver(new TextEditor.TextChangeObserver() {

                public void onTextChange() {
                    final String description = descriptionEditor.getText();

                    // Store in model
                    model.setDescription(description);

                    // Save description in settings.
                    UserSettings.instance().setLastDescription(description);
                }
            });

            descriptionEditor.setText(model.getDescription());
            descriptionEditor.setEditable(model.isActive());

            currentActivityPanel.add(new JXTitledSeparator(textBundle.textFor("MainFrame.DescriptionLabel")),
            "1, 5, 3, 5");
            currentActivityPanel.add(descriptionEditor, "1, 7, 3, 7");
        }
        return currentActivityPanel;
    }

    /**
     * This method initializes startStopButton.
     * @return javax.swing.JButton
     */
    private JButton getStartStopButton() {
        if (startStopButton == null) {
            startStopButton = new JButton(new StartAction(this, this.model));
        }
        return startStopButton;
    }

    /**
     * This method initializes projectSelector.
     * @return javax.swing.JComboBox
     */
    private JComboBox getProjectSelector() {
        if (projectSelector == null) {
            projectSelector = new JComboBox();
            projectSelector.setToolTipText(textBundle.textFor("ProjectSelector.ToolTipText"));

            projectSelector.setModel(new EventComboBoxModel<Project>(this.model.getProjectList()));

            /* Handling of selection events: */
            projectSelector.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // 1. Set current project to the just selected project.
                    final Project selectedProject = (Project) projectSelector.getSelectedItem();
                    MainFrame.this.model.changeProject(selectedProject);

                    // 2. Clear the description.
                    if (descriptionEditor != null) {
                        descriptionEditor.setText(StringUtils.EMPTY);
                    }
                }
            });
        }
        return projectSelector;
    }

    /**
     * This method initializes aboutMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu(textBundle.textFor("MainFrame.HelpMenu.Title"));
            helpMenu.setMnemonic(textBundle.textFor("MainFrame.HelpMenu.Title").charAt(0));
            helpMenu.add(getAboutMenuItem());
        }
        return helpMenu;
    }

    /**
     * This method initializes fileMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText(textBundle.textFor("MainFrame.FileMenu.Title")); //$NON-NLS-1$
            fileMenu.setMnemonic(textBundle.textFor("MainFrame.FileMenu.Title").charAt(0)); //$NON-NLS-1$

            fileMenu.add(getExportMenu());
            fileMenu.add(getImportMenu());
            fileMenu.addSeparator();

            fileMenu.add(getExitItem());
        }
        return fileMenu;
    }
    
    /**
     * This method initializes exitItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getImportItem() {
        if (importItem == null) {
            final AbstractBaralgaAction exitAction = new ImportDataAction(this, this.model);
            importItem = new JMenuItem(exitAction);
            importItem.setMnemonic(exitAction.getMnemonic());
        }
        return importItem;
    }

    /**
     * This method initializes editMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new JMenu();
            editMenu.setText(textBundle.textFor("MainFrame.EditMenu.Title")); //$NON-NLS-1$
            editMenu.setMnemonic(textBundle.textFor("MainFrame.EditMenu.Title").charAt(0)); //$NON-NLS-1$

            editMenu.add(this.model.getEditStack().getRedoAction());
            editMenu.add(this.model.getEditStack().getUndoAction());

            editMenu.addSeparator();

            editMenu.add(getEditProjectsMenuItem());
            editMenu.add(getAddActivityMenuItem());

//          INFO: Uncomment to enable settings menu.
//          editMenu.addSeparator();
//          editMenu.add(new JMenuItem(new SettingsAction(this, model)));
        }
        return editMenu;
    }

    /**
     * This method initializes addActivityMenuItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAddActivityMenuItem() {
        if (addActivityMenuItem == null) {
            AbstractBaralgaAction addActivityAction = new AddActivityAction(this, this.model);
            addActivityMenuItem = new JMenuItem(addActivityAction);
            addActivityMenuItem.setMnemonic(addActivityAction.getMnemonic());
        }
        return addActivityMenuItem;
    }

    /**
     * This method initializes editProjectsMenuItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getEditProjectsMenuItem() {
        if (editProjectsMenuItem == null) {
            AbstractBaralgaAction manageProjectsAction = new ManageProjectsAction(this, this.model);
            editProjectsMenuItem = new JMenuItem(manageProjectsAction);
            editProjectsMenuItem.setMnemonic(manageProjectsAction.getMnemonic());
        }
        return editProjectsMenuItem;
    }

    /**
     * This method initializes aboutMenuItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getAboutMenuItem() {
        if (aboutMenuItem == null) {
            AbstractBaralgaAction aboutAction = new AboutAction(this);
            aboutMenuItem = new JMenuItem(aboutAction);
            aboutMenuItem.setMnemonic(aboutAction.getMnemonic());
        }
        return aboutMenuItem;
    }


    /**
     * @param model the model to set
     */
    public void setModel(final PresentationModel model) {
        this.model = model;
    }

    public void update(final Observable source, final Object eventObject) {
        if (eventObject != null && eventObject instanceof BaralgaEvent) {
            final BaralgaEvent event = (BaralgaEvent) eventObject;

            switch (event.getType()) {

            case BaralgaEvent.PROJECT_ACTIVITY_STARTED:
                this.updateStart();
                break;

            case BaralgaEvent.PROJECT_ACTIVITY_STOPPED:
                this.updateStop();
                break;

            case BaralgaEvent.PROJECT_CHANGED:
                this.updateProjectChanged(event);
                break;

            case BaralgaEvent.PROJECT_ADDED:
                break;

            case BaralgaEvent.PROJECT_REMOVED:
                break;
            }
        }
    }

    /**
     * Executed on project changed event.
     */
    private void updateProjectChanged(final BaralgaEvent event) {
        if (this.model.isActive()) {
            this
            .setTitle(textBundle.textFor("Global.Title") + " - " + this.model.getSelectedProject() + textBundle.textFor("MainFrame.9") + FormatUtils.timeFormat.format(this.model.getStart())); //$NON-NLS-1$ //$NON-NLS-2$
        }
        getProjectSelector().setSelectedItem((Project) event.getData());
    }

    /**
     * Executed on start event.
     */
    private void updateStart() {
        setIconImage(ACTIVE_ICON);
        descriptionEditor.setText(StringUtils.EMPTY);
        descriptionEditor.setEditable(true);

        // Clear description in settings.
        UserSettings.instance().setLastDescription(StringUtils.EMPTY);

        this.setTitle(textBundle.textFor("Global.Title") + " - " + this.model.getSelectedProject() + textBundle.textFor("MainFrame.11") + FormatUtils.timeFormat.format(this.model.getStart())); //$NON-NLS-1$ //$NON-NLS-2$
        getStartStopButton().setAction(new StopAction(this.model));
    }

    /**
     * Executed on stop event.
     */
    private void updateStop() {
        setIconImage(NORMAL_ICON);
        descriptionEditor.setText(StringUtils.EMPTY);
        descriptionEditor.setEditable(false);

        // Clear description in settings.
        UserSettings.instance().setLastDescription(StringUtils.EMPTY);

        this.setTitle(textBundle.textFor("Global.Title") + " " + textBundle.textFor("MainFrame.12") + FormatUtils.timeFormat.format(this.model.getStop())); //$NON-NLS-1$
        getStartStopButton().setAction(new StartAction(this, this.model));
    }

    /**
     * This method initializes exportExcelItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExportExcelItem() {
        if (exportExcelItem == null) {
            final AbstractBaralgaAction excelExportAction = new ExportExcelAction(this, this.model);
            exportExcelItem = new JMenuItem(excelExportAction);
            exportExcelItem.setMnemonic(excelExportAction.getMnemonic());
        }
        return exportExcelItem;
    }
    
    /**
     * This method initializes exportCsvItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExportCsvItem() {
        if (exportCsvItem == null) {
            final AbstractBaralgaAction csvExportAction = new ExportCsvAction(this, this.model);
            exportCsvItem = new JMenuItem(csvExportAction);
            exportCsvItem.setMnemonic(csvExportAction.getMnemonic());
        }
        return exportCsvItem;
    }
    
    /**
     * This method initializes exportDataItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExportDataItem() {
        if (exportDataItem == null) {
            final AbstractBaralgaAction exportDataAction = new ExportDataAction(this, this.model);
            exportDataItem = new JMenuItem(exportDataAction);
            exportDataItem.setMnemonic(exportDataAction.getMnemonic());
        }
        return exportDataItem;
    }

    /**
     * This method initializes exportMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getExportMenu() {
        if (exportMenu == null) {
            exportMenu = new JMenu();
            exportMenu.setIcon(new ImageIcon(getClass().getResource("/icons/export-menu.png")));
            exportMenu.setText(textBundle.textFor("MainFrame.ExportMenu.Title")); //$NON-NLS-1$
            exportMenu.setMnemonic(textBundle.textFor("MainFrame.ExportMenu.Title").charAt(0)); //$NON-NLS-1$
            
            exportMenu.add(getExportExcelItem());
            exportMenu.add(getExportCsvItem());
            exportMenu.add(getExportDataItem());
        }
        return exportMenu;
    }
    
    /**
     * This method initializes importMenu.
     * @return javax.swing.JMenu
     */
    private JMenu getImportMenu() {
        if (importMenu == null) {
            importMenu = new JMenu();
            importMenu.setIcon(new ImageIcon(getClass().getResource("/icons/import-menu.png")));
            importMenu.setText(textBundle.textFor("MainFrame.ImportMenu.Title")); //$NON-NLS-1$
            importMenu.setMnemonic(textBundle.textFor("MainFrame.ImportMenu.Title").charAt(0)); //$NON-NLS-1$
            importMenu.add(getImportItem());
        }
        return importMenu;
    }

    public void windowIconified(final java.awt.event.WindowEvent e) {
        if (BaralgaMain.getTray() != null) {
            this.setVisible(false);
            BaralgaMain.getTray().show();
        }
    }

    public void windowOpened(final java.awt.event.WindowEvent e) {
    }

    public void windowClosing(final java.awt.event.WindowEvent e) {
        if (BaralgaMain.getTray() != null) {
            this.setVisible(false);
            BaralgaMain.getTray().show();
        } else {
            boolean quit = true;

            if (model.isActive()) {
                final int dialogResult = JOptionPane.showConfirmDialog(
                        getOwner(), 
                        textBundle.textFor("ExitConfirmDialog.Message"), 
                        textBundle.textFor("ExitConfirmDialog.Title"), 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );
                quit = JOptionPane.YES_OPTION == dialogResult;
            } 

            if (quit) {
                System.exit(0);
            }
        }
    }

    public void windowClosed(final java.awt.event.WindowEvent e) {
    }

    public void windowDeiconified(final java.awt.event.WindowEvent e) {
    }

    public void windowActivated(final java.awt.event.WindowEvent e) {
    }

    public void windowDeactivated(final java.awt.event.WindowEvent e) {
    }

    /**
     * This method initializes exitItem.
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExitItem() {
        if (exitItem == null) {
            final AbstractBaralgaAction exitAction = new ExitAction(this, this.model);
            exitItem = new JMenuItem(exitAction);
            exitItem.setMnemonic(exitAction.getMnemonic());
        }
        return exitItem;
    }

}
