package org.remast.baralga.model.filter;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ObjectUtils;
import org.remast.baralga.Messages;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

/**
 * Holds for all activities of one project.
 * @author remast
 */
public class ProjectPredicate implements Predicate {

    /**
     * The project to check for.
     */
    private final Project project;

    public ProjectPredicate(final Project project) {
        this.project = project;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that project else <code>false</code>
     */
    public boolean evaluate(Object object) {
        if (object == null) {
            return false;
        }

        if (!(object instanceof ProjectActivity)) {
            throw new IllegalArgumentException(Messages.getString("ProjectPredicate.ErrorNoProjectActivity")); //$NON-NLS-1$
        }

        final ProjectActivity activity = (ProjectActivity) object;
        return ObjectUtils.equals(this.project, activity.getProject());
    }

}
