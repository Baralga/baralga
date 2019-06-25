package org.remast.baralga.model.filter;

import org.remast.baralga.model.ProjectActivity;
import org.remast.util.Interval;

import java.time.LocalDateTime;
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
        final LocalDateTime startAdjusted = timeInterval.getStart().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime endAdjusted = timeInterval.getEnd().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final Interval intervalAdjusted = new Interval(startAdjusted, endAdjusted);
        
        return intervalAdjusted.contains(activity.getStart());
    }

}
