package org.remast.baralga.model.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.Interval;
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
    
    private Interval timeInterval;

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
     * Checks whether the given activity matches the filter criteria.
     * @param activity the project activity to check
     * @return <code>true</code> if activity matches the filter
     * otherwise <code>false</code>
     */
    public final boolean matchesCriteria(final ProjectActivity activity) {
    	if (activity == null) {
    		return false;
    	}
    	
        for (Predicate predicate : predicates) {
            if (!predicate.evaluate(activity)) {
                return false;
            }
        }
        return true;
    }
    
    public Interval getTimeInterval() {
    	return this.timeInterval;
    }
 
    /**
     * Sets the timeInterval to filter by.
     * @param timeInterval the timeInterval to set
     */
    public void setTimeInterval(final Interval timeInterval) {
        this.timeInterval = timeInterval;

//        if (this.timeIntervalPredicate != null) {
//            this.predicates.remove(this.timeIntervalPredicate);
//        }
//
//        final Predicate newTimeIntervalPredicate = new WeekOfYearPredicate(timeInterval);
//        this.timeIntervalPredicate = newTimeIntervalPredicate;
//        this.predicates.add(newTimeIntervalPredicate);
    }
    
    /**
     * Getter for the project.
     * @return the project
     */
    public Project getProject() {
        return this.project;
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
    
    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        
        if (that == null || !(that instanceof Filter)) {
            return false;
        }
        
        final Filter filter = (Filter) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.getProject(), filter.getProject());
        eqBuilder.append(this.getTimeInterval(), filter.getTimeInterval());
        
        return eqBuilder.isEquals();
    }
    
    @Override
    public int hashCode() {
    	final HashCodeBuilder hashBuilder = new HashCodeBuilder();

    	hashBuilder.append(this.getProject());
    	hashBuilder.append(this.getTimeInterval());
        
        return hashBuilder.toHashCode();
    }
    
    @Override
    public String toString() {
    	final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
    	
    	toStringBuilder.append(this.getProject()); 
    	toStringBuilder.append(this.getTimeInterval()); 
    	
    	return toStringBuilder.toString();
    }


}