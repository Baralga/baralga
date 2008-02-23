package org.remast.baralga.model.report;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.joda.time.DateTime;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class HoursByWeekReport extends Observable implements Observer  {

    /** The model. */
    private PresentationModel model;

    private EventList<HoursByWeek> hoursByWeekList;

    private Filter filter;

    /**
     * @param filter
     *            the filter to set
     */
    public void setFilter(final Filter filter) {
        this.filter = filter;

        calculateHours();
    }

    public HoursByWeekReport(final PresentationModel model) {
        this.model = model;
        this.filter = model.getFilter();
        this.model.addObserver(this);
        this.hoursByWeekList = new BasicEventList<HoursByWeek>();

        calculateHours();
    }

    public void calculateHours() {
        this.hoursByWeekList.clear();

        for (ProjectActivity activity : getFilteredActivities()) {
            this.addHours(activity);
        }
    }

    public void addHours(final ProjectActivity activity) {
        if (filter != null && !filter.satisfiesPredicates(activity)) {
            return;
        }

        final DateTime dateTime = new DateTime(activity.getStart());

        final HoursByWeek newHoursByWeek = new HoursByWeek(dateTime.getWeekOfWeekyear(), activity.getDuration());

        if (this.hoursByWeekList.contains(newHoursByWeek)) {
            HoursByWeek hoursByWeek = this.hoursByWeekList.get(hoursByWeekList.indexOf(newHoursByWeek));
            hoursByWeek.addHours(newHoursByWeek.getHours());
        } else {
            this.hoursByWeekList.add(newHoursByWeek);
        }

    }

    public EventList<HoursByWeek> getHoursByWeek() {
        return hoursByWeekList;
    }

    /**
     * Get all filtered acitivies.
     * 
     * @return all activies after applying the filter.
     */
    private List<ProjectActivity> getFilteredActivities() {
        final List<ProjectActivity> filteredActivitiesList = new Vector<ProjectActivity>();

        if (filter != null) {
            filteredActivitiesList.addAll(filter.applyFilters(this.model.getActivitiesList()));
        } else {
            filteredActivitiesList.addAll(this.model.getActivitiesList());
        }
        return filteredActivitiesList;
    }

    public void update(Observable source, Object eventObject) {
        if (eventObject != null && eventObject instanceof ProTrackEvent) {
            final ProTrackEvent event = (ProTrackEvent) eventObject;
            switch (event.getType()) {

                case ProTrackEvent.PROJECT_ACTIVITY_ADDED:
                    ProjectActivity activity = (ProjectActivity) event.getData();
                    addHours(activity);
                    break;

                case ProTrackEvent.PROJECT_ACTIVITY_REMOVED:
                    calculateHours();
                    break;

                case ProTrackEvent.PROJECT_ACTIVITY_CHANGED:
                    // TODO: Replace calculation by remove + add.
                    calculateHours();
                    break;
            }
            setChanged();
            notifyObservers();
        }
    }

}
