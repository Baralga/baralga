/**
 * 
 */
package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXPanel;
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

    public DescriptionPanelEntry(final ProjectActivity activity) {
        this.activity = activity;
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
                // TODO: Fire Event
                activity.setDescription(editor.getText());
            }
        });
    }

    /**
     * Update internal state from the project activity.
     */
    public void update() {
        this.titledBorder.setTitle(String.valueOf(activity));
    }

}
