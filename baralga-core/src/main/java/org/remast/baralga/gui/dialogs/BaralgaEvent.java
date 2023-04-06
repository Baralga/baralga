package org.remast.baralga.gui.dialogs;

import ca.odell.glazedlists.swing.DefaultEventTableModel;
import org.remast.baralga.model.Project;

public class BaralgaEvent extends Event {
    public static final int PROJECT_CHANGED = 1;
    public static final int PROJECT_REMOVED = 2;

    private DefaultEventTableModel<Project> projectTableModel;

    private int type;

    public BaralgaEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void fireTableDataChanged() {
        projectTableModel.fireTableDataChanged();
    }


}
