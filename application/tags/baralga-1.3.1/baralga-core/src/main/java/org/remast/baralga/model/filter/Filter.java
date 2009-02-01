package org.remast.baralga.model.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

/**
 * Filter for selecting only those project activities which satisfy 
 * some selected criteria.
 * @author remast
 */
public class Filter {

    /** The predicates of the filter. */
    private final List<Predicate> predicates = new ArrayList<Predicate>();
    
    
    /** The week of the year to filter by. */
    private Date weekOfYear;

    /** The predicate to filter by week of year. */
    private Predicate weekOfYearPredicate;
    
    
    /** The month to filter by. */
    private Date month;

    /** The predicate to filter by month. */
    private Predicate monthPredicate;
    
    /** The year to filter by. */
    private Date year;

    
    /** The predicate to filter by year. */
    private Predicate yearPredicate;

    /** The project to filter by. */
    private Project project;

    /** The predicate to filter by project. */
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

    /**
     * Checks whether the given activity matches the filter criteria.
     * @param activity the project activity to check
     * @return <code>true</code> if activity matches the filter
     * otherwise <code>false</code>
     */
    public final boolean matchesCriteria(final ProjectActivity activity) {
        for (Predicate predicate : predicates) {
            if (!predicate.evaluate(activity)) {
                return false;
            }
        }
        return true;
    }
 
    /**
     * Getter for the week of year.
     * @return the week of year
     */
    public Date getWeekOfYear() {
        return this.weekOfYear;
    }

    /**
     * Sets the weekOfYear to filter by.
     * @param weekOfYear the weekOfYear to set
     */
    public void setWeekOfYear(final Date weekOfYear) {
        this.weekOfYear = weekOfYear;

        if (this.weekOfYearPredicate != null) {
            this.predicates.remove(this.weekOfYearPredicate);
        }

        // If week is null set week predicate also to null.
        if (this.weekOfYear == null) {
            this.weekOfYearPredicate = null;
        }

        final Predicate newWeekOfYearPredicate = new WeekOfYearPredicate(weekOfYear);
        this.weekOfYearPredicate = newWeekOfYearPredicate;
        this.predicates.add(newWeekOfYearPredicate);
    }
    
    /**
     * Getter for the month.
     * @return the month
     */
    public Date getMonth() {
        return this.month;
    }

    /**
     * Sets the month to filter by.
     * @param month the month to set
     */
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

    /**
     * Getter for the year.
     * @return the year
     */
    public Date getYear() {
        return this.year;
    }

    /**
     * Sets the year to filter by.
     * @param year the year to set
     */
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

    /**
     * Sets the project to filter by.
     * @param project the project to set
     */
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
