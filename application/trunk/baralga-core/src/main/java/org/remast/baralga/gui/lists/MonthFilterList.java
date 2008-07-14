package org.remast.baralga.gui.lists;

import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class MonthFilterList implements Observer {

    public static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM"); //$NON-NLS-1$

    /** The model. */
    private final PresentationModel model;

    public static final String ALL_MONTHS_DUMMY = "*"; //$NON-NLS-1$

    public static final FilterItem<String> ALL_MONTHS_FILTER_ITEM = new FilterItem<String>(ALL_MONTHS_DUMMY, Messages
            .getString("MonthFilterList.AllMonthsLabel")); //$NON-NLS-1$

    private EventList<FilterItem<String>> monthList;

    public MonthFilterList(final PresentationModel model) {
        this.model = model;
        this.monthList = new BasicEventList<FilterItem<String>>();

        this.model.addObserver(this);

        initialize();
    }

    private void initialize() {
        this.monthList.clear();
        this.monthList.add(ALL_MONTHS_FILTER_ITEM);

        for (ProjectActivity activity : this.model.getData().getActivities()) {
            this.addMonth(activity);
        }
    }

    public EventList<FilterItem<String>> getMonthList() {
        return this.monthList;
    }

    public void update(final Observable source, final Object eventObject) {
        if (eventObject != null && eventObject instanceof BaralgaEvent) {
            final BaralgaEvent event = (BaralgaEvent) eventObject;

            switch (event.getType()) {

                case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
                    this.addMonth((ProjectActivity) event.getData());
                    break;

                case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
                    this.initialize();
                    break;
            }
        }
    }

    private void addMonth(final ProjectActivity activity) {
        final FilterItem<String> month = new FilterItem<String>(MONTH_FORMAT.format(activity.getStart()));
        if (!this.monthList.contains(month))
            this.monthList.add(month);
    }
}
