package org.remast.baralga.gui.dialogs.table;

import java.beans.PropertyChangeEvent;

import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;

import ca.odell.glazedlists.gui.WritableTableFormat;

public class ProjectListTableFormat implements WritableTableFormat<Project> {

    /** The model. */
    private PresentationModel model;

    public ProjectListTableFormat(final PresentationModel model) {
        this.model = model;
    }

        @Override
        public boolean isEditable(Project arg0, int arg1) {
            return arg1 == 0;
        }

        @Override
        public Project setColumnValue(Project project, Object arg1,
                int arg2) {
            if (arg2 == 0) {
                final String oldTitle = project.getTitle();
                final String newTitle = (String) arg1;
                project.setTitle(newTitle);

                // Fire event
                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(project, Project.PROPERTY_TITLE, oldTitle, newTitle);
                model.fireProjectChangedEvent(project, propertyChangeEvent);
            }
            return null;
        }

        @Override
        public int getColumnCount() {
            // TODO Auto-generated method stub
            return 1;
        }

        @Override
        public String getColumnName(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getColumnValue(Project arg0, int arg1) {
            // TODO Auto-generated method stub
            return arg0.getTitle();
        }

}
