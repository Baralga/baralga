package org.remast.baralga.gui.dialogs;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import org.remast.baralga.model.Project;

public class ProjectChangedEvent extends Event {
    private DefaultEventTableModel<Project> projectTableModel;

    public int getType() {
        return BaralgaEvent.PROJECT_CHANGED;
    }

    @Override
    public void fireTableDataChanged() {
        projectTableModel.fireTableDataChanged();
    }
}
