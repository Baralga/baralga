package org.remast.baralga.gui.panels.table;

import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.model.report.HoursByProject;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.gui.TableFormat;

/**
 * @author remast
 */
public class HoursByProjectTableFormat implements TableFormat<HoursByProject> {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

    /**
     * Gets the number of columns for the table.
     * @return the numer of columns
     */
    public final int getColumnCount() {
        return 2;
    }

    /**
     * Gets the name of the given column.
     * @param column the number of the column
     * @return the name of the column
     */
    public final String getColumnName(final int column) {
        switch (column) {
        case 0:
            return textBundle.textFor("HoursByDayProjectFormat.ProjectHeading"); //$NON-NLS-1$
        case 1:
            return textBundle.textFor("HoursByDayProjectFormat.HoursHeading"); //$NON-NLS-1$
        default:
            return null;
        }
    }

    public Object getColumnValue(final HoursByProject baseObject, final int column) {
        switch (column) {
        case 0:
            return baseObject.getProject();
        case 1:
            return (Double) baseObject.getHours();
        default:
            return null;
        }
    }

}
