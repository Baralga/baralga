package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.DateFormatter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
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

    /** Label for activity start time. */
    private JLabel startLabel = new JLabel(Messages.getString("AddActivityDialog.StartLabel")); //$NON-NLS-1$

    /** Label for activity end time. */
    private JLabel endLabel = new JLabel(Messages.getString("AddActivityDialog.EndLabel")); //$NON-NLS-1$;

    /** Label for description. */
    private JLabel descriptionLabel = new JLabel(Messages.getString("AddActivityDialog.DescriptionLabel")); //$NON-NLS-1$

    private JLabel projectLabel = new JLabel(Messages.getString("AddActivityDialog.ProjectLabel")); //$NON-NLS-1$

    private JComboBox projectSelector = null;

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

    private TextEditor descriptionEditor;

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
        
        initializeLayout();

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
    private void initializeLayout() {
        double border = 5;
        double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED,
                    border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.PREFERRED, border} }; // Rows

        TableLayout tableLayout = new TableLayout(size);

        dateLabel = new JLabel();
        dateLabel.setText(Messages.getString("AddActivityDialog.DateLabel")); //$NON-NLS-1$
        this.setLayout(tableLayout);

        this.add(projectLabel, "1, 1");
        this.add(getProjectSelector(), "3, 1");

        this.add(dateLabel, "1, 3");
        this.add(getDatePicker(), "3, 3");

        this.add(startLabel, "1, 5");
        this.add(getStartField(), "3, 5");

        this.add(endLabel, "1, 7");
        this.add(getEndField(), "3, 7");

        this.add(descriptionLabel, "1, 9");
        descriptionEditor = new TextEditor(true, false);
        descriptionEditor.setBorder(BorderFactory.createLineBorder(Constants.VERY_LIGHT_GREY));
        this.add(descriptionEditor, "3, 9");

        this.add(getAddActivityButton(), "1, 11, 3, 11");
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
                        activity.setDescription(descriptionEditor.getText());
                        model.addActivity(activity);
                        dia.dispose();
                    }
                }
            });

            addActivityButton.setDefaultCapable(true);
        }
        return addActivityButton;
    }


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
        if (getProjectSelector().getSelectedItem() == null) {
            return false;
        }

        if (StringUtils.isBlank(getStartField().getText())) {
            return false;
        }

        if (StringUtils.isBlank(getEndField().getText())) {
            return false;
        }

        try {
            start = Constants.hhMMFormat.parse(getStartField().getText());
            end = Constants.hhMMFormat.parse(getEndField().getText());
            
            correctDates();
        } catch (ParseException e) {
            // On parse error one of the dates is not valid
            return false;
        }
        
        project = (Project) getProjectSelector().getSelectedItem();

        // All tests passed so dialog contains valid data
        return true;
    }

    private void correctDates() {
        final Date day = new Date(getDatePicker().getDateInMillis());
        start = DateUtils.adjustToSameDay(day, start);
        end = DateUtils.adjustToSameDay(day, end);
    }

}
