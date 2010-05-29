package org.remast.baralga.gui.lists;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.swing.util.LabeledItem;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/**
 * The list containing all days available for the filter.
 * @author remast
 */
public class DayFilterList {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(DayFilterList.class);
    
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("e"); //$NON-NLS-1$

    /** The model. */
    @SuppressWarnings("unused")
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
    
	private static String currentDayLabel = "";
    static {
    	final int currentDay = Integer.valueOf(DAY_FORMAT.print(DateUtils.getNowAsDateTime()));
    	switch (currentDay) {
		case 1:
			currentDayLabel = textBundle.textFor("DayFilterList.MondayLabel"); //$NON-NLS-1$
			break;
		case 2:
			currentDayLabel = textBundle.textFor("DayFilterList.TuesdayLabel"); //$NON-NLS-1$
			break;
		case 3:
			currentDayLabel = textBundle.textFor("DayFilterList.WednesdayLabel"); //$NON-NLS-1$
			break;
		case 4:
			currentDayLabel = textBundle.textFor("DayFilterList.ThursdayLabel"); //$NON-NLS-1$
			break;
		case 5:
			currentDayLabel = textBundle.textFor("DayFilterList.FridayLabel"); //$NON-NLS-1$
			break;
		case 6:
			currentDayLabel = textBundle.textFor("DayFilterList.SaturdayLabel"); //$NON-NLS-1$
			break;
		case 7:
			currentDayLabel = textBundle.textFor("DayFilterList.SundayLabel"); //$NON-NLS-1$
			break;
		}
    }

    /** filter item for the current day dummy. */
    public static final LabeledItem<Integer> CURRENT_DAY_FILTER_ITEM = new LabeledItem<Integer>(
            CURRENT_DAY_DUMMY,
            textBundle.textFor("DayFilterList.CurrentDayLabel", currentDayLabel) //$NON-NLS-1$
    );
    
    /** Filter item for mondays. */
    public static final LabeledItem<Integer> MONDAY_FILTER_ITEM = new LabeledItem<Integer>(
    		2,
    		textBundle.textFor("DayFilterList.MondayLabel") //$NON-NLS-1$
    );
    
    /** Filter item for tuesdays. */
    public static final LabeledItem<Integer> TUESDAY_FILTER_ITEM = new LabeledItem<Integer>(
    		3,
    		textBundle.textFor("DayFilterList.TuesdayLabel") //$NON-NLS-1$
    );
    
    /** Filter item for wednesdays. */
    public static final LabeledItem<Integer> WEDNESDAY_FILTER_ITEM = new LabeledItem<Integer>(
    		4,
    		textBundle.textFor("DayFilterList.WednesdayLabel") //$NON-NLS-1$
    );
    
    /** Filter item for thursdays. */
    public static final LabeledItem<Integer> THURSDAY_FILTER_ITEM = new LabeledItem<Integer>(
    		5,
    		textBundle.textFor("DayFilterList.ThursdayLabel") //$NON-NLS-1$
    );
    
    /** Filter item for fridays. */
    public static final LabeledItem<Integer> FRIDAY_FILTER_ITEM = new LabeledItem<Integer>(
    		6,
    		textBundle.textFor("DayFilterList.FridayLabel") //$NON-NLS-1$
    );
    
    /** Filter item for saturdays. */
    public static final LabeledItem<Integer> SATURDAY_FILTER_ITEM = new LabeledItem<Integer>(
    		7,
    		textBundle.textFor("DayFilterList.SaturdayLabel") //$NON-NLS-1$
    );
    
    /** Filter item for sundays. */
    public static final LabeledItem<Integer> SUNDAY_FILTER_ITEM = new LabeledItem<Integer>(
    		1,
    		textBundle.textFor("DayFilterList.SundayLabel") //$NON-NLS-1$
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
    @SuppressWarnings("unchecked")
	private void initialize() {
        this.dayList.clear();
        this.dayList.add(ALL_DAYS_FILTER_ITEM);
        this.dayList.add(CURRENT_DAY_FILTER_ITEM);

        // All days of the week
        this.dayList.add(MONDAY_FILTER_ITEM);
        this.dayList.add(TUESDAY_FILTER_ITEM);
        this.dayList.add(WEDNESDAY_FILTER_ITEM);
        this.dayList.add(THURSDAY_FILTER_ITEM);
        this.dayList.add(FRIDAY_FILTER_ITEM);
        this.dayList.add(SATURDAY_FILTER_ITEM);
        this.dayList.add(SUNDAY_FILTER_ITEM);
    }

    public EventList<LabeledItem<Integer>> getDayList() {
        return this.dayList;
    }


}
