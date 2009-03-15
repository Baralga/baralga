package org.remast.baralga.gui.lists;

import java.util.Observable;
import java.util.Observer;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.util.LabeledItem;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

public class WeekOfYearFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(WeekOfYearFilterList.class);

    public static final DateTimeFormatter WEEK_OF_YEAR_FORMAT = DateTimeFormat.forPattern("ww"); //$NON-NLS-1$

    /** The model. */
    private final PresentationModel model;

    /** Value for the all weeks of year dummy. */
    public static final int ALL_WEEKS_OF_YEAR_DUMMY = -10;

    /** Filter item for the all weeks of year dummy. */
    public static final LabeledItem<Integer> ALL_WEEKS_OF_YEAR_FILTER_ITEM = new LabeledItem<Integer>(
            ALL_WEEKS_OF_YEAR_DUMMY, 
            textBundle.textFor("WeekOfYearFilterList.AllWeeksOfYearLabel") //$NON-NLS-1$
    );

    /** Value for the current week of year dummy. */
    public static final int CURRENT_WEEK_OF_YEAR_DUMMY = -5;

    /** Filter item for the current week of year dummy. */
    public static final LabeledItem<Integer> CURRENT_WEEK_OF_YEAR_FILTER_ITEM = new LabeledItem<Integer>(
            CURRENT_WEEK_OF_YEAR_DUMMY,
            textBundle.textFor("WeekOfYearFilterList.CurrentWeekOfYearLabel", WEEK_OF_YEAR_FORMAT.print(DateUtils.getNowAsDateTime())) //$NON-NLS-1$
    );

    private EventList<LabeledItem<Integer>> weekOfYearList;

    public WeekOfYearFilterList(final PresentationModel model) {
        this.model = model;
        this.weekOfYearList = new BasicEventList<LabeledItem<Integer>>();

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

    public SortedList<LabeledItem<Integer>> getWeekList() {
        return new SortedList<LabeledItem<Integer>>(this.weekOfYearList);
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
        final String weekOfYear = WEEK_OF_YEAR_FORMAT.print(activity.getStart());
        final LabeledItem<Integer> filterItem = new LabeledItem<Integer>(Integer.valueOf(weekOfYear), weekOfYear);
        if (!this.weekOfYearList.contains(filterItem))
            this.weekOfYearList.add(filterItem);
    }
}
