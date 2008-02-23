package org.remast.baralga.model.report;

import java.util.List;
import java.util.Observable;
import java.util.Vector;

import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class FilteredReport extends Observable {

    /** The data of the report. */
    private ProTrack data;

    /** Accumulated activities of the report. */
    private EventList<AccumulatedProjectActivity> accumulatedActivitiesByDay;

    /** The filter by which the tracked data is filtered. */
    protected Filter filter;

    /**
     * Create report from data.
     */
    public FilteredReport(final ProTrack data) {
        this.data = data;
        accumulatedActivitiesByDay = new BasicEventList<AccumulatedProjectActivity>();

        accumulate();
    }

    @Override
    public String toString() {
        String result = ""; //$NON-NLS-1$

        // accumulate activities for every day
        for (AccumulatedProjectActivity activity : accumulatedActivitiesByDay) {
            result += activity.toString() + ":"; //$NON-NLS-1$
        }

        return "[" + result + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Add given activity to the accumulated report.
     * @param activity
     *            the activity to be added
     */
    public void acummulateActivity(final ProjectActivity activity) {
        AccumulatedProjectActivity newAccActivity = new AccumulatedProjectActivity(activity.getProject(), activity
                .getStart(), activity.getDuration());
        if(filter != null && !filter.satisfiesPredicates(activity))
            return;

        if (this.accumulatedActivitiesByDay.contains(newAccActivity)) {
            AccumulatedProjectActivity accActivity = this.accumulatedActivitiesByDay.get(accumulatedActivitiesByDay
                    .indexOf(newAccActivity));
            accActivity.addTime(newAccActivity.getTime());
        } else {
            this.accumulatedActivitiesByDay.add(newAccActivity);
        }
    }

    /** Utility method for accumulating. */
    protected void accumulate() {
        this.accumulatedActivitiesByDay.clear();

        List<ProjectActivity> filteredActivities = getFilteredActivities();
        for (ProjectActivity activity : filteredActivities) {
            this.acummulateActivity(activity);
        }
    }

    /**
     * Get all filtered acitivies.
     * @return all activies after applying the filter.
     */
    private List<ProjectActivity> getFilteredActivities() {
        List<ProjectActivity> filteredActivitiesList = new Vector<ProjectActivity>();

        if (filter != null)
            filteredActivitiesList.addAll(filter.applyFilters(this.data.getActivities()));
        else
            filteredActivitiesList.addAll(this.data.getActivities());

        return filteredActivitiesList;
    }

    /**
     * @return the accumulatedActivitiesByDay
     */
    public EventList<AccumulatedProjectActivity> getAccumulatedActivitiesByDay() {
        return accumulatedActivitiesByDay;
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @param filter
     *            the filter to set
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
        accumulate();
    }
}
