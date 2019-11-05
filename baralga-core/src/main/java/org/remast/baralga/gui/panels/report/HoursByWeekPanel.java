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
import org.remast.baralga.gui.model.report.HoursByWeek;
import org.remast.baralga.gui.model.report.HoursByWeekReport;
import org.remast.baralga.gui.panels.table.HoursByWeekTableFormat;
import org.remast.baralga.gui.panels.table.HoursByWeekTextFilterator;
import org.remast.swing.JSearchField;
import org.remast.swing.table.JHighligthedTable;
import org.remast.text.DurationFormat;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Panel for displaying the report of working hours by week.
 * @see HoursByWeekReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByWeekPanel extends JXPanel {
	
	private final DateFormat WEEK_FORMAT = newWeekFormat();

    private final DateFormat YEAR_FORMAT = newYearFormat();

    /**
     * The report displayed by this panel.
     */
    private transient HoursByWeekReport report;
    
    /**
     * The table model.
     */
    private DefaultEventTableModel<HoursByWeek> tableModel;
    
    /**
     * Creates a new panel for the given report of hours by week.
     * @param report the report with hours by week
     */
    public HoursByWeekPanel(final HoursByWeekReport report) {
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
		final MatcherEditor<HoursByWeek> textMatcherEditor = new TextComponentMatcherEditor<>(searchField, new HoursByWeekTextFilterator());
		final FilterList<HoursByWeek> textFilteredIssues = new FilterList<>(this.report.getHoursByWeek(), textMatcherEditor);

        tableModel = new DefaultEventTableModel<>(textFilteredIssues, new HoursByWeekTableFormat());

        final JTable table = new JHighligthedTable(tableModel);
		TableComparatorChooser.install(
				table, 
				this.report.getHoursByWeek(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
		);
        
        table.getColumn(table.getColumnName(0)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(WEEK_FORMAT)));
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

    @Subscribe
    public void update(final Object o) {
        if (o instanceof HoursByWeekReport) {
            tableModel.fireTableDataChanged();
        }
    }

    public static DateFormat newWeekFormat() {
        return new SimpleDateFormat("ww");
    }
    public static DateFormat newYearFormat() {
        return new SimpleDateFormat("yyyy");
    }

}
