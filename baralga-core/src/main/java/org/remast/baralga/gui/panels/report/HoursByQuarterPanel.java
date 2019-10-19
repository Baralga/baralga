package org.remast.baralga.gui.panels.report;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import com.google.common.eventbus.Subscribe;
import info.clearthought.layout.TableLayout;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.gui.model.report.HoursByQuarter;
import org.remast.baralga.gui.model.report.HoursByQuarterReport;
import org.remast.baralga.gui.panels.table.HoursByQuarterTableFormat;
import org.remast.baralga.gui.panels.table.HoursByQuarterTextFilterator;
import org.remast.swing.JSearchField;
import org.remast.swing.table.JHighligthedTable;
import org.remast.text.DurationFormat;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Panel for displaying the report of working hours by Month.
 * 
 * @see HoursByQuarterReport
 * @author remast
 */
@SuppressWarnings("serial")
public class HoursByQuarterPanel extends JXPanel {

    public static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

    private HoursByQuarterReport report;

    private DefaultEventTableModel<HoursByQuarter> tableModel;

    /**
     * Creates a new panel for the given report of hours by Month.
     * 
     * @param report
     *            the report with hours by Month
     */
    public HoursByQuarterPanel(final HoursByQuarterReport report) {
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
	final MatcherEditor<HoursByQuarter> textMatcherEditor = new TextComponentMatcherEditor<>(searchField,
		new HoursByQuarterTextFilterator());
	final FilterList<HoursByQuarter> textFilteredIssues = new FilterList<>(this.report.getHoursByQuarter(),
		textMatcherEditor);

	tableModel = new DefaultEventTableModel<>(textFilteredIssues, new HoursByQuarterTableFormat());

	final JTable table = new JHighligthedTable(tableModel);
	TableComparatorChooser.install(table, this.report.getHoursByQuarter(),
		TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);

	table.getColumn(table.getColumnName(0))
	.setCellRenderer(new DefaultTableRenderer());
	table.getColumn(table.getColumnName(1))
		.setCellRenderer(new DefaultTableRenderer(new FormatStringValue(YEAR_FORMAT)));
	table.getColumn(table.getColumnName(2))
		.setCellRenderer(new DefaultTableRenderer(new FormatStringValue(new DurationFormat())));

	JScrollPane tableScrollPane = new JScrollPane(table);

	int border = 5;
	final double[][] size = { { border, TableLayout.FILL, border }, // Columns
		{ border, TableLayout.PREFERRED, border, TableLayout.FILL } }; // Rows
	this.setLayout(new TableLayout(size));

	this.add(searchField, "1, 1");
	this.add(tableScrollPane, "1, 3");
    }

    @Subscribe
    public void update(final Object o) {
	if (o instanceof HoursByQuarterReport) {
	    tableModel.fireTableDataChanged();
	}
    }

}
