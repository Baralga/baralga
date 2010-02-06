package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByMonth;
import org.remast.baralga.gui.model.report.HoursByMonthReport;
import org.remast.baralga.gui.panels.table.HoursByMonthTableFormat;
import org.remast.swing.util.GuiConstants;

import ca.odell.glazedlists.swing.EventTableModel;

import com.jidesoft.swing.JideScrollPane;

/**
 * Panel for displaying the report of working hours by Month.
 * @see HoursByMonthReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByMonthPanel extends JXPanel implements Observer {

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
        
        this.report.addObserver(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<HoursByMonth>(this.report.getHoursByMonth(), new HoursByMonthTableFormat());

        final JXTable table = new JXTable(tableModel);
        table.setHighlighters(GuiConstants.HIGHLIGHTERS);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(new SimpleDateFormat("MM MMMMMMMMMM yyyy"))));
        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.durationFormat)));
        
        JideScrollPane table_scroll_pane = new JideScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof HoursByMonthReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
