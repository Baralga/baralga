package org.remast.baralga.gui.model.report;

import java.util.Observable;
import java.util.Observer;

import org.joda.time.DateTime;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

/**
 * Report for the working hours by week.
 * @author remast
 */
public class HoursByWeekReport extends Observable implements Observer  {

    /** The model. */
    private final PresentationModel model;

    private final SortedList<HoursByWeek> hoursByWeekList;

    public HoursByWeekReport(final PresentationModel model) {
        this.model = model;
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
        final DateTime dateTime = activity.getStart();

        final HoursByWeek newHoursByWeek = new HoursByWeek(dateTime, activity.getDuration());

        if (this.hoursByWeekList.contains(newHoursByWeek)) {
            HoursByWeek hoursByWeek = this.hoursByWeekList.get(hoursByWeekList.indexOf(newHoursByWeek));
            hoursByWeek.addHours(newHoursByWeek.getHours());
        } else {
            this.hoursByWeekList.add(newHoursByWeek);
        }

    }

    public SortedList<HoursByWeek> getHoursByWeek() {
        return hoursByWeekList;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void update(final Observable source, final Object eventObject) {
        if (eventObject != null && eventObject instanceof BaralgaEvent) {
            final BaralgaEvent event = (BaralgaEvent) eventObject;
            switch (event.getType()) {

                case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
                case BaralgaEvent.DATA_CHANGED:
                case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
                case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
                case BaralgaEvent.FILTER_CHANGED:
                    calculateHours();
                    break;
            }
            setChanged();
            notifyObservers();
        }
    }

}
