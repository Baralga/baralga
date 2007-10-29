/**
 * 
 */
package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXPanel;
import org.remast.baralga.gui.utils.Constants;
import org.remast.baralga.model.ProjectActivity;

/**
 * @author Jan
 *
 */
public class DescriptionPanelEntry extends JXPanel {

    private ProjectActivity activity;
    private JXTextEditor editor;
    private TitledBorder titledBorder;

    public DescriptionPanelEntry(ProjectActivity activity) {
        this.activity = activity;
        initialize();
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        titledBorder = BorderFactory.createTitledBorder(String.valueOf(activity));
        titledBorder.setTitleColor(Constants.DARK_BLUE);
        this.setBorder(titledBorder);

        editor = new JXTextEditor();
        editor.setText(activity.getDescription());
        editor.setBorder(BorderFactory.createLineBorder(Constants.VERY_LIGHT_GREY));
        this.add(editor, BorderLayout.CENTER);

        editor.addTextObserver(new JXTextEditor.TextChangeObserver(){

            @Override
            public void onTextChange() {
                activity.setDescription(editor.getText());
            }
        });
    }

    public void update() {
        this.titledBorder.setTitle(String.valueOf(activity));
    }

}
