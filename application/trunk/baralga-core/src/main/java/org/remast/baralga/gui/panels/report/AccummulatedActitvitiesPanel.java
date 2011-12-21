package org.remast.baralga.gui.panels.report;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.ObservingAccumulatedActivitiesReport;
import org.remast.baralga.gui.panels.table.AccumulatedActivitiesTableFormat;
import org.remast.baralga.model.report.AccumulatedActivitiesReport;
import org.remast.baralga.model.report.AccumulatedProjectActivity;
import org.remast.swing.table.JHighligthedTable;

import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

import com.jidesoft.swing.JideScrollPane;

/**
 * Panel containing the accumulated hours spent on each project on one day.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class AccummulatedActitvitiesPanel extends JPanel implements Observer {

    private AccumulatedActivitiesReport report;
    
    private EventTableModel<AccumulatedProjectActivity> tableModel;

    public AccummulatedActitvitiesPanel(final AccumulatedActivitiesReport report) {
        this.report = report;
        this.setLayout(new BorderLayout());

        this.report.addObserver(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        tableModel = new EventTableModel<AccumulatedProjectActivity>(this.report.getAccumulatedActivitiesByDay(), new AccumulatedActivitiesTableFormat());
        final JTable table = new JHighligthedTable(tableModel);
		TableComparatorChooser.install(
				table, 
				this.report.getAccumulatedActivitiesByDay(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
		);
		
        table.getColumn(table.getColumnName(0)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.DAY_FORMAT)));
        table.getColumn(table.getColumnName(2)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.DURATION_FORMAT)));

        JideScrollPane table_scroll_pane = new JideScrollPane(table);

        this.add(table_scroll_pane, BorderLayout.CENTER);
    }

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof ObservingAccumulatedActivitiesReport) {
            tableModel.fireTableDataChanged();
        }
        
    }
}
