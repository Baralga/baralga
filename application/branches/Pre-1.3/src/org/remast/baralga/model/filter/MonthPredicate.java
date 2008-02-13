package org.remast.baralga.model.filter;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.remast.baralga.Messages;
import org.remast.baralga.model.ProjectActivity;
import org.remast.util.DateUtils;

public class MonthPredicate implements Predicate {

    private Date dateInMonth;

    public MonthPredicate(Date dateInMonth) {
        this.dateInMonth = dateInMonth;
    }

    public boolean evaluate(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ProjectActivity)) {
            throw new IllegalArgumentException(Messages.getString("MonthPredicate.ErrorNoProjectActivity")); //$NON-NLS-1$
        }

        final ProjectActivity activity = (ProjectActivity) object;
        return DateUtils.isSimilarMonth(activity.getStart(), this.dateInMonth);
    }

}
