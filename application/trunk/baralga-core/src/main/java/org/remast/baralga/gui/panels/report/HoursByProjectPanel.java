package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTable;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByProject;
import org.remast.baralga.gui.model.report.HoursByProjectReport;
import org.remast.baralga.gui.panels.table.HoursByProjectTableFormat;
import org.remast.swing.table.JHighligthedTable;

import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jidesoft.swing.JideScrollPane;

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

        final JTable table = new JHighligthedTable(tableModel);
		TableComparatorChooser.install(
				table, 
				this.report.getHoursByProject(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
		);
        
        table.getColumn(table.getColumnName(0)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance())));
        table.getColumn(table.getColumnName(1)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.durationFormat)));
        
        JideScrollPane table_scroll_pane = new JideScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof HoursByProjectReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
