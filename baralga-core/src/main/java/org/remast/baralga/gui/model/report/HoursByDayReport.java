package org.remast.baralga.gui.model.report;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Report for the working hours by day.
 * @author remast
 */
public class HoursByDayReport {

    /** The model. */
    private PresentationModel model;

    /** The bus to publish changes of the report. */
    private EventBus eventBus = new EventBus();

    private SortedList<HoursByDay> hoursByDayList;

    public HoursByDayReport(final PresentationModel model) {
        this.model = model;
        this.model.getEventBus().register(this);
        this.hoursByDayList = new SortedList<>(new BasicEventList<>());

        calculateHours();
    }
    
    /**
     * Getter for the event bus.
     * @return the event bus
     */
    public EventBus getEventBus() {
    	return eventBus;
    }

    public void calculateHours() {
        final List<HoursByDay> hoursByDays = new ArrayList<>();
        for (ProjectActivity activity : this.model.getActivitiesList()) {
            this.addHours(hoursByDays, activity);
        }

        this.hoursByDayList.clear();
        this.hoursByDayList.addAll(hoursByDays);
    }

    static void addHours(final List<HoursByDay> hoursByDays, final ProjectActivity activity) {
        final HoursByDay newHoursByDay = new HoursByDay(activity.getStart(), activity.getDuration());

        if (hoursByDays.contains(newHoursByDay)) {
            HoursByDay HoursByDay = hoursByDays.get(hoursByDays.indexOf(newHoursByDay));
            HoursByDay.addHours(newHoursByDay.getHours());
        } else {
            hoursByDays.add(newHoursByDay);
        }
    }

    public SortedList<HoursByDay> getHoursByDay() {
        return hoursByDayList;
    }

    @Subscribe
    public void update(final Object eventObject) {
        if (!(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;
        switch (event.getType()) {

            case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
            case BaralgaEvent.DATA_CHANGED:
            case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
            case BaralgaEvent.FILTER_CHANGED:
            case BaralgaEvent.PROJECT_REMOVED:
               calculateHours();
               break;
        }

        eventBus.post(this);
    }

}
