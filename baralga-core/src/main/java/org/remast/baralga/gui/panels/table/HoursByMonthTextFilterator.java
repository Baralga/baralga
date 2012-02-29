package org.remast.baralga.gui.panels.table;

import java.util.List;

import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByMonth;
import org.remast.baralga.gui.panels.report.HoursByMonthPanel;

import ca.odell.glazedlists.TextFilterator;

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
		baseList.add(FormatUtils.DURATION_FORMAT.format(hoursByMonth.getHours()));
	}

}
