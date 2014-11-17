package org.remast.baralga.gui.panels.table;

import java.util.List;

import org.remast.baralga.FormatUtils;
import org.remast.baralga.model.report.AccumulatedProjectActivity;

import ca.odell.glazedlists.TextFilterator;
import org.remast.text.DurationFormat;

/**
 * Prepares the filter strings for quick filtering project activities.
 * @author remast
 */
public class AccumulatedProjectActivityTextFilterator implements TextFilterator<AccumulatedProjectActivity>  {

	@Override
	public void getFilterStrings(List<String> baseList, AccumulatedProjectActivity activity) {
		if (baseList == null || activity == null) {
			return;
		}
		
		baseList.add(activity.getProject().getTitle());
		baseList.add(FormatUtils.formatDay(activity.getDayDateTime()));
		baseList.add(new DurationFormat().format(activity.getTime()));
	}

}
