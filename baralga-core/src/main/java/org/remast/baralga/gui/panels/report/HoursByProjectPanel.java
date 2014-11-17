package org.remast.baralga.gui.panels.report;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.text.DateFormat;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByProject;
import org.remast.baralga.gui.model.report.HoursByProjectReport;
import org.remast.baralga.gui.panels.table.HoursByProjectTableFormat;
import org.remast.baralga.gui.panels.table.HoursByProjectTextFilterator;
import org.remast.swing.JSearchField;
import org.remast.swing.table.JHighligthedTable;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.google.common.eventbus.Subscribe;
import org.remast.text.DurationFormat;

/**
 * Panel for displaying the report of working hours by project.
 * @see HoursByProjectReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByProjectPanel extends JXPanel {

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
        
        this.report.getEventBus().register(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
		// Init search field and a list filtered list for the quick search
		final JSearchField searchField = new JSearchField();
		final MatcherEditor<HoursByProject> textMatcherEditor = new TextComponentMatcherEditor<>(searchField, new HoursByProjectTextFilterator());
		final FilterList<HoursByProject> textFilteredIssues = new FilterList<>(this.report.getHoursByProject(), textMatcherEditor);

        tableModel = new EventTableModel<>(textFilteredIssues, new HoursByProjectTableFormat());

        final JTable table = new JHighligthedTable(tableModel);
		TableComparatorChooser.install(
				table, 
				this.report.getHoursByProject(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
		);
        
        table.getColumn(table.getColumnName(0)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(DateFormat.getDateInstance())));
        table.getColumn(table.getColumnName(1)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(new DurationFormat())));
        
        JScrollPane tableScrollPane = new JScrollPane(table);

		int border = 5;
		final double[][] size = {
				{ border, TableLayout.FILL, border}, // Columns
				{ border, TableLayout.PREFERRED, border, TableLayout.FILL } }; // Rows
		this.setLayout(new TableLayout(size));

		this.add(searchField, "1, 1");
		this.add(tableScrollPane, "1, 3");
		}

    @Subscribe public void update(final Object o) {
        if (o != null && o instanceof HoursByProjectReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
