package org.remast.baralga.gui.model.report;

import java.util.Collection;
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
 * Report for the working hours by month.
 * @author remast
 */
public class HoursByMonthReport extends Observable implements Observer  {

    /** The model. */
    private final PresentationModel model;

    private final EventList<HoursByMonth> hoursByMonthList;

    private Filter filter;

    /**
     * @param filter
     *            the filter to set
     */
    private void setFilter(final Filter filter) {
        this.filter = filter;

        calculateHours();
    }

    public HoursByMonthReport(final PresentationModel model) {
        this.model = model;
        this.filter = model.getFilter();
        this.model.addObserver(this);
        this.hoursByMonthList = new SortedList<HoursByMonth>(new BasicEventList<HoursByMonth>());

        calculateHours();
    }

    public void calculateHours() {
        this.hoursByMonthList.clear();

        for (ProjectActivity activity : this.model.getActivitiesList()) {
            this.addHours(activity);
        }
    }

    public void addHours(final ProjectActivity activity) {
        if (filter != null && !filter.matchesCriteria(activity)) {
            return;
        }

        final DateTime dateTime = activity.getStart();

        final HoursByMonth newHoursByMonth = new HoursByMonth(dateTime.getMonthOfYear(), activity.getDuration());

        if (this.hoursByMonthList.contains(newHoursByMonth)) {
            HoursByMonth hoursByMonth = this.hoursByMonthList.get(hoursByMonthList.indexOf(newHoursByMonth));
            hoursByMonth.addHours(newHoursByMonth.getHours());
        } else {
            this.hoursByMonthList.add(newHoursByMonth);
        }

    }

    public EventList<HoursByMonth> getHoursByMonth() {
        return hoursByMonthList;
    }

    /**
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (eventObject != null && eventObject instanceof BaralgaEvent) {
            final BaralgaEvent event = (BaralgaEvent) eventObject;
            switch (event.getType()) {

                case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
                    final Collection<ProjectActivity> projectActivities = (Collection<ProjectActivity>) event.getData();
                    for (ProjectActivity projectActivity : projectActivities) {
                        addHours(projectActivity);
                    }
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
