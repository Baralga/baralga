package org.remast.baralga.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.DateFormatter;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.utils.Constants;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;

import ca.odell.glazedlists.swing.EventComboBoxModel;

@SuppressWarnings("serial") //$NON-NLS-1$
public class AddActivityDialog extends JDialog {
    
    private JPanel contentPane = null;

    /** Label for activity start time. */
    private JLabel startLabel = null;

    /** Label for activity end time. */
    private JLabel endLabel = null;

    private JLabel projectLabel = null;

    private JComboBox projectSelector = null;

    private JPanel activityPanel = null;

    private JPanel buttonPanel = null;

    private JButton addActivityButton = null;

    private JLabel dateLabel = null;

    private JFormattedTextField startField = null;

    private JFormattedTextField endField = null;

    private PresentationModel model;

    private JXDatePicker datePicker;
    
    
    //------------------------------------------------
    // Fields for project activity
    //------------------------------------------------
    
    private Project project;

    private Date start;

    private Date end;

    /**
     * @param owner
     * @param model 
     */
    public AddActivityDialog(Frame owner, PresentationModel model) {
        super(owner);
        this.model = model;
        
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setIconImage(new ImageIcon(getClass().getResource("/resource/icons/gtk-add.png")).getImage()); //$NON-NLS-1$
        this.setSize(300, 200);
        this.setResizable(false);
        this.setTitle(Messages.getString("AddActivityDialog.AddActivityLabel")); //$NON-NLS-1$
        this.setModal(true);
        this.setContentPane(getJContentPane());
        
        // Set default Button to AddActtivityButton.
        this.getRootPane().setDefaultButton(addActivityButton);
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (contentPane == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap(0);
            borderLayout.setVgap(0);
            projectLabel = new JLabel();
            projectLabel.setText(Messages.getString("AddActivityDialog.ProjectLabel")); //$NON-NLS-1$
            startLabel = new JLabel();
            startLabel.setText(Messages.getString("AddActivityDialog.StartLabel")); //$NON-NLS-1$
            endLabel = new JLabel();
            endLabel.setText(Messages.getString("AddActivityDialog.EndLabel")); //$NON-NLS-1$
            contentPane = new JPanel();
            contentPane.setLayout(borderLayout);
            contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
            contentPane.add(getActivityPanel(), BorderLayout.CENTER);
        }
        return contentPane;
    }

    /**
     * This method initializes projectSelector	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getProjectSelector() {
        if (projectSelector == null) {
            projectSelector = new JComboBox(new EventComboBoxModel<Project>(getModel().getProjectList()));
            
            // Select first entry
            if(!getModel().getProjectList().isEmpty()) {
                Project project = getModel().getProjectList().get(0);
                projectSelector.setSelectedItem(project);
            }
        }
        return projectSelector;
    }

    /**
     * This method initializes activityPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getActivityPanel() {
        if (activityPanel == null) {
            dateLabel = new JLabel();
            dateLabel.setText(Messages.getString("AddActivityDialog.DateLabel")); //$NON-NLS-1$
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(4);
            gridLayout.setHgap(2);
            gridLayout.setVgap(2);
            gridLayout.setColumns(2);
            activityPanel = new JPanel();
            activityPanel.setLayout(gridLayout);
            activityPanel.add(projectLabel, null);
            activityPanel.add(getProjectSelector(), null);
            activityPanel.add(dateLabel, null);
            activityPanel.add(getDatePicker(), null);
            activityPanel.add(startLabel, null);
            activityPanel.add(getStartField(), null);
            activityPanel.add(endLabel, null);
            activityPanel.add(getEndField(), null);
        }
        return activityPanel;
    }

    /**
     * This method initializes buttonPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BorderLayout());
            buttonPanel.add(getAddActivityButton(), BorderLayout.SOUTH);
        }
        return buttonPanel;
    }

    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getAddActivityButton() {
        if (addActivityButton == null) {
            addActivityButton = new JButton();
            addActivityButton.setText(Messages.getString("AddActivityDialog.AddLabel")); //$NON-NLS-1$
            addActivityButton.setIcon(new ImageIcon(getClass().getResource("/resource/icons/gtk-add.png"))); //$NON-NLS-1$
            final AddActivityDialog dia = this;
            final PresentationModel model = getModel();
            addActivityButton.setMnemonic(KeyEvent.VK_ENTER);
            
            addActivityButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent event) {
                    if(dia.validateFields()) {
                        ProjectActivity activity = new ProjectActivity(start, end, project);
                        model.addActivity(activity);
                        resetFields();
                        dia.dispose();
                    }
                }
            });
            
            addActivityButton.setDefaultCapable(true);
        }
        return addActivityButton;
    }

    /**
     * This method initializes jPanel2	
     * 	
     * @return javax.swing.JPanel	
     */
    private JXDatePicker getDatePicker() {
        if(datePicker == null)
            datePicker = new JXDatePicker(System.currentTimeMillis());
        
        return datePicker;
    }



    /**
     * This method initializes startField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JFormattedTextField getStartField() {
        if (startField == null) {
          DateFormatter df  = new DateFormatter(Constants.hhMMFormat);
          startField = new JFormattedTextField(df);
          df.install(startField);

        }
        return startField;
    }

    /**
     * This method initializes endField	
     * 	
     * @return javax.swing.JFormattedTextField	
     */
    private JFormattedTextField getEndField() {
        if (endField == null) {
            DateFormatter df;
            df  = new DateFormatter(Constants.hhMMFormat);
            endField = new JFormattedTextField(df);
            df.install(endField);
        }
        return endField;
    }

    /**
     * @return the model
     */
    public PresentationModel getModel() {
        return model;
    }
    
    public boolean validateFields() {
        if(getProjectSelector().getSelectedItem() == null)
            return false;
        
        if(StringUtils.isEmpty(getStartField().getText()))
            return false;

        if(StringUtils.isEmpty(getEndField().getText()))
            return false;

        try {
            start = Constants.hhMMFormat.parse(getStartField().getText());
            end = Constants.hhMMFormat.parse(getEndField().getText());
            correctDates();
        } catch (ParseException e) {
            return false;
        }
        project = (Project) getProjectSelector().getSelectedItem();

        return true;
    }
    
    private void correctDates() {
        Date day = new Date(getDatePicker().getDateInMillis());
        start = DateUtils.adjustToSameDay(day, start);
        end = DateUtils.adjustToSameDay(day, end);
    }

    public void resetFields() {
        getStartField().setText(null);
        getEndField().setText(null);
        getProjectSelector().setSelectedIndex(-1);
    }

}  //  @jve:decl-index=0:visual-constraint="4,7"
