package org.remast.baralga.gui.panels.table;

import java.util.List;

import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.model.report.HoursByProject;

import ca.odell.glazedlists.TextFilterator;

/**
 * Prepares the filter strings for quick filtering hours by project.
 * @author remast
 */
public class HoursByProjectTextFilterator implements TextFilterator<HoursByProject> {

	@Override
	public void getFilterStrings(List<String> baseList, HoursByProject hoursByProject) {
		if (baseList == null || hoursByProject == null) {
			return;
		}
		
		baseList.add(hoursByProject.getProject().getTitle());
		baseList.add(FormatUtils.DURATION_FORMAT.format(hoursByProject.getHours()));
	}

}
