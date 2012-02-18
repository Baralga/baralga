package org.remast.baralga.gui.panels.report;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByWeek;
import org.remast.baralga.gui.model.report.HoursByWeekReport;
import org.remast.baralga.gui.panels.table.HoursByWeekTableFormat;
import org.remast.baralga.gui.panels.table.HoursByWeekTextFilterator;
import org.remast.swing.JSearchField;
import org.remast.swing.table.JHighligthedTable;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

import com.google.common.eventbus.Subscribe;

/**
 * Panel for displaying the report of working hours by week.
 * @see HoursByWeekReport
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class HoursByWeekPanel extends JXPanel {
	
	public static final DateFormat WEEK_FORMAT = new SimpleDateFormat("ww")
	;
	public static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

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
        
        this.report.getEventBus().register(this);
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
		// Init search field and a list filtered list for the quick search
		final JSearchField searchField = new JSearchField();
		final MatcherEditor<HoursByWeek> textMatcherEditor = new TextComponentMatcherEditor<HoursByWeek>(searchField, new HoursByWeekTextFilterator());
		final FilterList<HoursByWeek> textFilteredIssues = new FilterList<HoursByWeek>(this.report.getHoursByWeek(), textMatcherEditor);

        tableModel = new EventTableModel<HoursByWeek>(textFilteredIssues, new HoursByWeekTableFormat());

        final JTable table = new JHighligthedTable(tableModel);
		TableComparatorChooser.install(
				table, 
				this.report.getHoursByWeek(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
		);
        
        table.getColumn(table.getColumnName(0)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(WEEK_FORMAT)));
        table.getColumn(table.getColumnName(1)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(YEAR_FORMAT)));
        table.getColumn(table.getColumnName(2)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.DURATION_FORMAT)));
        
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
        if (o != null && o instanceof HoursByWeekReport) {
            tableModel.fireTableDataChanged();
        }
    }

}
