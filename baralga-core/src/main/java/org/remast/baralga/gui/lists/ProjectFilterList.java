package org.remast.baralga.gui.lists;

import java.util.Collection;
import java.util.UUID;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.util.LabeledItem;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

import com.google.common.eventbus.Subscribe;

/**
 * The list containing all projects available for the filter.
 * @author remast
 * TODO: Enhance so that only projects occur in list that there are activities for.
 */
public class ProjectFilterList {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ProjectFilterList.class);

    /** The model. */
    private final PresentationModel model;

    public static final String ALL_PROJECTS_DUMMY_VALUE = new UUID(0,0).toString();

    public static final Project ALL_PROJECTS_DUMMY = new Project(ALL_PROJECTS_DUMMY_VALUE, "*", "*"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final LabeledItem<Project> ALL_PROJECTS_FILTER_ITEM = new LabeledItem<>(ALL_PROJECTS_DUMMY, textBundle.textFor("ProjectFilterList.AllProjectsLabel")); //$NON-NLS-1$

    /** The actual list containing all projects. */
    private final EventList<LabeledItem<Project>> projectList;

    /**
     * Creates a new list for the given model.
     * @param model the model to create list for
     */
    public ProjectFilterList(final PresentationModel model) {
        this.model = model;
        this.projectList = new BasicEventList<>();
        this.model.getEventBus().register(this);

        initialize();
    }

    /**
     * Initializes the list with all projects from model.
     */
    private void initialize() {
        this.projectList.clear();
        this.projectList.add(ALL_PROJECTS_FILTER_ITEM);

        for (Project project : this.model.getDAO().getAllProjects()) {
            this.addProject(project);
        }
    }

    public SortedList<LabeledItem<Project>> getProjectList() {
        return new SortedList<>(this.projectList);
    }

    @SuppressWarnings("unchecked")
    @Subscribe public void update(final Object eventObject) {
        if (!(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;

        switch (event.getType()) {

        case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
            final Collection<ProjectActivity> projectActivities = (Collection<ProjectActivity>) event.getData();
            for (ProjectActivity projectActivity : projectActivities) {
                this.addProject(projectActivity.getProject());
            }
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            this.initialize();
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
            this.initialize();
            break;

        case BaralgaEvent.DATA_CHANGED:
        case BaralgaEvent.PROJECT_CHANGED:
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

        final LabeledItem<Project> filterItem = new LabeledItem<>(project);
        if (!this.projectList.contains(filterItem)) {
            this.projectList.add(new LabeledItem<>(project));
        }
    }

}
