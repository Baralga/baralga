package org.remast.baralga.gui.lists;

import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.swing.util.LabeledItem;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

public class ProjectFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ProjectFilterList.class);

	/** The model. */
	private final PresentationModel model;
	
	public static final int ALL_PROJECTS_DUMMY_VALUE = -10;

	public static final Project ALL_PROJECTS_DUMMY = new Project(ALL_PROJECTS_DUMMY_VALUE, "*", "*"); //$NON-NLS-1$ //$NON-NLS-2$

	public static final LabeledItem<Project> ALL_PROJECTS_FILTER_ITEM = new LabeledItem<Project>(ALL_PROJECTS_DUMMY, textBundle.textFor("ProjectFilterList.AllProjectsLabel")); //$NON-NLS-1$

	private final EventList<LabeledItem<Project>> projectList;

	public ProjectFilterList(final PresentationModel model) {
		this.model = model;
		this.projectList = new BasicEventList<LabeledItem<Project>>();
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

	public SortedList<LabeledItem<Project>> getProjectList() {
		return new SortedList<LabeledItem<Project>>(this.projectList);
	}

	public void update(final Observable source, final Object eventObject) {
		if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
			return;
		}

		final BaralgaEvent event = (BaralgaEvent) eventObject;

		switch (event.getType()) {

		case BaralgaEvent.PROJECT_ADDED:
			this.addProject((Project) event.getData());
			break;

		case BaralgaEvent.PROJECT_REMOVED:
			this.removeProject((Project) event.getData());
			break;
		}
	}

	private void addProject(final Project project) {
		if (project != null ) {
		    final LabeledItem<Project> filterItem = new LabeledItem<Project>( project );
		    if(!this.projectList.contains(filterItem)) {
		        this.projectList.add(new LabeledItem<Project>(project));
		    }
		}
	}

	private void removeProject(final Project project) {
	    if (project != null) {
    		final LabeledItem<Project> filterItem = new LabeledItem<Project>(project);
    		if (this.projectList.contains(filterItem)) {
    			this.projectList.remove(filterItem);
    		}
	    }
	}
}
