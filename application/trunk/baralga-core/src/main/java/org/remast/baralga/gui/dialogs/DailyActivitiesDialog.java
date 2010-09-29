package org.remast.baralga.gui.dialogs;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXTitledSeparator;
import org.joda.time.DateTime;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.DailyActivitiesPanel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.dialog.EscapeDialog;
import org.remast.util.TextResourceBundle;

import com.jidesoft.swing.JideScrollPane;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class DailyActivitiesDialog extends EscapeDialog {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(DailyActivitiesDialog.class);

    private final PresentationModel model;

	private DailyActivitiesPanel dailyActivitiesPanel;

    /**
     * Creates a new dialog.
     * @param owner the owning frame
     * @param presentationModel 
     */
    public DailyActivitiesDialog(final Frame owner, PresentationModel model) {
        super(owner);

        this.model = model;
        this.setName("aboutDialog"); //$NON-NLS-1$
        setTitle(textBundle.textFor("TodaysActivities.Title")); //$NON-NLS-1$
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        dailyActivitiesPanel = new DailyActivitiesPanel(model);
        
        JideScrollPane scrollPane = new JideScrollPane(dailyActivitiesPanel);
        
        this.add(scrollPane, BorderLayout.CENTER);


        this.setSize(340, 320);   
    }

	public void setDay(DateTime day) {
		dailyActivitiesPanel.setDay(day);
	}
    

}
