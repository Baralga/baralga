package org.remast.baralga.gui.lists;

import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
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
 * The list containing all weeks of year available for the filter.
 * @author remast
 */
public class WeekOfYearFilterList implements Observer {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(WeekOfYearFilterList.class);

    public static final NumberFormat WEEK_OF_YEAR_FORMAT = new DecimalFormat("##00"); //$NON-NLS-1$

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
            textBundle.textFor("WeekOfYearFilterList.CurrentWeekOfYearLabel", WEEK_OF_YEAR_FORMAT.format(DateUtils.getNowAsDateTime().getWeekOfWeekyear())) //$NON-NLS-1$
    );

    /** The actual list containing all weeks of year. */
    private EventList<LabeledItem<Integer>> weekOfYearList;

    /**
     * Creates a new list for the given model.
     * @param model the model to create list for
     */
    public WeekOfYearFilterList(final PresentationModel model) {
        this.model = model;
        this.weekOfYearList = new BasicEventList<LabeledItem<Integer>>();

        this.model.addObserver(this);

        initialize();
    }

    /**
     * Initializes the list with all weeks of year from model.
     */
    private void initialize() {
        this.weekOfYearList.clear();
        this.weekOfYearList.add(ALL_WEEKS_OF_YEAR_FILTER_ITEM);
        this.weekOfYearList.add(CURRENT_WEEK_OF_YEAR_FILTER_ITEM);

        // Get week of year from filter
        final Integer filterWeekOfYear = UserSettings.instance().getFilterSelectedWeekOfYear();
        boolean filterWeekOfYearFound = false;

        for (ProjectActivity activity : this.model.getData().getActivities()) {
        	final int weekOfYear = activity.getDay().getWeekOfWeekyear();
        	
        	/**
        	 * In Joda time weeks go from 1 - 53 (see http://joda-time.sourceforge.net/field.html). 
        	 * We only allow weeks up to 52 because week 53 results in an exception.
        	 */
        	if (weekOfYear > 52) {
        		this.addWeekOfYear(1);
        	} else {
        		this.addWeekOfYear(weekOfYear);
        	}

            if (filterWeekOfYear != null && activity.getDay().getWeekOfWeekyear() == filterWeekOfYear) {
                filterWeekOfYearFound = true;
            }
        }

        // Add week of year from filter if not already in list.
        if (filterWeekOfYear != null && filterWeekOfYear > 0 && !filterWeekOfYearFound) {
            this.addWeekOfYear(filterWeekOfYear);
        }
    }

    public SortedList<LabeledItem<Integer>> getWeekList() {
        return new SortedList<LabeledItem<Integer>>(this.weekOfYearList);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;

        switch (event.getType()) {

        case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
            final Collection<ProjectActivity> projectActivities = (Collection<ProjectActivity>) event.getData();
            for (ProjectActivity projectActivity : projectActivities) {
                this.addWeekOfYear(projectActivity.getDay().getWeekOfWeekyear());
            }
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
     * Adds the week of year of the given activity to the list.
     * @param weekOfYear the activity whose week of year is to be added
     */
    private void addWeekOfYear(final int weekOfYear) {
        final String weekOfYearLabel = WEEK_OF_YEAR_FORMAT.format(weekOfYear);
        final LabeledItem<Integer> filterItem = new LabeledItem<Integer>(
                weekOfYear,
                weekOfYearLabel
        );

        if (!this.weekOfYearList.contains(filterItem)) {
            this.weekOfYearList.add(filterItem);
        }
    }
}
