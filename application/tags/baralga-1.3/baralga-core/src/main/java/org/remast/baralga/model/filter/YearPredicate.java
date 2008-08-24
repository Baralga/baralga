package org.remast.baralga.model.filter;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.remast.baralga.Messages;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;

/**
 * Holds for all project activities of one year.
 * @author remast
 */
public class YearPredicate implements Predicate {

    /**
     * The year to check for.
     */
    private final Date dateInYear;

    public YearPredicate(final Date dateInYear) {
        this.dateInYear = dateInYear;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that year else <code>false</code>
     */
    public boolean evaluate(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ProjectActivity)) {
            throw new IllegalArgumentException(Messages
                    .getString("YearPredicate.ErrorNoProjectActivity")); //$NON-NLS-1$
        }

        final ProjectActivity activity = (ProjectActivity) object;
        return DateUtils.isSameYear(activity.getStart(), this.dateInYear);
    }

}
