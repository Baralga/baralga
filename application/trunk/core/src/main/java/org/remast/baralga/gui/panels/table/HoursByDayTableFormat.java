package org.remast.baralga.gui.panels.table;

import java.text.DateFormat;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.model.report.HoursByDay;
import org.remast.gui.util.Constants;

import ca.odell.glazedlists.gui.TableFormat;

/**
 * @author remast
 */
public class HoursByDayTableFormat implements TableFormat<HoursByDay> {

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return Messages.getString("HoursByDayTableFormat.DayHeading"); //$NON-NLS-1$
        case 1:
            return Messages.getString("HoursByDayTableFormat.HoursHeading"); //$NON-NLS-1$
        default:
            return null;
        }
    }

    public Object getColumnValue(HoursByDay baseObject, int column) {
        switch (column) {
        case 0:
            return DateFormat.getDateInstance().format(baseObject.getDay());
        case 1:
            return Constants.durationFormat.format(baseObject.getHours());
        default:
            return null;
        }
    }

}
