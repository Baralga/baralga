package org.remast.baralga.gui.dialogs;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import org.remast.baralga.model.Project;

public class ProjectRemovedEvent extends Event {
    private DefaultEventTableModel<Project> projectTableModel;

    public int getType() {
        return BaralgaEvent.PROJECT_REMOVED;
    }

    @Override
    public void fireTableDataChanged() {
        projectTableModel.fireTableDataChanged();
    }
}
