package org.remast.baralga.model.filter;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.remast.baralga.Messages;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;

/**
 * Holds for all project activities of one week of the year.
 * @author remast
 */
public class WeekOfYearPredicate implements Predicate {

    /**
     * The week of year to check for.
     */
    private final Date dateInWeekOfYear;

    public WeekOfYearPredicate(final Date dateInWeekOfYear) {
        this.dateInWeekOfYear = dateInWeekOfYear;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that month else <code>false</code>
     */
    public boolean evaluate(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ProjectActivity)) {
            throw new IllegalArgumentException(Messages.getString("WeekOfYearPredicate.ErrorNoProjectActivity")); //$NON-NLS-1$
        }

        final ProjectActivity activity = (ProjectActivity) object;
        return DateUtils.isSimilarWeekOfYear(activity.getStart(), this.dateInWeekOfYear);
    }

}
