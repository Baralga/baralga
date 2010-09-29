//---------------------------------------------------------
// $Id$ 
// 
// (c) 2010 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXTitledSeparator;
import org.joda.time.DateTime;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

public class DailyActivitiesPanel extends JPanel implements Observer {

    private final PresentationModel model;
    
    private DateTime day;
    
    public DailyActivitiesPanel(PresentationModel model) {
    	this.model = model;
    	
    	this.model.addObserver(this);
	}

	/**
     * Set up GUI components.
     */
    private void initialize() {
    	this.setLayout(new BorderLayout());
    	
        JPanel container = new JPanel();
        BoxLayout bl = new BoxLayout(container, BoxLayout.Y_AXIS);
		container.setLayout(bl);

        List<ProjectActivity> activities = model.findActivitiesByDay(day);
        
        this.add(new JXHeader(FormatUtils.formatDate(day), null), BorderLayout.NORTH);

        for (ProjectActivity act : activities) {
        	JPanel actPanel = buildActivityPanel(act);
        	container.add(actPanel);
        }
        
        this.add(container, BorderLayout.CENTER);
    }


    public JPanel buildActivityPanel(ProjectActivity act) {
    	JPanel jp = new JPanel();
    	jp.setLayout(new BorderLayout());
    	
        JTextPane textPane = new JTextPane();
        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule("body {font-family: Tahoma; font-size: 11pt; font-style: normal; font-weight: normal;}");

        HTMLEditorKit editorKit = new HTMLEditorKit();
        editorKit.setStyleSheet(styleSheet);
        textPane.setEditorKit(editorKit);
        textPane.setText(act.getDescription());
        textPane.setEditable(false);

        jp.setBackground(Color.WHITE);
        
        final double border = 5;
        final double[][] size = { 
                {border, TableLayout.FILL, border}, // Columns
                {border * 2, TableLayout.PREFERRED, border, TableLayout.FILL, border} // Rows
        };

        final TableLayout tableLayout = new TableLayout(size);
        jp.setLayout(tableLayout);

        final String title = FormatUtils.formatTime(act.getStart()) + " - " + FormatUtils.formatTime(act.getEnd()) + " ("
        + FormatUtils.durationFormat.format(act.getDuration()) + " h) " + act.getProject();
        jp.add(new JXTitledSeparator(title), "1, 1");
        jp.add(textPane, "1, 3");
        
        return jp;
    }
    
    public void setDay(final DateTime day) {
    	this.day = day;
    	
    	if (this.day != null) {
            initialize();
    	}
    }

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public void update(final Observable source, final Object eventObject) {
		if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
			return;
		}

		final BaralgaEvent event = (BaralgaEvent) eventObject;
//		ProjectActivity activity;

		switch (event.getType()) {

		case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
		case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
		case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
		case BaralgaEvent.PROJECT_CHANGED:
		case BaralgaEvent.DATA_CHANGED:
			initialize();
			break;

		case BaralgaEvent.FILTER_CHANGED:
			break;
		}
	}

}
