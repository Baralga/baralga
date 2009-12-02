package org.remast.baralga.gui.model.report;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

/**
 * Report for the working hours by project.
 * @author remast
 */
public class HoursByProjectReport extends Observable implements Observer  {

    /** The model. */
    private PresentationModel model;

    private EventList<HoursByProject> hoursByProjectList;

    private Filter filter;

    /**
     * @param filter
     *            the filter to set
     */
    private void setFilter(final Filter filter) {
        this.filter = filter;

        calculateHours();
    }

    public HoursByProjectReport(final PresentationModel model) {
        this.model = model;
        this.filter = model.getFilter();
        this.model.addObserver(this);
        this.hoursByProjectList = new SortedList<HoursByProject>(new BasicEventList<HoursByProject>());

        calculateHours();
    }

    public void calculateHours() {
        this.hoursByProjectList.clear();

        for (ProjectActivity activity : this.model.getActivitiesList()) {
            this.addHours(activity);
        }
    }

    public void addHours(final ProjectActivity activity) {
        if (filter != null && !filter.matchesCriteria(activity)) {
            return;
        }

        final HoursByProject newHoursByProject = new HoursByProject(activity.getProject(), activity.getDuration());

        if (this.hoursByProjectList.contains(newHoursByProject)) {
            HoursByProject HoursByProject = this.hoursByProjectList.get(hoursByProjectList.indexOf(newHoursByProject));
            HoursByProject.addHours(newHoursByProject.getHours());
        } else {
            this.hoursByProjectList.add(newHoursByProject);
        }

    }

    public EventList<HoursByProject> getHoursByProject() {
        return hoursByProjectList;
    }

    /**
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;
        switch (event.getType()) {

            case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
                final Collection<ProjectActivity> projectActivities = (Collection<ProjectActivity>) event.getData();
                for (ProjectActivity projectActivity : projectActivities) {
                    addHours(projectActivity);
                }
                break;

            case BaralgaEvent.DATA_CHANGED:
            case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
                calculateHours();
                break;

            case BaralgaEvent.FILTER_CHANGED:
                final Filter newFilter = (Filter) event.getData();
                setFilter(newFilter);
                break;
        }

        setChanged();
        notifyObservers();
    }

}
