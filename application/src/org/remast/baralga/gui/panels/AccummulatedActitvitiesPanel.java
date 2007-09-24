package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.remast.baralga.gui.model.AccumulatedActivitiesTableFormat;
import org.remast.baralga.gui.utils.GUISettings;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.report.AccumulatedProjectActivity;
import org.remast.baralga.model.report.FilteredReport;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * Panel containing the accumulated hours spent on each project on one day.
 * @author Jan Stamer
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AccummulatedActitvitiesPanel extends JXPanel {

    private FilteredReport report;
    
    private Filter<ProjectActivity> filter;

    public AccummulatedActitvitiesPanel(FilteredReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        this.setBackground(Color.RED);

        JXTable table = new JXTable(new EventTableModel<AccumulatedProjectActivity>(this.report.getAccumulatedActivitiesByDay(), new AccumulatedActivitiesTableFormat()));


        table.setHighlighters(GUISettings.HIGHLIGHTERS);

        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    /**
     * @return the filter
     */
    public Filter<ProjectActivity> getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(Filter<ProjectActivity> filter) {
        this.filter = filter;
        this.report.setFilter(filter);
    }
}
