package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Frame;
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXPanel;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.panels.TextEditor;
import org.remast.baralga.gui.utils.Constants;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;

import ca.odell.glazedlists.swing.EventComboBoxModel;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class AddActivityDialog extends JDialog {

    private JPanel contentPane = null;

    /** Label for activity start time. */
    private JLabel startLabel = new JLabel(Messages.getString("AddActivityDialog.StartLabel")); //$NON-NLS-1$

    /** Label for activity end time. */
    private JLabel endLabel = new JLabel(Messages.getString("AddActivityDialog.EndLabel")); //$NON-NLS-1$;

    /** Label for description. */
    private JLabel descriptionLabel = new JLabel(Messages.getString("AddActivityDialog.DescriptionLabel")); //$NON-NLS-1$

    private JLabel projectLabel = new JLabel(Messages.getString("AddActivityDialog.ProjectLabel")); //$NON-NLS-1$

    private JComboBox projectSelector = null;

    private JPanel activityPanel = null;

    private JPanel buttonPanel = null;

    private JButton addActivityButton = null;

    private JLabel dateLabel = null;

    private JFormattedTextField startField = null;

    private JFormattedTextField endField = null;

    private PresentationModel model;

    private JXDatePicker datePicker;

    // ------------------------------------------------
    // Fields for project activity
    // ------------------------------------------------

    private Project project;

    private Date start;

    private Date end;

    private TextEditor textEditor;

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
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());
        this.setIconImage(new ImageIcon(getClass().getResource("/resource/icons/gtk-add.png")).getImage()); //$NON-NLS-1$
        this.setSize(300, 350);
        this.setTitle(Messages.getString("AddActivityDialog.AddActivityLabel")); //$NON-NLS-1$
        this.setModal(true);
        this.setContentPane(getJContentPane());

        // Initialize selected project
        // a) If no project selected take first project
        if (getModel().getSelectedProject() == null) {
            // Select first entry
            if (!CollectionUtils.isEmpty(getModel().getProjectList())) {
                Project project = getModel().getProjectList().get(0);
                projectSelector.setSelectedItem(project);
            }
        } else {
            // b) Take selected project
            projectSelector.setSelectedItem(getModel().getSelectedProject());
        }

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
            contentPane = new JXPanel();
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
            double border = 5;
            double size[][] = {
                    { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                    { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED,
                            border, TableLayout.PREFERRED, border, TableLayout.FILL, border } }; // Rows

            TableLayout tableLayout = new TableLayout(size);

            dateLabel = new JLabel();
            dateLabel.setText(Messages.getString("AddActivityDialog.DateLabel")); //$NON-NLS-1$
            activityPanel = new JXPanel();
            activityPanel.setLayout(tableLayout);

            activityPanel.add(projectLabel, "1, 1");
            activityPanel.add(getProjectSelector(), "3, 1");

            activityPanel.add(dateLabel, "1, 3");
            activityPanel.add(getDatePicker(), "3, 3");

            activityPanel.add(startLabel, "1, 5");
            activityPanel.add(getStartField(), "3, 5");

            activityPanel.add(endLabel, "1, 7");
            activityPanel.add(getEndField(), "3, 7");

            activityPanel.add(descriptionLabel, "1, 9");
            textEditor = new TextEditor(true, false);
            activityPanel.add(textEditor, "3, 9");
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
                    if (dia.validateFields()) {
                        final ProjectActivity activity = new ProjectActivity(start, end, project);
                        activity.setDescription(textEditor.getText());
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
        if (datePicker == null) {
            datePicker = new JXDatePicker(System.currentTimeMillis());
        }

        return datePicker;
    }

    /**
     * This method initializes startField
     * 
     * @return javax.swing.JTextField
     */
    private JFormattedTextField getStartField() {
        if (startField == null) {
            DateFormatter df = new DateFormatter(Constants.hhMMFormat);
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
            df = new DateFormatter(Constants.hhMMFormat);
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
        if (getProjectSelector().getSelectedItem() == null)
            return false;

        if (StringUtils.isEmpty(getStartField().getText()))
            return false;

        if (StringUtils.isEmpty(getEndField().getText()))
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

}
