package org.remast.baralga.gui.panels.report;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.ObservingAccumulatedActivitiesReport;
import org.remast.baralga.gui.panels.table.AccumulatedActivitiesTableFormat;
import org.remast.baralga.gui.panels.table.AccumulatedProjectActivityTextFilterator;
import org.remast.baralga.model.report.AccumulatedActivitiesReport;
import org.remast.baralga.model.report.AccumulatedProjectActivity;
import org.remast.swing.JSearchField;
import org.remast.swing.table.JHighligthedTable;

import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import org.remast.text.DurationFormat;

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
		// Init search field and a list filtered list for the quick search
		final JSearchField searchField = new JSearchField();
		final MatcherEditor<AccumulatedProjectActivity> textMatcherEditor = new TextComponentMatcherEditor<>(searchField, new AccumulatedProjectActivityTextFilterator());
		final FilterList<AccumulatedProjectActivity> textFilteredIssues = new FilterList<>(this.report.getAccumulatedActivitiesByDay(), textMatcherEditor);
    	
        tableModel = new EventTableModel<>(textFilteredIssues, new AccumulatedActivitiesTableFormat());
        final JTable table = new JHighligthedTable(tableModel);
		TableComparatorChooser.install(
				table, 
				this.report.getAccumulatedActivitiesByDay(), 
				TableComparatorChooser.MULTIPLE_COLUMN_MOUSE
		);
		
        table.getColumn(table.getColumnName(0)).setCellRenderer(new DefaultTableRenderer(new FormatStringValue(FormatUtils.DAY_FORMAT)));
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

    public void update(final Observable o, final Object arg) {
        if (o != null && o instanceof ObservingAccumulatedActivitiesReport) {
            tableModel.fireTableDataChanged();
        }
    }
}
