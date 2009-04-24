package org.remast.baralga.gui.lists;

import java.beans.PropertyChangeEvent;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang.StringUtils;
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
 * The list containing all years available for the filter.
 * @author remast
 */
public class YearFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(YearFilterList.class);

    public static final DateTimeFormatter YEAR_FORMAT = DateTimeFormat.forPattern("yyyy"); //$NON-NLS-1$

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
            textBundle.textFor("YearFilterList.CurrentYearsLabel", YEAR_FORMAT.print(DateUtils.getNowAsDateTime())) //$NON-NLS-1$
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
        
        for (ProjectActivity activity : this.model.getData().getActivities()) {
            this.addYear(activity);
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
            ProjectActivity activity = (ProjectActivity) event.getData();
            this.addYear(activity);
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
        }
    }

    /**
     * Adds the year of the given activity to the list.
     * @param activity the activity whose year is to be added
     */
    private void addYear(final ProjectActivity activity) {
        if (activity == null) {
            return;
        }
        
        final String year = YEAR_FORMAT.print(activity.getStart());
        final LabeledItem<Integer> yearItem = new LabeledItem<Integer>(Integer.parseInt(year), year);
        if (!this.yearList.contains(yearItem)) {
            this.yearList.add(yearItem);
        }
    }

}
