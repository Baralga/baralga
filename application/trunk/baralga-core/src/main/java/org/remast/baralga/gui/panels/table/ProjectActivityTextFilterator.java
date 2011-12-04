//---------------------------------------------------------
// $Id$ 
// 
// (c) 2011 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
package org.remast.baralga.gui.panels.table;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;

import org.remast.baralga.FormatUtils;
import org.remast.baralga.model.ProjectActivity;

import ca.odell.glazedlists.TextFilterator;

public class ProjectActivityTextFilterator implements TextFilterator<ProjectActivity>  {

	@Override
	public void getFilterStrings(List<String> baseList, ProjectActivity activity) {
		baseList.add(activity.getProject().getTitle());
		baseList.add(DateFormat.getInstance().format(activity.getStart().toDate()));
		baseList.add(activity.getDescription());
		baseList.add(FormatUtils.formatTime(activity.getStart()));
		baseList.add(FormatUtils.formatTime(activity.getEnd()));
		baseList.add(NumberFormat.getNumberInstance().format(activity.getDuration()));
	}

}
