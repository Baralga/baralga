package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.DateFormatter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.gui.text.TextEditor;
import org.remast.gui.util.Constants;
import org.remast.gui.util.EscapeDialog;
import org.remast.util.DateUtils;

import ca.odell.glazedlists.swing.EventComboBoxModel;

/**
 * Dialog for manually adding a project activity.
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class AddActivityDialog extends EscapeDialog {

    // ------------------------------------------------
    // Labels
    // ------------------------------------------------
    
    /** Label for activity start time. */
    private final JLabel startLabel = new JLabel(Messages.getString("AddActivityDialog.StartLabel")); //$NON-NLS-1$

    /** Label for activity end time. */
    private final JLabel endLabel = new JLabel(Messages.getString("AddActivityDialog.EndLabel")); //$NON-NLS-1$;

    /** Label for description. */
    private final JLabel descriptionLabel = new JLabel(Messages.getString("AddActivityDialog.DescriptionLabel")); //$NON-NLS-1$

    /** Label for project. */
    private final JLabel projectLabel = new JLabel(Messages.getString("AddActivityDialog.ProjectLabel")); //$NON-NLS-1$

    /** Label for date. */
    private final JLabel dateLabel = new JLabel(Messages.getString("AddActivityDialog.DateLabel")); //$NON-NLS-1$

    // ------------------------------------------------
    // Edit components
    // ------------------------------------------------
    
    /** Selects the project of the activity. */
    private JComboBox projectSelector = null;

    private JButton addActivityButton = null;

    /** Start of the project activity. */
    private JFormattedTextField startField = null;

    /** End of the project activity. */
    private JFormattedTextField endField = null;

    /** The model. */
    private PresentationModel model;

    /** Selects the date of the activity. */
    private JXDatePicker datePicker;

    /** The description of the activity. */
    private TextEditor descriptionEditor;
    
    // ------------------------------------------------
    // Fields for project activity
    // ------------------------------------------------

    /** The project of the activity. */
    private Project project;

    /** The start time of the activity. */
    private Date start;

    /** The end time of the activity. */
    private Date end;

    /**
     * Create a new dialog.
     * @param owner
     * @param model
     */
    public AddActivityDialog(final Frame owner, final PresentationModel model) {
        super(owner);
        this.model = model;

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());
        this.setIconImage(new ImageIcon(getClass().getResource("/icons/gtk-add.png")).getImage()); //$NON-NLS-1$
        this.setSize(300, 350);
        this.setTitle(Messages.getString("AddActivityDialog.AddActivityLabel")); //$NON-NLS-1$
        this.setModal(true);
        
        initializeLayout();

        // Initialize selected project
        // a) If no project selected take first project
        if (model.getSelectedProject() == null) {
            // Select first entry
            if (!CollectionUtils.isEmpty(model.getProjectList())) {
                Project project = model.getProjectList().get(0);
                projectSelector.setSelectedItem(project);
            }
        } else {
            // b) Take selected project
            projectSelector.setSelectedItem(model.getSelectedProject());
        }
        
        // Initialize start and end time with current time
        final String now = Constants.hhMMFormat.format(new Date());
        this.startField.setText(now);
        this.endField.setText(now);

        // Set default Button to AddActtivityButton.
        this.getRootPane().setDefaultButton(addActivityButton);
    }

    /**
     * This method initializes projectSelector
     * @return javax.swing.JComboBox
     */
    private JComboBox getProjectSelector() {
        if (projectSelector == null) {
            projectSelector = new JComboBox(new EventComboBoxModel<Project>(model.getProjectList()));
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
     * @return javax.swing.JButton
     */
    private JButton getAddActivityButton() {
        if (addActivityButton == null) {
            addActivityButton = new JButton();
            addActivityButton.setText(Messages.getString("AddActivityDialog.AddLabel")); //$NON-NLS-1$
            addActivityButton.setIcon(new ImageIcon(getClass().getResource("/icons/gtk-add.png"))); //$NON-NLS-1$

            addActivityButton.setMnemonic(KeyEvent.VK_ENTER);

            addActivityButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (AddActivityDialog.this.validateFields()) {
                        final ProjectActivity activity = new ProjectActivity(start, end, project);
                        activity.setDescription(descriptionEditor.getText());
                        model.addActivity(activity, this);
                        AddActivityDialog.this.dispose();
                    }
                }
            });

            addActivityButton.setDefaultCapable(true);
        }
        return addActivityButton;
    }


    private JXDatePicker getDatePicker() {
        if (datePicker == null) {
            datePicker = new JXDatePicker(new Date());
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
     * Validates the field to ensure that the entered data is valid.
     * @return
     */
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

    /** Correct the start and end date so that they are on the same day in year. */
    private void correctDates() {
        final Date day = getDatePicker().getDate();
        start = DateUtils.adjustToSameDay(day, start);
        end = DateUtils.adjustToSameDay(day, end);
    }

}
