package org.remast.baralga.gui.lists;

import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang.StringUtils;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.ProjectActivity;
import org.remast.swing.util.LabeledItem;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;

/**
 * The list containing all years available for the filter.
 * @author remast
 */
public class YearFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(YearFilterList.class);

    /** Format for the year. */
    public static final NumberFormat YEAR_FORMAT = new DecimalFormat("##00"); //$NON-NLS-1$

    /** The model. */
    private final PresentationModel model;

    /** Value for the all years dummy. */
    public static final int ALL_YEARS_DUMMY = -10; //$NON-NLS-1$

    /** Filter item for the all years dummy. */
    public static final LabeledItem<Integer> ALL_YEARS_FILTER_ITEM = new LabeledItem<Integer>(
            ALL_YEARS_DUMMY,
            textBundle.textFor("YearFilterList.AllYearsLabel") //$NON-NLS-1$
    );

    /** Value for the current year dummy. */
    public static final int CURRENT_YEAR_DUMMY = -5; //$NON-NLS-1$

    /** Filter item for the current year dummy. */
    public static final LabeledItem<Integer> CURRENT_YEAR_FILTER_ITEM = new LabeledItem<Integer>(
            CURRENT_YEAR_DUMMY,
            textBundle.textFor("YearFilterList.CurrentYearsLabel", YEAR_FORMAT.format(DateUtils.getNowAsDateTime().getYear())) //$NON-NLS-1$
    );

    /** The actual list containing all years. */
    private EventList<LabeledItem<Integer>> yearList;

    /**
     * Creates a new list for the given model.
     * @param model the model to create list for
     */
    public YearFilterList(final PresentationModel model) {
        this.model = model;
        this.yearList = new BasicEventList<LabeledItem<Integer>>();
        this.model.addObserver(this);

        initialize();
    }

    /**
     * Initializes the list with all years from model.
     */
    private void initialize() {
        this.yearList.clear();
        this.yearList.add(ALL_YEARS_FILTER_ITEM);
        this.yearList.add(CURRENT_YEAR_FILTER_ITEM);
        
        // Get year from filter
        final Integer filterYear = UserSettings.instance().getFilterSelectedYear();
        boolean filterYearFound = false;

        for (ProjectActivity activity : this.model.getData().getActivities()) {
            this.addYear(activity.getDay().getYear());
            
            if (filterYear != null && activity.getDay().getYear() == filterYear) {
                filterYearFound = true;
            }
        }
        
        // Add year from filter if not already in list.
        if (filterYear != null && filterYear > 0 && !filterYearFound) {
            this.addYear(filterYear);
        }
    }

    public SortedList<LabeledItem<Integer>> getYearList() {
        return new SortedList<LabeledItem<Integer>>(this.yearList);
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
            final ProjectActivity activity = (ProjectActivity) event.getData();
            this.addYear(activity.getDay().getYear());
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
            final PropertyChangeEvent propertyChangeEvent = event.getPropertyChangeEvent();
            if (StringUtils.equals(ProjectActivity.PROPERTY_DATE, propertyChangeEvent.getPropertyName())) {
                this.initialize();
            }
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            this.initialize();
            break;

        case BaralgaEvent.DATA_CHANGED:
            this.initialize();
            break;
        }
    }

    /**
     * Adds the year of the given activity to the list.
     * @param year the activity whose year is to be added
     */
    private void addYear(final int year) {
        final String yearLabel = YEAR_FORMAT.format(year);
        final LabeledItem<Integer> yearItem = new LabeledItem<Integer>(
                year,
                yearLabel
        );

        if (!this.yearList.contains(yearItem)) {
            this.yearList.add(yearItem);
        }
    }

}
