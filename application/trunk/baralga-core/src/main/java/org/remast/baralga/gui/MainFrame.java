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
import org.remast.baralga.BaralgaMain;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.actions.AboutAction;
import org.remast.baralga.gui.actions.AbstractBaralgaAction;
import org.remast.baralga.gui.actions.AddActivityAction;
import org.remast.baralga.gui.actions.ExcelExportAction;
import org.remast.baralga.gui.actions.ExitAction;
import org.remast.baralga.gui.actions.ManageProjectsAction;
import org.remast.baralga.gui.actions.SaveAction;
import org.remast.baralga.gui.actions.StartAction;
import org.remast.baralga.gui.actions.StopAction;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.ReportPanel;
import org.remast.baralga.model.Project;
import org.remast.gui.text.TextEditor;
import org.remast.gui.util.Constants;

import ca.odell.glazedlists.swing.EventComboBoxModel;

@SuppressWarnings("serial")//$NON-NLS-1$
public class MainFrame extends JXFrame implements Observer, WindowListener {

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

    /** The edit menu. */
    private JMenu editMenu = null;
    

    // ------------------------------------------------
    // The menu items
    // ------------------------------------------------

    private JMenuItem aboutMenuItem = null;
    
    private JMenuItem addActivityMenuItem = null;

    private JMenuItem editProjectsMenuItem = null;

    private JMenuItem excelExportItem = null;

    private JMenuItem exitItem = null;

    private JMenuItem saveItem = null;

    /**
     * This is the default constructor
     * @param model
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
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/Baralga-Tray.gif"))); //$NON-NLS-1$
        this.setResizable(true);
        this.setJMenuBar(getMainMenuBar());

        this.addWindowListener(this);

        // 1. Init start-/stop-Buttons
        if (this.model.isActive()) {
            this
                    .setTitle(Messages.getString("Global.Title") + " - " + this.model.getSelectedProject() + Messages.getString("MainFrame.9") + Constants.hhMMFormat.format(this.model.getStart())); //$NON-NLS-1$ //$NON-NLS-2$
            getStartStopButton().setAction(new StopAction(this.model));
        } else {
            this.setTitle(Messages.getString("Global.Title")); //$NON-NLS-1$

            getStartStopButton().setAction(new StartAction(this.model));
        }

        // 2. Restore selected project if set.
        if (this.model.getData().getActiveProject() != null) {
            this.getProjectSelector().setSelectedItem(this.model.getData().getActiveProject());
        } else {
            // If not set initially select first project
            if (!this.model.getProjectList().isEmpty()) {
                getProjectSelector().setSelectedItem(this.model.getProjectList().get(0));
            }
        }

        // 3. Set layout
        double size[][] = { { TableLayout.FILL }, // Columns
                { TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.FILL } }; // Rows

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
        toolBar.add(new SaveAction(this, this.model));
        toolBar.add(new ManageProjectsAction(this, this.model));
        toolBar.add(new ExcelExportAction(this.model));
        toolBar.add(new AddActivityAction(this, this.model));
        toolBar.add(new JToolBar.Separator());
        toolBar.add(this.model.getEditStack().getUndoAction());
        toolBar.add(this.model.getEditStack().getRedoAction());

        return toolBar;
    }

    /**
     * This method initializes currentPanel
     * 
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
            descriptionEditor.setBorder(BorderFactory.createLineBorder(Constants.VERY_LIGHT_GREY));
            descriptionEditor.setPreferredSize(new Dimension(200, 100));
            descriptionEditor.setCollapseEditToolbar(false);
            descriptionEditor.addTextObserver(new TextEditor.TextChangeObserver() {

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

            currentActivityPanel.add(new JXTitledSeparator(Messages.getString("MainFrame.DescriptionLabel")),
                    "1, 5, 3, 5");
            currentActivityPanel.add(descriptionEditor, "1, 7, 3, 7");
        }
        return currentActivityPanel;
    }

    /**
     * This method initializes startStopButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStartStopButton() {
        if (startStopButton == null) {
            startStopButton = new JButton(new StartAction(this.model));
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
            projectSelector.setToolTipText(Messages.getString("ProjectSelector.ToolTipText"));

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
     * This method initializes aboutMenu
     * 
     * @return javax.swing.JMenu
     */
    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu(Messages.getString("MainFrame.HelpMenu.Title"));
            helpMenu.setMnemonic(Messages.getString("MainFrame.HelpMenu.Title").charAt(0));
            helpMenu.add(getAboutMenuItem());
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
            fileMenu.setMnemonic(Messages.getString("MainFrame.FileMenu.Title").charAt(0)); //$NON-NLS-1$
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
            editMenu.setText(Messages.getString("MainFrame.EditMenu.Title")); //$NON-NLS-1$
            editMenu.setMnemonic(Messages.getString("MainFrame.EditMenu.Title").charAt(0)); //$NON-NLS-1$

