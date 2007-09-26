package org.remast.baralga.model.filter;

import java.util.Date;

import org.apache.commons.collections.Predicate;
import org.remast.baralga.Messages;
import org.remast.baralga.model.ProjectActivity;

public class YearPredicate implements Predicate {

    private Date dateInYear;

    public YearPredicate(Date dateInYear) {
        this.dateInYear = dateInYear;
    }

    public boolean evaluate(Object object) {
        if(!(object instanceof ProjectActivity))
            throw new IllegalArgumentException(Messages.getString("YearPredicate.ErrorNoeProjectActivityErrorNoProjectActivitity")); //$NON-NLS-1$
        
        ProjectActivity activity = (ProjectActivity) object;
        return true;
//        return DateUtils.isSameYear(activity.getStart(), this.dateInYear);
    }

}
