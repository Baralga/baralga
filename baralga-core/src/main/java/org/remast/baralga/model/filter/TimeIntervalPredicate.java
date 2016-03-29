package org.remast.baralga.model.filter;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.remast.baralga.model.ProjectActivity;

import java.util.function.Predicate;

/**
 * Holds for all project activities of a time interval.
 * @author remast
 */
public class TimeIntervalPredicate implements Predicate<ProjectActivity> {

    /**
     * The interval to check for.
     */
    private final Interval timeInterval;

    /**
     * Creates a new predicate that holds for the given day.
     * @param timeInterval the day the predicate holds for
     */
    public TimeIntervalPredicate(final Interval timeInterval) {
        this.timeInterval = timeInterval;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object
     *            the object to check
     * @return <code>true</code> if the given object is a project activity of
     *         that day else <code>false</code>
     */
    public boolean test(final ProjectActivity activity) {
        if (activity == null) {
            return false;
        }

        // Set hours, minutes, seconds and milliseconds of interval ends 
        // to zero so that every activity of a day is contained within the 
        // interval.
        final DateTime startAdjusted = timeInterval.getStart().withMillisOfDay(0);
        final DateTime endAdjusted = timeInterval.getEnd().withMillisOfDay(0);
        final Interval intervalAdjusted = new Interval(startAdjusted, endAdjusted);
        
        return intervalAdjusted.contains(activity.getStart().getMillis());
    }

}
