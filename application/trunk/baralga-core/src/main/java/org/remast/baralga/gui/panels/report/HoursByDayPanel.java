package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.JTable;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.joda.time.format.DateTimeFormat;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByDay;
import org.remast.baralga.gui.model.report.HoursByDayReport;
import org.remast.baralga.gui.panels.table.HoursByDayTableFormat;
import org.remast.swing.table.JHighligthedTable;

import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.google.common.eventbus.Subscribe;
import com.jidesoft.swing.JideScrollPane;

/**
 * Panel for displaying the report of working hours by day.
 * @see HoursByDayReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByDayPanel extends JXPanel {
	
	/** Format for one day in report. */
	private static DateFormat DAY_FORMAT = new SimpleDateFormat(DateTimeFormat.patternForStyle("S-", Locale.getDefault()) + " EEEEEEEEE");

    /**
     * The report displayed by this panel.
     */
    private HoursByDayReport report;
    
    /**
     * The table model.
     */
    private EventTableModel<HoursByDay> tableModel;
    
    /**
     * Creates a new panel for the given report of hours by day.
     * @param report the report with hours by day
     */
    public HoursByDayPanel(final HoursByDayReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());
        
        this.report.getEventBus().register(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<HoursByDay>(this.report.getHoursByDay(), new HoursByDayTableFormat());

        final JTable table = new JHighligthedTable(tableModel);
		TableComparatorChooser.install(
				table, 
				this.report.getHoursByDay(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
		);
        
        table.getColumn(table.getColumnName(0)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DAY_FORMAT)));
        table.getColumn(table.getColumnName(1)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.DURATION_FORMAT)));
        
        JideScrollPane table_scroll_pane = new JideScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

   @Subscribe public void update(final Object o) {
        if (o != null && o instanceof HoursByDayReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
