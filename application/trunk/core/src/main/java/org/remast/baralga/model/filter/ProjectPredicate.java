package org.remast.baralga.model.filter;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ObjectUtils;
import org.remast.baralga.gui.Messages;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

public class ProjectPredicate implements Predicate {

    private Project project;

    public ProjectPredicate(Project project) {
        this.project = project;
    }

    public boolean evaluate(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ProjectActivity))
            throw new IllegalArgumentException(Messages.getString("ProjectPredicate.ErrorNoProjectActivity")); //$NON-NLS-1$

        final ProjectActivity activity = (ProjectActivity) object;
        return ObjectUtils.equals(this.project, activity.getProject());
    }

}
