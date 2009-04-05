/**
 * 
 */
package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXPanel;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.text.TextEditor;
import org.remast.swing.util.GuiConstants;

/**
 * Holds the editor for the description of a project activity.
 * @author remast
 */
@SuppressWarnings("serial")
public class DescriptionPanelEntry extends JXPanel {

    private ProjectActivity activity;

    private TextEditor editor;

    private TitledBorder titledBorder;
    
    private PresentationModel model;

    public DescriptionPanelEntry(final ProjectActivity activity, final PresentationModel model) {
        this.activity = activity;
        this.model = model;
        initialize();
    }

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
                final String newDescription = editor.getText();
                
                ProjectActivity newActivity = activity.withDescription(newDescription);

                model.replaceActivity(activity, newActivity, this);
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
