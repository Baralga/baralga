package org.remast.baralga.gui.panels.table;

import ca.odell.glazedlists.TextFilterator;
import org.remast.baralga.gui.model.report.HoursByDay;
import org.remast.baralga.gui.panels.report.HoursByDayPanel;
import org.remast.text.DurationFormat;

import java.util.List;

/**
 * Prepares the filter strings for quick filtering hours by day.
 * @author remast
 */
public class HoursByDayTextFilterator implements TextFilterator<HoursByDay> {

	@Override
	public void getFilterStrings(List<String> baseList, HoursByDay hoursByDay) {
		if (baseList == null || hoursByDay == null) {
			return;
		}
		
		baseList.add(HoursByDayPanel.DAY_FORMAT.format(hoursByDay.getDay()));
		baseList.add(new DurationFormat().format(hoursByDay.getHours()));
	}

}
