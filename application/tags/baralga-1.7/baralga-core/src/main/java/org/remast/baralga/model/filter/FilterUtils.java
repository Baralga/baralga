package org.remast.baralga.model.filter;

import java.util.Locale;

import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.util.TextResourceBundle;

/**
 * Miscellaneous utility methods for dealing with filters.
 * @author remast
 */
public abstract class FilterUtils {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

    private static final DateTimeFormatter dayFormatter = DateTimeFormat.forPattern(DateTimeFormat.patternForStyle("S-", Locale.getDefault()) + " EEEEEEEEE");
	private static final DateTimeFormatter weekOfYearFormatter = DateTimeFormat.forPattern("ww");
    private static final DateTimeFormatter monthFormatter = DateTimeFormat.forPattern("MMMMMMMMMM yyyy");
    private static final DateTimeFormatter yearFormatter = DateTimeFormat.forPattern("yyyy");

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
			intervalString = dayFormatter.print(filter.getTimeInterval().getStart());
			break;
		case Week:
			intervalString = "(" + textBundle.textFor("ReportPanel.CWLabel") + " " + weekOfYearFormatter.print(filter.getTimeInterval().getStart()) + ") ";
			intervalString += FormatUtils.formatDate(filter.getTimeInterval().getStart()) + " - " + FormatUtils.formatDate(filter.getTimeInterval().getEnd().minusDays(1));
			break;
		case Month:
			intervalString = monthFormatter.print(filter.getTimeInterval().getStart());
			break;
		case Year:
			intervalString = yearFormatter.print(filter.getTimeInterval().getStart());
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
		case Year:
			newTimeInterval = new Interval(timeInterval.getStart().plusYears(1), timeInterval.getEnd().plusYears(1));
			break;
		}
		
		filter.setTimeInterval(newTimeInterval);
	}
	
}
