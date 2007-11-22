package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.remast.baralga.gui.model.AccumulatedActivitiesTableFormat;
import org.remast.baralga.gui.utils.GUISettings;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.report.AccumulatedProjectActivity;
import org.remast.baralga.model.report.FilteredReport;
import org.remast.baralga.model.report.ObservingFilteredReport;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * Panel containing the accumulated hours spent on each project on one day.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AccummulatedActitvitiesPanel extends JXPanel implements Observer {

    private FilteredReport report;
    
    private Filter filter;
    
    private EventTableModel<AccumulatedProjectActivity> tableModel;

    public AccummulatedActitvitiesPanel(FilteredReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());

        this.report.addObserver(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        this.setBackground(Color.RED);

        tableModel = new EventTableModel<AccumulatedProjectActivity>(this.report.getAccumulatedActivitiesByDay(), new AccumulatedActivitiesTableFormat());
        final JXTable table = new JXTable(tableModel);
        table.setHighlighters(GUISettings.HIGHLIGHTERS);

        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
        this.report.setFilter(filter);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o != null && o instanceof ObservingFilteredReport) {
            tableModel.fireTableDataChanged();
        }
        
    }
}
