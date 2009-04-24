package org.remast.baralga.gui.dialogs.table;

import java.beans.PropertyChangeEvent;

import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;

import ca.odell.glazedlists.gui.WritableTableFormat;

/**
 * Format for table containing the projects.
 * @author remast
 */
public class ProjectListTableFormat implements WritableTableFormat<Project> {

    /** The model. */
    private PresentationModel model;

    public ProjectListTableFormat(final PresentationModel model) {
        this.model = model;
    }

    public boolean isEditable(final Project project, final int column) {
        return column == 0;
    }

    public Project setColumnValue(final Project project, final Object value,
            final int column) {
        if (column == 0) {
            final String oldTitle = project.getTitle();
            final String newTitle = (String) value;
            project.setTitle(newTitle);

            // Fire event
            final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(project, Project.PROPERTY_TITLE, oldTitle, newTitle);
            model.fireProjectChangedEvent(project, propertyChangeEvent);
        }
        
        return project;
    }

    public int getColumnCount() {
        return 1;
    }

    public String getColumnName(final int column) {
        return null;
    }

    public Object getColumnValue(final Project project, final int column) {
        return project.getTitle();
    }

}
