package org.remast.baralga.model.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

public class Filter {

    /** The predicates of the filter. */
    private final List<Predicate> predicates = new ArrayList<Predicate>();

    private Date month;

    private Predicate monthPredicate;

    private Date year;

    private Predicate yearPredicate;

    private Project project;

    private Predicate projectPredicate;

    /**
     * Create filter with no predicates.
     */
    public Filter() {
    }

    /**
     * Apply this filter to given elements.
     * 
     * @param elements
     *            the elements to apply filter to
     * @return a list of elements satisfying the filter
     */
    public List<ProjectActivity> applyFilters(final List<ProjectActivity> elements) {
        ArrayList<ProjectActivity> filteredElements = new ArrayList<ProjectActivity>(elements);
        for (Predicate predicate : predicates) {
            for (ProjectActivity activity : new ArrayList<ProjectActivity>(filteredElements)) {
                if (!predicate.evaluate(activity))
                    filteredElements.remove(activity);
            }
        }

        filteredElements.trimToSize();
        return filteredElements;
    }

    public boolean satisfiesPredicates(final ProjectActivity activity) {
        for (Predicate predicate : predicates) {
            if (!predicate.evaluate(activity))
                return false;
        }
        return true;
    }

    public Date getMonth() {
        return this.month;
    }

    public void setMonth(final Date month) {
        this.month = month;

        if (this.monthPredicate != null) {
            this.predicates.remove(this.monthPredicate);
        }

        // If month is null set month predicate also to null.
        if (this.month == null) {
            this.monthPredicate = null;
        }

        final Predicate newMonthPredicate = new MonthPredicate(month);
        this.monthPredicate = newMonthPredicate;
        this.predicates.add(newMonthPredicate);
    }

    public Date getYear() {
        return this.year;
    }

    public void setYear(final Date year) {
        this.year = year;

        if (this.yearPredicate != null) {
            this.predicates.remove(this.yearPredicate);
            return;
        }

        // If year is null set year predicate also to null.
        if (this.year == null) {
            this.yearPredicate = null;
            return;
        }

        final Predicate newYearPredicate = new YearPredicate(year);
        this.yearPredicate = newYearPredicate;
        this.predicates.add(newYearPredicate);
    }

    public void setProject(final Project project) {
        this.project = project;

        if (this.projectPredicate != null) {
            this.predicates.remove(this.projectPredicate);
        }

        // If project is null set project predicate also to null.
        if (this.project == null) {
            this.projectPredicate = null;
            return;
        }

        final Predicate newProjectPredicate = new ProjectPredicate(project);
        this.projectPredicate = newProjectPredicate;
        this.predicates.add(newProjectPredicate);
    }

}
