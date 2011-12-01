package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;

import javax.swing.JTable;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByMonth;
import org.remast.baralga.gui.model.report.HoursByMonthReport;
import org.remast.baralga.gui.panels.table.HoursByMonthTableFormat;
import org.remast.swing.table.JHighligthedTable;

import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.google.common.eventbus.Subscribe;
import com.jidesoft.swing.JideScrollPane;

/**
 * Panel for displaying the report of working hours by Month.
 * @see HoursByMonthReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByMonthPanel extends JXPanel {

    /**
     * The report displayed by this panel.
     */
    private HoursByMonthReport report;
    
    /**
     * The table model.
     */
    private EventTableModel<HoursByMonth> tableModel;
    
    /**
     * Creates a new panel for the given report of hours by Month.
     * @param report the report with hours by Month
     */
    public HoursByMonthPanel(final HoursByMonthReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());
        
        this.report.getEventBus().register(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<HoursByMonth>(this.report.getHoursByMonth(), new HoursByMonthTableFormat());

        final JTable table = new JHighligthedTable(tableModel);
		TableComparatorChooser.install(
				table, 
				this.report.getHoursByMonth(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
		);
        
        table.getColumn(table.getColumnName(0)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(new SimpleDateFormat("MM MMMMMMMMMM"))));
        table.getColumn(table.getColumnName(1)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(new SimpleDateFormat("yyyy"))));
        table.getColumn(table.getColumnName(2)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.durationFormat)));
        
        JideScrollPane table_scroll_pane = new JideScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    @Subscribe public void update(final Object o) {
        if (o != null && o instanceof HoursByMonthReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
