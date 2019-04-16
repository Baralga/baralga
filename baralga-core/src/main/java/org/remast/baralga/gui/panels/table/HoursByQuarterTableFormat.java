package org.remast.baralga.gui.panels.table;

import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.model.report.HoursByQuarter;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.gui.TableFormat;

/**
 * @author remast
 */
public class HoursByQuarterTableFormat implements TableFormat<HoursByQuarter> {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

    /**
     * Gets the number of columns for the table.
     */
    public int getColumnCount() {
        return 3;
    }

    /**
     * Gets the name of the given column.
     * @param column the number of the column
     */
    public String getColumnName(final int column) {
        switch (column) {
        case 0:
            return textBundle.textFor("HoursByQuarterTableFormat.QuarterHeading"); //$NON-NLS-1$
        case 1:
            return textBundle.textFor("HoursByQuarterTableFormat.YearHeading"); //$NON-NLS-1$
        case 2:
            return textBundle.textFor("HoursByQuarterTableFormat.HoursHeading"); //$NON-NLS-1$
        default:
            return null;
        }
    }

    public Object getColumnValue(final HoursByQuarter baseObject, final int column) {
        switch (column) {
        case 0:
            return "Q" + baseObject.getQuarter().getQuarter();
        case 1:
            return baseObject.getDate();
        case 2:
            return baseObject.getHours();
        default:
            return null;
        }
    }

}
