package org.remast.baralga.model.filter;

import com.google.common.base.Predicate;
import org.apache.commons.lang.ObjectUtils;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

/**
 * Holds for all activities of one project.
 * @author remast
 */
public class ProjectPredicate implements Predicate<ProjectActivity> {

    /**
     * The project to check for.
     */
    private final Project project;

    /**
     * Creates a new predicate that holds for the given project.
     * @param project the project the predicate holds for
     */
    public ProjectPredicate(final Project project) {
        this.project = project;
    }

    /**
     * Checks if this predicate holds for the given object.
     * @param object the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that project else <code>false</code>
     */
    public boolean apply(final ProjectActivity activity) {
        if (activity == null) {
            return false;
        }

        return ObjectUtils.equals(this.project, activity.getProject());
    }

}
