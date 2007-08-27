package org.remast.baralga.model.filter;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.remast.baralga.model.ProjectActivity;

public class Filter<E> {
    
    /** The predicates of the filter. */
    private List<Predicate> predicates;
    
    /**
     * Create filter with no predicates.
     */
    public Filter() {
        this.predicates = new Vector<Predicate>();
    }
    
    /**
     * Add a predicate to the filter.
     * @param predicate the predicate to add
     */
    public void addPredicate(Predicate predicate) {
        this.predicates.add(predicate);
    }
   
    /**
     * Remove a predicate from the filter.
     * @param predicate the predicate to remove
     */
    public void removePredicate(Predicate predicate) {
        this.predicates.remove(predicate);
    }
    
    /**
     * Apply this filter to given elements.
     * @param elements the elements to apply filter to
     * @return a list of elements satisfying the filter
     */
    public List<E> applyFilters(final List<E> elements) {
        Collection<E> filteredElements = elements;
        for (Predicate predicate : predicates) {
            filteredElements = CollectionUtils.select(filteredElements, predicate);
        }
        
        List<E> filteredElementsList = new Vector<E>();
        filteredElementsList.addAll(filteredElements);
        return filteredElementsList;
    }

    public boolean satisfiesPredicates(ProjectActivity activity) {
        for (Predicate predicate : predicates) {
            if(!predicate.evaluate(activity))
                return false;
        }
        return true;
    }

}
