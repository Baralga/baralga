package org.remast.baralga.model.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.Predicate;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

public class Filter {
    
    /** The predicates of the filter. */
    private List<Predicate> predicates;
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
        this.predicates = new Vector<Predicate>();
    }
    
    /**
     * Apply this filter to given elements.
     * @param elements the elements to apply filter to
     * @return a list of elements satisfying the filter
     */
    public List<ProjectActivity> applyFilters(final List<ProjectActivity> elements) {
        Collection<ProjectActivity> filteredElements = new ArrayList<ProjectActivity>(elements);
        for (Predicate predicate : predicates) {
//            filteredElements = CollectionUtils.select(filteredElements, predicate);
            for (ProjectActivity activity : new ArrayList<ProjectActivity>(filteredElements)) {
                if (!predicate.evaluate(activity))
                    filteredElements.remove(activity);
            }
        }
        
        final List<ProjectActivity> filteredElementsList = new Vector<ProjectActivity>(filteredElements.size());
        filteredElementsList.addAll(filteredElements);
        return filteredElementsList;
    }

    public boolean satisfiesPredicates(ProjectActivity activity) {
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

        Predicate newMonthPredicate = new MonthPredicate(month);
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
        }
            
        Predicate newYearPredicate = new YearPredicate(year);
        this.yearPredicate = newYearPredicate;
        this.predicates.add(newYearPredicate);
    }

    public void setProject(final Project project) {
        this.project = project;
        
        if (this.projectPredicate != null) {
            this.predicates.remove(this.projectPredicate);
        }
            
        Predicate newProjectPredicate = new ProjectPredicate(project);
        this.projectPredicate = newProjectPredicate;
        this.predicates.add(newProjectPredicate);
    }

}
