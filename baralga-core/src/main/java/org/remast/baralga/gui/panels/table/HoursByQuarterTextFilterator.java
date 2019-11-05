package org.remast.baralga.gui.panels.table;

import ca.odell.glazedlists.TextFilterator;
import org.remast.baralga.gui.model.report.HoursByQuarter;
import org.remast.baralga.gui.panels.report.HoursByQuarterPanel;
import org.remast.text.DurationFormat;

import java.util.List;

/**
 * Prepares the filter strings for quick filtering hours by month.
 * 
 * @author remast
 */
public class HoursByQuarterTextFilterator implements TextFilterator<HoursByQuarter> {

    @Override
    public void getFilterStrings(List<String> baseList, HoursByQuarter hoursByQuarter) {
	if (baseList == null || hoursByQuarter == null) {
	    return;
	}

	baseList.add(String.valueOf(hoursByQuarter.getQuarter()));
	baseList.add(HoursByQuarterPanel.newYearFormat().format(hoursByQuarter.getDate()));
	baseList.add(new DurationFormat().format(hoursByQuarter.getHours()));
    }

}
