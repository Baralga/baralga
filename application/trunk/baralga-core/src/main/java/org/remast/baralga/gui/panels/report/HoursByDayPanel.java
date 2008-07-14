package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.gui.model.report.HoursByDay;
import org.remast.baralga.gui.model.report.HoursByDayReport;
import org.remast.baralga.gui.panels.table.HoursByDayTableFormat;
import org.remast.swing.util.GuiConstants;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * Panel for displaying the report of working hours by day.
 * @see HoursByDayReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByDayPanel extends JXPanel implements Observer {

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
        
        this.report.addObserver(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<HoursByDay>(this.report.getHoursByDay(), new HoursByDayTableFormat());

        final JXTable table = new JXTable(tableModel);
        table.setHighlighters(GuiConstants.HIGHLIGHTERS);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance()))) ;
        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(GuiConstants.durationFormat))) ;
        
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(Observable o, Object arg) {
        if (o != null && o instanceof HoursByDayReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
