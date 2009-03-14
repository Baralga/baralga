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

    private EventList<LabeledItem<Integer>> yearList;

    public YearFilterList(final PresentationModel model) {
        this.model = model;
        this.yearList = new BasicEventList<LabeledItem<Integer>>();
        this.model.addObserver(this);

        initialize();
    }

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

        case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            this.initialize();
            break;
        }
    }

    private void addYear(final ProjectActivity activity) {
        final String year = YEAR_FORMAT.print(activity.getStart());
        final LabeledItem<Integer> yearItem = new LabeledItem<Integer>(Integer.parseInt(year), year);
        if (!this.yearList.contains(yearItem)) {
            this.yearList.add(yearItem);
        }
    }

}
