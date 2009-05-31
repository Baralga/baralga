package org.remast.baralga.gui.model.report;

import java.util.Observable;
import java.util.Observer;

import org.joda.time.DateTime;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

/**
 * Report for the working hours by week.
 * @author remast
 */
public class HoursByWeekReport extends Observable implements Observer  {

    /** The model. */
    private final PresentationModel model;

    private final EventList<HoursByWeek> hoursByWeekList;

    private Filter filter;

    /**
     * @param filter
     *            the filter to set
     */
    private void setFilter(final Filter filter) {
        this.filter = filter;

        calculateHours();
    }

    public HoursByWeekReport(final PresentationModel model) {
        this.model = model;
        this.filter = model.getFilter();
        this.model.addObserver(this);
        this.hoursByWeekList = new SortedList<HoursByWeek>(new BasicEventList<HoursByWeek>());

        calculateHours();
    }

    public void calculateHours() {
        this.hoursByWeekList.clear();

        for (ProjectActivity activity : this.model.getActivitiesList()) {
            this.addHours(activity);
        }
    }

    public void addHours(final ProjectActivity activity) {
        if (filter != null && !filter.matchesCriteria(activity)) {
            return;
        }

        final DateTime dateTime = activity.getStart();

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
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (eventObject != null && eventObject instanceof BaralgaEvent) {
            final BaralgaEvent event = (BaralgaEvent) eventObject;
            switch (event.getType()) {

                case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
                    ProjectActivity activity = (ProjectActivity) event.getData();
                    addHours(activity);
                    break;

                case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
                    calculateHours();
                    break;
                    
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

}
