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
import org.remast.baralga.gui.model.report.HoursByProject;
import org.remast.baralga.gui.model.report.HoursByProjectReport;
import org.remast.baralga.gui.panels.table.HoursByProjectTableFormat;
import org.remast.gui.util.Constants;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * Panel for displaying the report of working hours by project.
 * @see HoursByProjectReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByProjectPanel extends JXPanel implements Observer {

    /**
     * The report displayed by this panel.
     */
    private HoursByProjectReport report;
    
    /**
     * The table model.
     */
    private EventTableModel<HoursByProject> tableModel;
    
    /**
     * Creates a new panel for the given report of hours by project.
     * @param report the report with hours by project
     */
    public HoursByProjectPanel(final HoursByProjectReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());
        
        this.report.addObserver(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<HoursByProject>(this.report.getHoursByProject(), new HoursByProjectTableFormat());

        final JXTable table = new JXTable(tableModel);
        table.setHighlighters(Constants.HIGHLIGHTERS);
        table.setAutoResizeMode(JXTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        table.getColumn(0).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance()))) ;
        table.getColumn(1).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(Constants.durationFormat))) ;
        
        JScrollPane table_scroll_pane = new JScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(Observable o, Object arg) {
        if (o != null && o instanceof HoursByProjectReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
