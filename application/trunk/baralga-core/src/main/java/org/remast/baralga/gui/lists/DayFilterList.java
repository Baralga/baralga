package org.remast.baralga.gui.lists;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.swing.util.LabeledItem;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

/**
 * The list containing all days available for the filter.
 * @author remast
 */
public class DayFilterList {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(DayFilterList.class);

//    public static final DateFormat DAY_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT); //$NON-NLS-1$
    
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("EE " + DateTimeFormat.patternForStyle("S-", Locale.getDefault())); //$NON-NLS-1$


    /** The model. */
    private final PresentationModel model;

    /** Value for the all days dummy. */
    public static final int ALL_DAYS_DUMMY = -10;

    /** filter item for the all days dummy. */
    public static final LabeledItem<Integer> ALL_DAYS_FILTER_ITEM = new LabeledItem<Integer>(
            ALL_DAYS_DUMMY,
            textBundle.textFor("DayFilterList.AllDaysLabel") //$NON-NLS-1$
    );

    /** Value for the current day dummy. */
    public static final int CURRENT_DAY_DUMMY = -5;

    /** filter item for the current day dummy. */
    public static final LabeledItem<Integer> CURRENT_DAY_FILTER_ITEM = new LabeledItem<Integer>(
            CURRENT_DAY_DUMMY,
            textBundle.textFor("DayFilterList.CurrentDayLabel", DAY_FORMAT.print(DateUtils.getNowAsDateTime())) //$NON-NLS-1$
    );

    /** The actual list containing all days. */
    private EventList<LabeledItem<Integer>> dayList;

    /**
     * Creates a new list for the given model.
     * @param model the model to create list for
     */
    public DayFilterList(final PresentationModel model) {
        this.model = model;
        this.dayList = new BasicEventList<LabeledItem<Integer>>();

        initialize();
    }

    /**
     * Initializes the list with all days from model.
     */
    private void initialize() {
        this.dayList.clear();
        this.dayList.add(ALL_DAYS_FILTER_ITEM);
        this.dayList.add(CURRENT_DAY_FILTER_ITEM);

        // Get day from filter
        final Integer filterDay = UserSettings.instance().getFilterSelectedDay();
        boolean filterDayFound = false;

        final Iterator weekIterator = org.apache.commons.lang.time.DateUtils.iterator(DateUtils.getNowAsDateTime().toDate(), org.apache.commons.lang.time.DateUtils.RANGE_WEEK_MONDAY);
        for (Iterator iterator = weekIterator; iterator.hasNext();) {
            final Calendar calendar = (Calendar) iterator.next();
            final DateTime date = new DateTime(calendar);
            addDay(date);
        }
    }

    public SortedList<LabeledItem<Integer>> getDayList() {
        return new SortedList<LabeledItem<Integer>>(this.dayList);
    }

    /**
     * Adds the month of the given activity to the list.
     * @param month the activity whose month is to be added
     */
    private void addDay(final DateTime day) {
        final String dayLabel = DAY_FORMAT.print(day);
        final LabeledItem<Integer> dayItem = new LabeledItem<Integer>(
                day.getDayOfYear(), 
                dayLabel
        );

        if (!this.dayList.contains(dayItem)) {
            this.dayList.add(dayItem);
        }
    }

}
