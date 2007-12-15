package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.remast.baralga.gui.model.HoursByWeekTableFormat;
import org.remast.baralga.gui.utils.GUISettings;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.report.HoursByWeek;
import org.remast.baralga.model.report.HoursByWeekReport;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByWeekPanel extends JXPanel implements Observer {

    private HoursByWeekReport report;
    
    private EventTableModel<HoursByWeek> tableModel;
    
    public HoursByWeekPanel(HoursByWeekReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());
        
        this.report.addObserver(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<HoursByWeek>(this.report.getHoursByWeek(), new HoursByWeekTableFormat());
        final JXTable table = new JXTable(tableModel);

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

    @Override
    public void update(Observable o, Object arg) {
        if (o != null && o instanceof HoursByWeekReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
