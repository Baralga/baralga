package org.remast.baralga.model.filter;

import java.util.Objects;
import java.util.function.Predicate;
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
     * @param activity the object to check
     * @return <code>true</code> if the given object is a project activity
     * of that project else <code>false</code>
     */
    public boolean test(final ProjectActivity activity) {
        if (activity == null) {
            return false;
        }

        return Objects.equals(this.project, activity.getProject());
    }

}
