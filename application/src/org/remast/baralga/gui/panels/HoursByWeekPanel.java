package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.remast.baralga.gui.model.HoursByWeekTableFormat;
import org.remast.baralga.gui.utils.GUISettings;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.report.HoursByWeek;
import org.remast.baralga.model.report.HoursByWeekReport;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * @author Jan Stamer
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByWeekPanel extends JXPanel {

    private HoursByWeekReport report;
    
    public HoursByWeekPanel(HoursByWeekReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        JXTable table = new JXTable(new EventTableModel<HoursByWeek>(this.report.getHoursByWeek(), new HoursByWeekTableFormat()));

        table.setHighlighters(GUISettings.HIGHLIGHTERS);

        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(Filter filter) {
        this.report.setFilter(filter);
    }

}
