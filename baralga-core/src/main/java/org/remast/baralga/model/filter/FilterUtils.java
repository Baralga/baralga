package org.remast.baralga.model.filter;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.IsoFields;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.util.Interval;
import org.remast.util.TextResourceBundle;

/**
 * Miscellaneous utility methods for dealing with filters.
 * @author remast
 */
public abstract class FilterUtils {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

    private static final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern(" EEEE");
	private static final DateTimeFormatter weekOfYearFormatter = DateTimeFormatter.ofPattern("ww");
    private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

    /**
     * Builds a string for the time interval of the filter.
     * @param filter the filter with the time interval
     * @return the string for the time interval of the filter
     */
	public static String makeIntervalString(final Filter filter) {
		if (filter == null) {
			return "";
		}
		
		String intervalString = filter.getTimeInterval().toString();
		switch (filter.getSpanType()) {
		case Day:
			intervalString = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(filter.getTimeInterval().getStart()) + dayFormatter.format(filter.getTimeInterval().getStart());
			break;
		case Week:
			intervalString = "(" + textBundle.textFor("ReportPanel.CWLabel") + " " + weekOfYearFormatter.format(filter.getTimeInterval().getStart()) + ") ";
			intervalString += FormatUtils.formatDate(filter.getTimeInterval().getStart()) + " - " + FormatUtils.formatDate(filter.getTimeInterval().getEnd().minusDays(1));
			break;
		case Month:
			intervalString = monthFormatter.format(filter.getTimeInterval().getStart());
			break;
		case Quarter:
			intervalString = "Q" + filter.getTimeInterval().getStart().get(IsoFields.QUARTER_OF_YEAR) + " " + yearFormatter.format(filter.getTimeInterval().getStart());
			break;
		case Year:
			intervalString = yearFormatter.format(filter.getTimeInterval().getStart());
			break;
		}
		
		return intervalString;
	}

	/**
	 * Moves the filter to the previous time interval.
	 * @param filter the filter to be moved
	 */
	public static void moveToPreviousInterval(final Filter filter) {
		if (filter == null) {
			return;
		}

		Interval newTimeInterval = null;
		
		final Interval timeInterval = filter.getTimeInterval();
		
		switch (filter.getSpanType()) {
		case Day:
			newTimeInterval = new Interval(timeInterval.getStart().minusDays(1), timeInterval.getEnd().minusDays(1));
			break;
		case Week:
			newTimeInterval = new Interval(timeInterval.getStart().minusWeeks(1), timeInterval.getEnd().minusWeeks(1));
			break;
		case Month:
			newTimeInterval = new Interval(timeInterval.getStart().minusMonths(1), timeInterval.getEnd().minusMonths(1));
			break;
		case Quarter:
			newTimeInterval = new Interval(timeInterval.getStart().minusMonths(3), timeInterval.getEnd().minusMonths(3));
			break;
		case Year:
			newTimeInterval = new Interval(timeInterval.getStart().minusYears(1), timeInterval.getEnd().minusYears(1));
			break;
		}
		
		filter.setTimeInterval(newTimeInterval);
	}
	
	/**
	 * Moves the filter to the next time interval.
	 * @param filter the filter to be moved
	 */
	public static void moveToNextInterval(final Filter filter) {
		if (filter == null) {
			return;
		}
		
		Interval newTimeInterval = null;
		
		final Interval timeInterval = filter.getTimeInterval();
		
		switch (filter.getSpanType()) {
		case Day:
			newTimeInterval = new Interval(timeInterval.getStart().plusDays(1), timeInterval.getEnd().plusDays(1));
			break;
		case Week:
			newTimeInterval = new Interval(timeInterval.getStart().plusWeeks(1), timeInterval.getEnd().plusWeeks(1));
			break;
		case Month:
			newTimeInterval = new Interval(timeInterval.getStart().plusMonths(1), timeInterval.getEnd().plusMonths(1));
			break;
		case Quarter:
			newTimeInterval = new Interval(timeInterval.getStart().plusMonths(3), timeInterval.getEnd().plusMonths(3));
			break;
		case Year:
			newTimeInterval = new Interval(timeInterval.getStart().plusYears(1), timeInterval.getEnd().plusYears(1));
			break;
		}
		
		filter.setTimeInterval(newTimeInterval);
	}
	
}
