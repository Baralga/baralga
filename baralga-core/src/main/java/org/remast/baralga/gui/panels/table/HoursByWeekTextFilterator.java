package org.remast.baralga.gui.panels.table;

import java.util.List;

import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByWeek;
import org.remast.baralga.gui.panels.report.HoursByWeekPanel;

import ca.odell.glazedlists.TextFilterator;
import org.remast.text.DurationFormat;

/**
 * Prepares the filter strings for quick filtering hours by week.
 * @author remast
 */
public class HoursByWeekTextFilterator implements TextFilterator<HoursByWeek> {

	@Override
	public void getFilterStrings(List<String> baseList, HoursByWeek hoursByWeek) {
		if (baseList == null || hoursByWeek == null) {
			return;
		}
		
		baseList.add(HoursByWeekPanel.WEEK_FORMAT.format(hoursByWeek.getWeek()));
		baseList.add(HoursByWeekPanel.YEAR_FORMAT.format(hoursByWeek.getWeek()));
		baseList.add(new DurationFormat().format(hoursByWeek.getHours()));
	}

}