            editMenu.add(this.model.getEditStack().getRedoAction());
            editMenu.add(this.model.getEditStack().getUndoAction());
            
            editMenu.addSeparator();
            
            editMenu.add(getEditProjectsMenuItem());
            editMenu.add(getAddActivityMenuItem());
        }
        return editMenu;
    }

    /**
     * This method initializes addActivityMenuItem
     * 
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
     * This method initializes editProjectsMenuItem
     * 
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
     * This method initializes aboutMenuItem
     * 
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
        if (this.model.isActive()) {
            this
                    .setTitle(Messages.getString("Global.Title") + " - " + this.model.getSelectedProject() + Messages.getString("MainFrame.9") + Constants.hhMMFormat.format(this.model.getStart())); //$NON-NLS-1$ //$NON-NLS-2$
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

        this.setTitle(Messages.getString("Global.Title") + " - " + this.model.getSelectedProject() + Messages.getString("MainFrame.11") + Constants.hhMMFormat.format(this.model.getStart())); //$NON-NLS-1$ //$NON-NLS-2$
        getStartStopButton().setAction(new StopAction(this.model));
    }

    /**
     * Executed on stop event.
     */
    private void updateStop() {
        descriptionEditor.setText(StringUtils.EMPTY);
        descriptionEditor.setEditable(false);

        // Clear description in settings.
        Settings.instance().setLastDescription(StringUtils.EMPTY);

        this.setTitle(Messages.getString("Global.Title") + " " + Messages.getString("MainFrame.12") + " " + Constants.hhMMFormat.format(this.model.getStop())); //$NON-NLS-1$
        getStartStopButton().setAction(new StartAction(this.model));
    }

    /**
     * This method initializes ExcelExportItem
     * 
     * @return javax.swing.JMenuItem
     */
    private JMenuItem getExcelExportItem() {
        if (excelExportItem == null) {
            final AbstractBaralgaAction excelExportAction = new ExcelExportAction(this.model);
            excelExportItem = new JMenuItem(excelExportAction);
            excelExportItem.setMnemonic(excelExportAction.getMnemonic());
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
            exportMenu.setIcon(new ImageIcon(getClass().getResource("/icons/gnome-mime-text-x-credits.png")));
            exportMenu.setText(Messages.getString("MainFrame.ExportMenu.Title")); //$NON-NLS-1$
            exportMenu.setMnemonic(Messages.getString("MainFrame.ExportMenu.Title").charAt(0)); //$NON-NLS-1$
            exportMenu.add(getExcelExportItem());
        }
        return exportMenu;
    }

    public void windowIconified(java.awt.event.WindowEvent e) {
        if (BaralgaMain.getTray() != null) {
            this.setVisible(false);
            BaralgaMain.getTray().show();
        }
    }

    public void windowOpened(java.awt.event.WindowEvent e) {
    }

    public void windowClosing(java.awt.event.WindowEvent e) {
        if (BaralgaMain.getTray() != null) {
            this.setVisible(false);
            BaralgaMain.getTray().show();
        } else {
            boolean quit = true;

            if (model.isActive()) {
                final int dialogResult = JOptionPane.showConfirmDialog(
                        getOwner(), 
                        Messages.getString("ExitConfirmDialog.Message"), 
                        Messages.getString("ExitConfirmDialog.Title"), 
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
            final AbstractBaralgaAction exitAction = new ExitAction(this, this.model);
            exitItem = new JMenuItem(exitAction);
            exitItem.setMnemonic(exitAction.getMnemonic());
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
            final AbstractBaralgaAction saveAction = new SaveAction(this, this.model);
            saveItem = new JMenuItem(saveAction);
            saveItem.setMnemonic(saveAction.getMnemonic());            
        }
        return saveItem;
    }

}
