package org.remast.baralga.model.filter;

import org.apache.commons.collections.Predicate;
import org.joda.time.Interval;
import org.remast.baralga.model.ProjectActivity;

/**
 * Holds for all project activities of a time interval.
 * @author remast
 */
public class TimeIntervalPredicate implements Predicate {

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
    public boolean evaluate(final Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ProjectActivity)) {
            return false;
        }

        final ProjectActivity activity = (ProjectActivity) object;
  
		final boolean isSameYear = timeInterval.getStart().getYear() == timeInterval.getEnd().getYear();
		if (isSameYear) {
	        final boolean dayMatches = timeInterval.getStart().getDayOfYear() <= activity.getDay().getDayOfYear() && activity.getDay().getDayOfYear() < timeInterval.getEnd().getDayOfYear();
	        return dayMatches;
		} else {
	        final boolean yearMatches = timeInterval.getStart().getYear() <= activity.getDay().getYear() && activity.getDay().getYear() < timeInterval.getEnd().getYear();
	        final boolean dayMatches = timeInterval.getStart().getDayOfYear() <= activity.getDay().getDayOfYear() && activity.getDay().getDayOfYear() < timeInterval.getEnd().getDayOfYear();
	        return yearMatches && dayMatches;
		}
    }

}