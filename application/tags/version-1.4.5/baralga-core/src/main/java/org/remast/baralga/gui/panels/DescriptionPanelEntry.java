/**
 * 
 */
package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.text.TextEditor;
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
    private TextEditor editor;

    /** The border containing the title of the activity. */
    private TitledBorder titledBorder;

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
        this.setLayout(new BorderLayout());
        titledBorder = BorderFactory.createTitledBorder(String.valueOf(activity));
        titledBorder.setTitleColor(GuiConstants.DARK_BLUE);
        this.setBorder(titledBorder);

        editor = new TextEditor();
        editor.setText(activity.getDescription());
        editor.setBorder(BorderFactory.createLineBorder(GuiConstants.VERY_LIGHT_GREY));
        this.add(editor, BorderLayout.CENTER);

        editor.addTextObserver(new TextEditor.TextChangeObserver() {

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
        this.titledBorder.setTitle(String.valueOf(activity));
        updateUI();
    }

}
