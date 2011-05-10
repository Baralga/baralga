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
 * Report for the working hours by month.
 * @author remast
 */
public class HoursByMonthReport extends Observable implements Observer  {

    /** The model. */
    private final PresentationModel model;

    private final SortedList<HoursByMonth> hoursByMonthList;

    public HoursByMonthReport(final PresentationModel model) {
        this.model = model;
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
        final DateTime dateTime = activity.getStart();

        final HoursByMonth newHoursByMonth = new HoursByMonth(dateTime, activity.getDuration());

        if (this.hoursByMonthList.contains(newHoursByMonth)) {
            HoursByMonth hoursByMonth = this.hoursByMonthList.get(hoursByMonthList.indexOf(newHoursByMonth));
            hoursByMonth.addHours(newHoursByMonth.getHours());
        } else {
            this.hoursByMonthList.add(newHoursByMonth);
        }

    }

    public SortedList<HoursByMonth> getHoursByMonth() {
        return hoursByMonthList;
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
