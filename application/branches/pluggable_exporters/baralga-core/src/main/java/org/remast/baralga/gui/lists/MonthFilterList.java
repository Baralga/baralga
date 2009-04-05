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

/**
 * The list containing all months available for the filter.
 * @author remast
 */
public class MonthFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MonthFilterList.class);

    public static final DateTimeFormatter MONTH_FORMAT = DateTimeFormat.forPattern("MM"); //$NON-NLS-1$

    /** The model. */
    private final PresentationModel model;

    /** Value for the all months dummy. */
    public static final int ALL_MONTHS_DUMMY = -10;

    /** filter item for the all months dummy. */
    public static final LabeledItem<Integer> ALL_MONTHS_FILTER_ITEM = new LabeledItem<Integer>(
            ALL_MONTHS_DUMMY,
            textBundle.textFor("MonthFilterList.AllMonthsLabel") //$NON-NLS-1$
    );

    /** Value for the current month dummy. */
    public static final int CURRENT_MONTH_DUMMY = -5;

    /** filter item for the current month dummy. */
    public static final LabeledItem<Integer> CURRENT_MONTH_FILTER_ITEM = new LabeledItem<Integer>(
            CURRENT_MONTH_DUMMY,
            textBundle.textFor("MonthFilterList.CurrentMonthLabel", MONTH_FORMAT.print(DateUtils.getNowAsDateTime())) //$NON-NLS-1$
    );

    /** The actual list containing all months. */
    private EventList<LabeledItem<Integer>> monthList;

    /**
     * Creates a new list for the given model.
     * @param model the model to create list for
     */
    public MonthFilterList(final PresentationModel model) {
        this.model = model;
        this.monthList = new BasicEventList<LabeledItem<Integer>>();

        this.model.addObserver(this);

        initialize();
    }

    /**
     * Initializes the list with all months from model.
     */
    private void initialize() {
        this.monthList.clear();
        this.monthList.add(ALL_MONTHS_FILTER_ITEM);
        this.monthList.add(CURRENT_MONTH_FILTER_ITEM);

        for (ProjectActivity activity : this.model.getData().getActivities()) {
            this.addMonth(activity);
        }
    }

    public SortedList<LabeledItem<Integer>> getMonthList() {
        return new SortedList<LabeledItem<Integer>>(this.monthList);
    }

    /**
     * {@inheritDoc}
     */
    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

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

    /**
     * Adds the month of the given activity to the list.
     * @param activity the activity whose month is to be added
     */
    private void addMonth(final ProjectActivity activity) {
        if (activity == null) {
            return;
        }

        final String month = MONTH_FORMAT.print(activity.getStart());
        final LabeledItem<Integer> monthItem = new LabeledItem<Integer>(
                Integer.valueOf(month), 
                month
        );

        if (!this.monthList.contains(monthItem)) {
            this.monthList.add(monthItem);
        }
    }
}
