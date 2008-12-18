package org.remast.baralga.model.filter;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

/**
 * Holds for all project activities of one month.
 * @author remast
 */
public class MonthPredicate implements Predicate {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(MonthPredicate.class);

    /**
     * The month to check for.
     */
    private final Date dateInMonth;

    public MonthPredicate(final Date dateInMonth) {
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
            throw new IllegalArgumentException(textBundle.textFor("MonthPredicate.ErrorNoProjectActivity")); //$NON-NLS-1$
        }

        final ProjectActivity activity = (ProjectActivity) object;
        return DateUtils.isSimilarMonth(activity.getStart(), this.dateInMonth);
    }

}
