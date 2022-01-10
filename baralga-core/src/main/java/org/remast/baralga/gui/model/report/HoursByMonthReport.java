package org.remast.baralga.gui.model.report;

import org.joda.time.DateTime;
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
 * Report for the working hours by month.
 * @author remast
 */
public class HoursByMonthReport {

    /** The model. */
    private final PresentationModel model;
    
    /** The bus to publish changes of the report. */
    private EventBus eventBus = new EventBus();

    private final SortedList<HoursByMonth> hoursByMonthList;

    public HoursByMonthReport(final PresentationModel model) {
        this.model = model;
        this.model.getEventBus().register(this);
        this.hoursByMonthList = new SortedList<>(new BasicEventList<>());

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
        final List<HoursByMonth> hoursByMonths = new ArrayList<>();
        for (ProjectActivity activity : this.model.getActivitiesList()) {
            this.addHours(hoursByMonths, activity);
        }

        this.hoursByMonthList.clear();
        this.hoursByMonthList.addAll(hoursByMonths);
    }

    static void addHours(final List<HoursByMonth> hoursByMonths, final ProjectActivity activity) {
        final DateTime dateTime = activity.getStart();

        final HoursByMonth newHoursByMonth = new HoursByMonth(dateTime, activity.getDuration());

        if (hoursByMonths.contains(newHoursByMonth)) {
            HoursByMonth hoursByMonth = hoursByMonths.get(hoursByMonths.indexOf(newHoursByMonth));
            hoursByMonth.addHours(newHoursByMonth.getHours());
        } else {
            hoursByMonths.add(newHoursByMonth);
        }

    }

    public SortedList<HoursByMonth> getHoursByMonth() {
        return hoursByMonthList;
    }

    @Subscribe
    public void update(final Object eventObject) {
        if (eventObject instanceof BaralgaEvent) {
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

}
