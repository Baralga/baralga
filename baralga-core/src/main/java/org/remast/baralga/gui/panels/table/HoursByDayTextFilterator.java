package org.remast.baralga.gui.panels.table;

import java.util.List;

import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByDay;
import org.remast.baralga.gui.panels.report.HoursByDayPanel;

import ca.odell.glazedlists.TextFilterator;

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
		baseList.add(FormatUtils.DURATION_FORMAT.format(hoursByDay.getHours()));
	}

}
