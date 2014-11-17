package org.remast.baralga.gui.panels.table;

import ca.odell.glazedlists.TextFilterator;
import org.remast.baralga.gui.model.report.HoursByProject;
import org.remast.text.DurationFormat;

import java.util.List;

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
		baseList.add(new DurationFormat().format(hoursByProject.getHours()));
	}

}
