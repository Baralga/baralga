package org.remast.baralga.gui.lists;

import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.util.LabeledItem;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

/**
 * The list containing all projects available for the filter.
 * @author remast
 * TODO: Enhance so that only projects occur in list that there are activities for.
 */
public class ProjectFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ProjectFilterList.class);

    /** The model. */
    private final PresentationModel model;

    public static final int ALL_PROJECTS_DUMMY_VALUE = -10;

    public static final Project ALL_PROJECTS_DUMMY = new Project(ALL_PROJECTS_DUMMY_VALUE, "*", "*"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final LabeledItem<Project> ALL_PROJECTS_FILTER_ITEM = new LabeledItem<Project>(ALL_PROJECTS_DUMMY, textBundle.textFor("ProjectFilterList.AllProjectsLabel")); //$NON-NLS-1$

    /** The actual list containing all projects. */
    private final EventList<LabeledItem<Project>> projectList;

    /**
     * Creates a new list for the given model.
     * @param model the model to create list for
     */
    public ProjectFilterList(final PresentationModel model) {
        this.model = model;
        this.projectList = new BasicEventList<LabeledItem<Project>>();
        this.model.addObserver(this);

        initialize();
    }

    /**
     * Initializes the list with all projects from model.
     */
    private void initialize() {
        this.projectList.clear();
        this.projectList.add(ALL_PROJECTS_FILTER_ITEM);

        // Get project from filter
        final Long filterProjectId = UserSettings.instance().getFilterSelectedProjectId();
        boolean filterProjectFound = false;

        for (ProjectActivity projectActivity : this.model.getData().getActivities()) {
            final Project project = projectActivity.getProject();
            
            this.addProject(project);

            if (filterProjectId != null && project.getId() == filterProjectId) {
                filterProjectFound = true;
            }
        }

        // Add project from filter if not already in list.
        if (filterProjectId != null && filterProjectId > 0 && !filterProjectFound) {
            final Project filterProject = this.model.getData().findProjectById(filterProjectId);

            if (filterProject != null) {
                this.addProject(filterProject);
            }
        }
    }

    public SortedList<LabeledItem<Project>> getProjectList() {
        return new SortedList<LabeledItem<Project>>(this.projectList);
    }

    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;

        switch (event.getType()) {

        case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
            final ProjectActivity projectActivity = (ProjectActivity) event.getData();
            this.addProject(projectActivity.getProject());
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            this.initialize();
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
            this.initialize();
            break;
        }
    }

    /**
     * Adds the given project to the list.
     * @param project the project to be added
     */
    private void addProject(final Project project) {
        if (project == null) {
            return;
        }

        final LabeledItem<Project> filterItem = new LabeledItem<Project>(project);
        if (!this.projectList.contains(filterItem)) {
            this.projectList.add(new LabeledItem<Project>(project));
        }
    }

    /**
     * Removes the given project from the list.
     * @param project the project to be removed
     */
    private void removeProject(final Project project) {
        if (project == null) {
            return;
        }

        final LabeledItem<Project> filterItem = new LabeledItem<Project>(project);
        if (this.projectList.contains(filterItem)) {
            this.projectList.remove(filterItem);
        }
    }
}
