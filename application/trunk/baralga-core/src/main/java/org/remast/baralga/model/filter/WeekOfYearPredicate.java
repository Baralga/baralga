package org.remast.baralga.model.filter;

import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.remast.baralga.model.ProjectActivity;

/**
 * Holds for all project activities of one week of the year.
 * @author remast
 */
public class WeekOfYearPredicate implements Predicate {

    /**
     * The week of year to check for.
     */
    private final DateTime dateInWeekOfYear;

    /**
     * Constructor for a new predicate.
     * @param dateInWeekOfYear the week of year of the predicate
     */
    public WeekOfYearPredicate(final DateTime dateInWeekOfYear) {
        this.dateInWeekOfYear = dateInWeekOfYear;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that month else <code>false</code>
     */
    public final boolean evaluate(final Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ProjectActivity)) {
            return false;
        }

        final ProjectActivity activity = (ProjectActivity) object;
        return this.dateInWeekOfYear.getWeekOfWeekyear() == activity.getStart().getWeekOfWeekyear();
    }

}
