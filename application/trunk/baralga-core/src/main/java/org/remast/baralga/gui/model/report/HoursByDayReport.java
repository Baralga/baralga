package org.remast.baralga.gui.model.report;

import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

/**
 * Report for the working hours by day.
 * @author remast
 */
public class HoursByDayReport extends Observable implements Observer  {

    /** The model. */
    private PresentationModel model;

    private SortedList<HoursByDay> hoursByDayList;

    public HoursByDayReport(final PresentationModel model) {
        this.model = model;
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
        final HoursByDay newHoursByDay = new HoursByDay(activity.getStart(), activity.getDuration());

        if (this.hoursByDayList.contains(newHoursByDay)) {
            HoursByDay HoursByDay = this.hoursByDayList.get(hoursByDayList.indexOf(newHoursByDay));
            HoursByDay.addHours(newHoursByDay.getHours());
        } else {
            this.hoursByDayList.add(newHoursByDay);
        }

    }

    public SortedList<HoursByDay> getHoursByDay() {
        return hoursByDayList;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

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
