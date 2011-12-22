/**
 * 
 */
package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.JTextEditor;
import org.remast.swing.util.GuiConstants;

/**
 * Holds the editor for the description of a project activity.
 * @author remast
 */
@SuppressWarnings("serial")
public class DescriptionPanelEntry extends JPanel {

    /** The activity whose description is displayed. */
    private ProjectActivity activity;

    /** The editor to edit the description of the activity. */
    private JTextEditor editor;

    /** The label containing the title of the activity. */
    private JLabel activityLabel;

    /** The model. */
    private PresentationModel model;

    /**
     * Creates a new panel to edit the description.
     * @param activity the activity whose description is displayed
     * @param model the model
     */
    public DescriptionPanelEntry(final ProjectActivity activity, final PresentationModel model) {
        this.activity = activity;
        this.model = model;
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Table.gridColor")));
        activityLabel = new JLabel(String.valueOf(activity));
    	activityLabel.setFont(activityLabel.getFont().deriveFont(activityLabel.getFont().getSize() + 2));
    	
		int border = 5;
		final double[][] size = {
				{ border, TableLayout.FILL, border}, // Columns
				{ 12, TableLayout.PREFERRED, border, TableLayout.FILL, border } }; // Rows
		this.setLayout(new TableLayout(size));

        editor = new JTextEditor();
        editor.setText(activity.getDescription());
        editor.setBorder(BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY));
        
		this.add(activityLabel, "1, 1");
		this.add(editor, "1, 3");


        editor.addTextObserver(new JTextEditor.TextChangeObserver() {

            public void onTextChange() {
                final String oldDescription = activity.getDescription();
                final String newDescription = editor.getText();

                activity.setDescription(newDescription);

                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(
                        activity, 
                        ProjectActivity.PROPERTY_DESCRIPTION, 
                        oldDescription, 
                        newDescription
                );
                model.fireProjectActivityChangedEvent(activity, propertyChangeEvent);
            }
        });
    }

    /**
     * Update internal state from the project activity.
     */
    public void update() {
        this.activityLabel.setText(String.valueOf(activity));
        updateUI();
    }

}
