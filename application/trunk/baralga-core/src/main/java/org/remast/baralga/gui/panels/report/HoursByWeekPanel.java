package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.gui.model.report.HoursByWeek;
import org.remast.baralga.gui.model.report.HoursByWeekReport;
import org.remast.baralga.gui.panels.table.HoursByWeekTableFormat;
import org.remast.gui.util.Constants;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * Panel for displaying the report of working hours by week.
 * @see HoursByWeekReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByWeekPanel extends JXPanel implements Observer {

    /**
     * The report displayed by this panel.
     */
    private HoursByWeekReport report;
    
    /**
     * The table model.
     */
    private EventTableModel<HoursByWeek> tableModel;
    
    /**
     * Creates a new panel for the given report of hours by week.
     * @param report the report with hours by week
     */
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
        table.setHighlighters(Constants.HIGHLIGHTERS);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(Constants.durationFormat))) ;
        
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(Observable o, Object arg) {
        if (o != null && o instanceof HoursByWeekReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
