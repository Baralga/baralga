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
import ca.odell.glazedlists.SortedList;

public class YearFilterList implements Observer {

    public static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy"); //$NON-NLS-1$

    /** The model. */
    private final PresentationModel model;

    public static final int ALL_YEARS_DUMMY = -1; //$NON-NLS-1$

    public static final FilterItem<Integer> ALL_YEARS_FILTER_ITEM = new FilterItem<Integer>(ALL_YEARS_DUMMY, Messages
            .getString("YearFilterList.AllYearsLabel")); //$NON-NLS-1$

    private EventList<FilterItem<Integer>> yearList;

    public YearFilterList(final PresentationModel model) {
        this.model = model;
        this.yearList = new BasicEventList<FilterItem<Integer>>();
        this.model.addObserver(this);

        initialize();
    }

    private void initialize() {
        this.yearList.clear();
        this.yearList.add(ALL_YEARS_FILTER_ITEM);
        for (ProjectActivity activity : this.model.getData().getActivities()) {
            this.addYear(activity);
        }
    }

    public SortedList<FilterItem<Integer>> getYearList() {
        return new SortedList<FilterItem<Integer>>(this.yearList);
    }

    public void update(final Observable source, final Object eventObject) {
        if (eventObject != null && eventObject instanceof BaralgaEvent) {
            final BaralgaEvent event = (BaralgaEvent) eventObject;

            switch (event.getType()) {

                case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
                    ProjectActivity activity = (ProjectActivity) event.getData();
                    this.addYear(activity);
                    break;

                case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
                    this.initialize();
                    break;
            }
        }
    }

    private void addYear(final ProjectActivity activity) {
    	final String year = YEAR_FORMAT.format(activity.getStart());
        final FilterItem<Integer> yearItem = new FilterItem<Integer>(Integer.parseInt(year), year);
        if (!this.yearList.contains(yearItem)) {
            this.yearList.add(yearItem);
        }
    }

}
