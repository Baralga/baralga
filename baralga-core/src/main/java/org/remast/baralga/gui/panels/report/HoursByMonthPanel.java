package org.remast.baralga.gui.panels.report;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import com.google.common.eventbus.Subscribe;
import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.gui.model.report.HoursByMonth;
import org.remast.baralga.gui.model.report.HoursByMonthReport;
import org.remast.baralga.gui.panels.table.HoursByMonthTableFormat;
import org.remast.baralga.gui.panels.table.HoursByMonthTextFilterator;
import org.remast.swing.JSearchField;
import org.remast.swing.table.JHighligthedTable;
import org.remast.text.DurationFormat;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Panel for displaying the report of working hours by Month.
 * @see HoursByMonthReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByMonthPanel extends JXPanel {
	
	public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("MM MMMMMMMMMM");
	
	public static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

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
		// Init search field and a list filtered list for the quick search
		final JSearchField searchField = new JSearchField();
		final MatcherEditor<HoursByMonth> textMatcherEditor = new TextComponentMatcherEditor<>(searchField, new HoursByMonthTextFilterator());
		final FilterList<HoursByMonth> textFilteredIssues = new FilterList<>(this.report.getHoursByMonth(), textMatcherEditor);

        tableModel = new EventTableModel<>(textFilteredIssues, new HoursByMonthTableFormat());

        final JTable table = new JHighligthedTable(tableModel);
		TableComparatorChooser.install(
				table, 
				this.report.getHoursByMonth(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
		);
        
        table.getColumn(table.getColumnName(0)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(MONTH_FORMAT)));
        table.getColumn(table.getColumnName(1)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(YEAR_FORMAT)));
        table.getColumn(table.getColumnName(2)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(new DurationFormat())));
        
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
        if (o != null && o instanceof HoursByMonthReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
