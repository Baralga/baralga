package org.remast.baralga.gui.dialogs.table;

import java.beans.PropertyChangeEvent;

import org.remast.baralga.gui.dialogs.ManageProjectsDialog;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.gui.WritableTableFormat;

/**
 * Format for table containing the projects.
 * @author remast
 */
public class ProjectListTableFormat implements WritableTableFormat<Project> {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ManageProjectsDialog.class);

    /** The model. */
    private PresentationModel model;

    public ProjectListTableFormat(final PresentationModel model) {
        this.model = model;
    }

    public boolean isEditable(final Project project, final int column) {
        return true;
    }

    public Project setColumnValue(final Project project, final Object value, final int column) {
        switch (column) {
        case 0:
            final String oldTitle = project.getTitle();
            final String newTitle = (String) value;
            project.setTitle(newTitle);

            {
                // Fire event
                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(project, Project.PROPERTY_TITLE, oldTitle, newTitle);
                model.fireProjectChangedEvent(project, propertyChangeEvent);
            }
            break;

        case 1:
            final Boolean oldActive = project.isActive();
            final Boolean newActive = (Boolean) value;
            project.setActive(newActive);

            {
                // Fire event
                final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(project, Project.PROPERTY_ACTIVE, oldActive, newActive);
                model.fireProjectChangedEvent(project, propertyChangeEvent);
            }
            break;

        default:
            break;
        }

        return project;
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(final int column) {
        switch (column) {

        case 0:
            return textBundle.textFor("ProjectListTableFormat.TitleHeading");

        case 1:
            return textBundle.textFor("ProjectListTableFormat.ActiveHeading");

        default:
            return null;
        }
    }

    public Object getColumnValue(final Project project, final int column) {
        switch (column) {

        case 0:
            return project.getTitle();

        case 1:
            return new Boolean(project.isActive());

        default:
            return null;
        }
    }

}
