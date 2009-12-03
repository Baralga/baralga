package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang.math.RandomUtils;
import org.jdesktop.swingx.JXTable;
import org.remast.baralga.gui.dialogs.table.ProjectListTableFormat;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.swing.dialog.EscapeDialog;
import org.remast.swing.table.BooleanCellRenderer;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.swing.EventTableModel;

import com.jidesoft.swing.JideScrollPane;

/**
 * The dialog to manage the available projects.
 * @author remast
 */
@SuppressWarnings("serial")
public class ManageProjectsDialog extends EscapeDialog implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ManageProjectsDialog.class);

    private JXTable projectTable = null;

    private JTextField newProjectTextField = null;

    private JButton addProjectButton = null;

    private JButton removeProjectButton = null;

    private JLabel lableProjectTitle = null;

    private final PresentationModel model;

    private EventTableModel<Project> projectTableModel;

    /**
     * @param owner
     */
    public ManageProjectsDialog(final Frame owner, final PresentationModel model) {
        super(owner);

        this.model = model;
        this.model.addObserver(this);

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());
        this.setSize(300, 200);
        this.setIconImage(new ImageIcon(getClass().getResource("/icons/gtk-edit.png")).getImage()); //$NON-NLS-1$

        this.setModal(true);
        this.setPreferredSize(new Dimension(350, 120));
        this.setTitle(textBundle.textFor("ManageProjectsDialog.Title")); //$NON-NLS-1$
//        this.setContentPane(getJContentPane());
        
        final double border = 5;
        final double[][] size = { 
                {border, TableLayout.PREFERRED, border, border, TableLayout.FILL, border, TableLayout.PREFERRED, border }, // Columns
                {border, TableLayout.PREFERRED, border * 2, TableLayout.FILL, border, TableLayout.FILL, border} // Rows
        };

        final TableLayout tableLayout = new TableLayout(size);
        this.setLayout(tableLayout);
        
        lableProjectTitle = new JLabel();
        lableProjectTitle.setText(textBundle.textFor("ManageProjectsDialog.ProjectSelector.Title")); //$NON-NLS-1$
        lableProjectTitle.setBackground(Color.lightGray);
        
        this.add(lableProjectTitle, "1, 1");
        this.add(getNewProjectTextField(), "3, 1, 6, 1");
        
        this.add(new JideScrollPane(getProjectTable()), "1, 3, 4, 5");

        this.add(getAddProjectButton(), "6, 3");
        this.add(getRemoveProjectButton(), "6, 5");

        // Set default Button to AddProjectsButton.
        this.getRootPane().setDefaultButton(addProjectButton);
    }

    /**
     * This method initializes projectList.
     * @return javax.swing.JList	
     */
    private JXTable getProjectTable() {
        if (projectTable == null) {
            projectTable = new JXTable();

            // Fix sorting
            // :INFO: This corrupts the initial sorting. Would be nice though...
            // EventListJXTableSorting.install(projectList, model.getAllProjectsList());
            projectTable.setSortable(false);
            
            projectTableModel = new EventTableModel<Project>(model.getAllProjectsList(), new ProjectListTableFormat(model));
            projectTable.setModel(projectTableModel);
            projectTable.setToolTipText(textBundle.textFor("ManageProjectsDialog.ProjectList.ToolTipText")); //$NON-NLS-1$
            
            projectTable.getColumn(1).setCellRenderer(new BooleanCellRenderer());
            projectTable.getColumn(1).setCellEditor(new JXTable.BooleanEditor());
        }
        return projectTable;
    }

    /**
     * This method initializes newProjectTextField.
     * @return javax.swing.JTextField	
     */
    private JTextField getNewProjectTextField() {
        if (newProjectTextField == null) {
            newProjectTextField = new JTextField();
            newProjectTextField.setName(textBundle.textFor("ManageProjectsDialog.4")); //$NON-NLS-1$
            newProjectTextField.setText(textBundle.textFor("ManageProjectsDialog.NewProjectTitle.DefaultNewProjectName")); //$NON-NLS-1$
            newProjectTextField.setToolTipText(textBundle.textFor("ManageProjectsDialog.NewProjectTitle.ToolTipText")); //$NON-NLS-1$
            newProjectTextField.setPreferredSize(new Dimension(224, 19));
        }
        return newProjectTextField;
    }

    /**
     * This method initializes addProjectButton	.
     * @return javax.swing.JButton	
     */
    private JButton getAddProjectButton() {
        if (addProjectButton == null) {
            addProjectButton = new JButton(new ImageIcon(getClass().getResource("/icons/gtk-add.png")));
            addProjectButton.setText(textBundle.textFor("ManageProjectsDialog.AddProjectButton.Title")); //$NON-NLS-1$
            addProjectButton.setToolTipText(textBundle.textFor("ManageProjectsDialog.AddProjectButton.ToolTipText")); //$NON-NLS-1$
            addProjectButton.addActionListener(new java.awt.event.ActionListener() {   
                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    String projectName = getNewProjectTextField().getText();
                    model.addProject(new Project(RandomUtils.nextLong(), projectName, projectName), ManageProjectsDialog.this);
                    getNewProjectTextField().setText(""); //$NON-NLS-1$
                }

            });
            addProjectButton.setDefaultCapable(true);
        }
        return addProjectButton;
    }

    /**
     * This method initializes removeProjectButton.
     * @return javax.swing.JButton	
     */
    private JButton getRemoveProjectButton() {
        if (removeProjectButton == null) {
            removeProjectButton = new JButton(new ImageIcon(getClass().getResource("/icons/gtk-delete.png")));
            removeProjectButton.setText(textBundle.textFor("ManageProjectsDialog.RemoveProjectButton.Title")); //$NON-NLS-1$
            removeProjectButton.setToolTipText(textBundle.textFor("ManageProjectsDialog.RemoveProjectButton.ToolTipText")); //$NON-NLS-1$
            removeProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(final java.awt.event.ActionEvent e) {
                    for (int index : getProjectTable().getSelectedRows()) {
                        model.removeProject(
                                model.getAllProjectsList().get(index), 
                                ManageProjectsDialog.this
                        );
                    }
                }
            });
        }
        return removeProjectButton;
    }

    @Override
    public void update(final Observable source, final Object eventObject) {
        if (source == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;

        switch (event.getType()) {
        case BaralgaEvent.PROJECT_CHANGED:
            projectTableModel.fireTableDataChanged();
            break;
        }
    }
}
