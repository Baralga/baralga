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
 * Report for the working hours by day.
 * @author remast
 */
public class HoursByDayReport extends Observable implements Observer  {

    /** The model. */
    private PresentationModel model;

    private EventList<HoursByDay> hoursByDayList;

    private Filter filter;

    /**
     * @param filter
     *            the filter to set
     */
    private void setFilter(final Filter filter) {
        this.filter = filter;

        calculateHours();
    }

    public HoursByDayReport(final PresentationModel model) {
        this.model = model;
        this.filter = model.getFilter();
        this.model.addObserver(this);
        this.hoursByDayList = new SortedList<HoursByDay>(new BasicEventList<HoursByDay>());

        calculateHours();
    }

    public void calculateHours() {
        this.hoursByDayList.clear();

        for (ProjectActivity activity : this.model.getActivitiesList()) {
            this.addHours(activity);
        }
    }

    public void addHours(final ProjectActivity activity) {
        if (filter != null && !filter.matchesCriteria(activity)) {
            return;
        }

        final HoursByDay newHoursByDay = new HoursByDay(activity.getStart(), activity.getDuration());

        if (this.hoursByDayList.contains(newHoursByDay)) {
            HoursByDay HoursByDay = this.hoursByDayList.get(hoursByDayList.indexOf(newHoursByDay));
            HoursByDay.addHours(newHoursByDay.getHours());
        } else {
            this.hoursByDayList.add(newHoursByDay);
        }

    }

    public EventList<HoursByDay> getHoursByDay() {
        return hoursByDayList;
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
