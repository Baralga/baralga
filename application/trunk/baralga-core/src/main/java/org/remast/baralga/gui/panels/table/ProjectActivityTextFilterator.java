//---------------------------------------------------------
// $Id$ 
// 
// (c) 2011 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.baralga.gui.panels.table;

import java.util.List;

import org.remast.baralga.FormatUtils;
import org.remast.baralga.model.ProjectActivity;

import ca.odell.glazedlists.TextFilterator;

/**
 * Prepares the filter strings for quick filtering project activities.
 * @author remast
 */
public class ProjectActivityTextFilterator implements TextFilterator<ProjectActivity>  {

	@Override
	public void getFilterStrings(List<String> baseList, ProjectActivity activity) {
		if (baseList == null || activity == null) {
			return;
		}
		
		baseList.add(activity.getProject().getTitle());
		baseList.add(FormatUtils.formatDay(activity.getStart()));
		baseList.add(activity.getDescription());
		baseList.add(FormatUtils.formatTime(activity.getStart()));
		baseList.add(FormatUtils.formatTime(activity.getEnd()));
		baseList.add(FormatUtils.DURATION_FORMAT.format(activity.getDuration()));
	}

}
