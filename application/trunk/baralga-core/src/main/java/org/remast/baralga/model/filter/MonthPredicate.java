package org.remast.baralga.model.filter;

import org.apache.commons.collections.Predicate;
import org.joda.time.DateTime;
import org.remast.baralga.model.ProjectActivity;

/**
 * Holds for all project activities of one month.
 * @author remast
 */
public class MonthPredicate implements Predicate {

    /**
     * The month to check for.
     */
    private final DateTime dateInMonth;

    /**
     * Creates a new predicate that holds for the given month.
     * @param dateInMonth the month the predicate holds for
     */
    public MonthPredicate(final DateTime dateInMonth) {
        this.dateInMonth = dateInMonth;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that month else <code>false</code>
     */
    public boolean evaluate(final Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ProjectActivity)) {
            return false;
        }

        final ProjectActivity activity = (ProjectActivity) object;
        return this.dateInMonth.getMonthOfYear() == activity.getStart().getMonthOfYear();
    }

}
