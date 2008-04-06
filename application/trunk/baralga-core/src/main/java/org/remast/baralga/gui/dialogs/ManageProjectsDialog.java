package org.remast.baralga.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.commons.lang.math.RandomUtils;
import org.jdesktop.swingx.JXList;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.gui.util.EscapeDialog;

import ca.odell.glazedlists.swing.EventListModel;

/**
 * @author remast
 * :TODO: Rework the dialog to use table layout.
 */
@SuppressWarnings("serial")
public class ManageProjectsDialog extends EscapeDialog {

    private JPanel jContentPane = null;

    private JXList projectList = null;

    private JTextField newProjectTextField = null;

    private JPanel projectsPanel = null;

    private JButton addProjectButton = null;

    private JButton removeProjectButton = null;

    private JPanel newProjectNamePanel = null;

    private JLabel lableProjectTitle = null;

    private PresentationModel model;

    /**
     * @param owner
     */
    public ManageProjectsDialog(final Frame owner, final PresentationModel model) {
        super(owner);
        
        setModel(model);
        
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
        this.setTitle(Messages.getString("ManageProjectsDialog.Title")); //$NON-NLS-1$
        this.setContentPane(getJContentPane());
        
        // Set default Button to AddProjectsButton.
        this.getRootPane().setDefaultButton(addProjectButton);
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getNewProjectNamePanel(), BorderLayout.NORTH);
            jContentPane.add(getProjectsPanel(), BorderLayout.EAST);
            
            JScrollPane projectListScrollPane = new JScrollPane(getProjectList());
            jContentPane.add(projectListScrollPane, BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes projectList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getProjectList() {
        if (projectList == null) {
            projectList = new JXList();
            projectList.setModel(new EventListModel<Project>(getModel().getProjectList()));
            projectList.setToolTipText(Messages.getString("ManageProjectsDialog.ProjectList.ToolTipText")); //$NON-NLS-1$
        }
        return projectList;
    }

    /**
     * This method initializes newProjectTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getNewProjectTextField() {
        if (newProjectTextField == null) {
            newProjectTextField = new JTextField();
            newProjectTextField.setName(Messages.getString("ManageProjectsDialog.4")); //$NON-NLS-1$
            newProjectTextField.setText(Messages.getString("ManageProjectsDialog.NewProjectTitle.DefaultNewProjectName")); //$NON-NLS-1$
            newProjectTextField.setToolTipText(Messages.getString("ManageProjectsDialog.NewProjectTitle.ToolTipText")); //$NON-NLS-1$
            newProjectTextField.setPreferredSize(new Dimension(224, 19));
        }
        return newProjectTextField;
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getProjectsPanel() {
        if (projectsPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            projectsPanel = new JPanel();
            projectsPanel.setLayout(gridLayout);
            projectsPanel.add(getAddProjectButton(), null);
            projectsPanel.add(getRemoveProjectButton(), null);
        }
        
        return projectsPanel;
    }

    /**
     * This method initializes addProjectButton	.
     * @return javax.swing.JButton	
     */
    private JButton getAddProjectButton() {
        if (addProjectButton == null) {
            addProjectButton = new JButton(new ImageIcon(getClass().getResource("/icons/gtk-add.png")));
            addProjectButton.setText(Messages.getString("ManageProjectsDialog.AddProjectButton.Title")); //$NON-NLS-1$
            addProjectButton.setToolTipText(Messages.getString("ManageProjectsDialog.AddProjectButton.ToolTipText")); //$NON-NLS-1$
            addProjectButton.addActionListener(new java.awt.event.ActionListener() {   
            	public void actionPerformed(java.awt.event.ActionEvent e) {
                    String projectName = getNewProjectTextField().getText();
                    getModel().addProject(new Project(RandomUtils.nextLong(), projectName, projectName), ManageProjectsDialog.this);
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
            removeProjectButton = new JButton(new ImageIcon(getClass().getResource("/icons/gtk-stop.png")));
            removeProjectButton.setText(Messages.getString("ManageProjectsDialog.RemoveProjectButton.Title")); //$NON-NLS-1$
            removeProjectButton.setToolTipText(Messages.getString("ManageProjectsDialog.RemoveProjectButton.ToolTipText")); //$NON-NLS-1$
            removeProjectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    for (Object item : getProjectList().getSelectedValues()) {
                        getModel().removeProject((Project) item, ManageProjectsDialog.this);
                    }
                }
            });
        }
        return removeProjectButton;
    }

    /**
     * @return the model
     */
    public PresentationModel getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    private void setModel(PresentationModel model) {
        this.model = model;
    }

    /**
     * This method initializes jPanel1	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getNewProjectNamePanel() {
        if (newProjectNamePanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
            flowLayout.setVgap(3);
            flowLayout.setHgap(3);
            lableProjectTitle = new JLabel();
            lableProjectTitle.setText(Messages.getString("ManageProjectsDialog.ProjectSelector.Title")); //$NON-NLS-1$
            lableProjectTitle.setBackground(Color.lightGray);
            newProjectNamePanel = new JPanel();
            newProjectNamePanel.setLayout(flowLayout);
            newProjectNamePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.gray));
            newProjectNamePanel.add(lableProjectTitle, null);
            newProjectNamePanel.add(getNewProjectTextField(), null);
        }
        return newProjectNamePanel;
    }

}
