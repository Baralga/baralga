package org.remast.baralga.gui.lists;

import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.Messages;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class ProjectFilterList implements Observer {

    /** The model. */
    private PresentationModel model;

    public static final Project ALL_PROJECTS_DUMMY = new Project(0, "*", "*"); //$NON-NLS-1$ //$NON-NLS-2$
    
    public static final FilterItem<Project> ALL_PROJECTS_FILTER_ITEM = new FilterItem<Project>(ALL_PROJECTS_DUMMY, Messages.getString("ProjectFilterList.AllProjectsLabel")); //$NON-NLS-1$

    private EventList<FilterItem<Project>> projectList;

    public ProjectFilterList(final PresentationModel model) {
        this.model = model;
        this.projectList = new BasicEventList<FilterItem<Project>>();
        this.model.addObserver(this);

        initialize();
    }

    private void initialize() {
        this.projectList.clear();
        this.projectList.add(ALL_PROJECTS_FILTER_ITEM);
        
        for (Project activity : this.model.getData().getProjects()) {
            this.addProject(activity);
        }
    }

    public EventList<FilterItem<Project>> getProjectList() {
        return this.projectList;
    }

    public void update(Observable source, Object eventObject) {
        if (eventObject != null && eventObject instanceof ProTrackEvent) {
            ProTrackEvent event = (ProTrackEvent) eventObject;

            switch (event.getType()) {

            case ProTrackEvent.PROJECT_ADDED:
                this.addProject((Project) event.getData());
                break;

            case ProTrackEvent.PROJECT_REMOVED:
                this.removeProject((Project) event.getData());
                break;
            }
        }
    }

    private void addProject(final Project project) {
        if (project != null && !this.projectList.contains(project)) {
            this.projectList.add(new FilterItem<Project>(project));
        }
    }

    private void removeProject(final Project project) {
        if (project != null && this.projectList.contains(project)) {
            this.projectList.remove(project);
        }
    }
}
