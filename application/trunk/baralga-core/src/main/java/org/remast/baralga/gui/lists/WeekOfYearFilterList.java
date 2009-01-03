package org.remast.baralga.gui.lists;

import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

import org.joda.time.DateTime;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

public class WeekOfYearFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(WeekOfYearFilterList.class);

    public static final SimpleDateFormat WEEK_OF_YEAR_FORMAT = new SimpleDateFormat("w"); //$NON-NLS-1$

    /** The model. */
    private final PresentationModel model;

    /** Value for the all weeks of year dummy. */
    public static final int ALL_WEEKS_OF_YEAR_DUMMY = -10;

    /** Filter item for the all weeks of year dummy. */
    public static final FilterItem<Integer> ALL_WEEKS_OF_YEAR_FILTER_ITEM = new FilterItem<Integer>(
            ALL_WEEKS_OF_YEAR_DUMMY, 
            textBundle.textFor("WeekOfYearFilterList.AllWeeksOfYearLabel") //$NON-NLS-1$
    );

    /** Value for the current week of year dummy. */
    public static final int CURRENT_WEEK_OF_YEAR_DUMMY = -5;

    /** Filter item for the current week of year dummy. */
    public static final FilterItem<Integer> CURRENT_WEEK_OF_YEAR_FILTER_ITEM = new FilterItem<Integer>(
            CURRENT_WEEK_OF_YEAR_DUMMY,
            textBundle.textFor("WeekOfYearFilterList.CurrentWeekOfYearLabel") //$NON-NLS-1$
    );

    private EventList<FilterItem<Integer>> weekOfYearList;

    public WeekOfYearFilterList(final PresentationModel model) {
        this.model = model;
        this.weekOfYearList = new BasicEventList<FilterItem<Integer>>();

        this.model.addObserver(this);

        initialize();
    }

    private void initialize() {
        this.weekOfYearList.clear();
        this.weekOfYearList.add(ALL_WEEKS_OF_YEAR_FILTER_ITEM);
        this.weekOfYearList.add(CURRENT_WEEK_OF_YEAR_FILTER_ITEM);

        for (ProjectActivity activity : this.model.getData().getActivities()) {
            this.addWeekOfYear(activity);
        }
    }

    public SortedList<FilterItem<Integer>> getWeekList() {
        return new SortedList<FilterItem<Integer>>(this.weekOfYearList);
    }

    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;

        switch (event.getType()) {

        case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
            this.addWeekOfYear((ProjectActivity) event.getData());
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            this.initialize();
            break;
        }
    }

    private void addWeekOfYear(final ProjectActivity activity) {
        final DateTime dateTime = new DateTime(activity.getStart());
        final Integer weekOfYear = dateTime.getWeekOfWeekyear();

        final FilterItem<Integer> filterItem = new FilterItem<Integer>(weekOfYear);
        if (!this.weekOfYearList.contains(filterItem))
            this.weekOfYearList.add(filterItem);
    }
}
