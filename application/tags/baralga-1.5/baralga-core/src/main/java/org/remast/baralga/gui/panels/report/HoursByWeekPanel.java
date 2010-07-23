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
import org.remast.baralga.gui.model.report.HoursByWeek;
import org.remast.baralga.gui.model.report.HoursByWeekReport;
import org.remast.baralga.gui.panels.table.HoursByWeekTableFormat;
import org.remast.swing.util.GuiConstants;

import ca.odell.glazedlists.swing.EventTableModel;

import com.jidesoft.swing.JideScrollPane;

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
    public HoursByWeekPanel(final HoursByWeekReport report) {
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
        table.setHighlighters(GuiConstants.HIGHLIGHTERS);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(new SimpleDateFormat("ww"))));
        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(new SimpleDateFormat("yyyy"))));
        table.getColumn(2).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.durationFormat)));
        
        JideScrollPane table_scroll_pane = new JideScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof HoursByWeekReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
