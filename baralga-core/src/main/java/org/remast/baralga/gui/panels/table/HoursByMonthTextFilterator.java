package org.remast.baralga.gui.panels.table;

import ca.odell.glazedlists.TextFilterator;
import org.remast.baralga.gui.model.report.HoursByMonth;
import org.remast.baralga.gui.panels.report.HoursByMonthPanel;
import org.remast.text.DurationFormat;

import java.util.List;

/**
 * Prepares the filter strings for quick filtering hours by month.
 * @author remast
 */
public class HoursByMonthTextFilterator implements TextFilterator<HoursByMonth> {

	@Override
	public void getFilterStrings(List<String> baseList, HoursByMonth hoursByMonth) {
		if (baseList == null || hoursByMonth == null) {
			return;
		}
		
		baseList.add(HoursByMonthPanel.MONTH_FORMAT.format(hoursByMonth.getMonth()));
		baseList.add(HoursByMonthPanel.YEAR_FORMAT.format(hoursByMonth.getMonth()));
		baseList.add(new DurationFormat().format(hoursByMonth.getHours()));
	}

}
