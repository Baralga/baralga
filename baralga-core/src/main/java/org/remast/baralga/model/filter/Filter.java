package org.remast.baralga.model.filter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.repository.FilterVO;
import org.remast.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import static org.remast.util.DateUtils.quarterEndFor;
import static org.remast.util.DateUtils.quarterStartFor;

/**
 * Filter for selecting only those project activities which satisfy 
 * some selected criteria.
 * @author remast
 */
public class Filter {

    /** The predicates of the filter. */
    private final List<Predicate<ProjectActivity>> predicates = new ArrayList<>();
    
    /** The time interval to filter by. */
    private Interval timeInterval = new Interval(
    		org.remast.util.DateUtils.getNowAsDateTime().withMillisOfDay(0), 
    		org.remast.util.DateUtils.getNowAsDateTime().plusDays(1).withMillisOfDay(0)
    );

    /** 
     * The type of span the time interval belongs to.
     * @see SpanType
     */
	private SpanType spanType = SpanType.Day;

	/** The predicate to filter by time interval. */
    private Predicate<ProjectActivity> timeIntervalPredicate;

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
    	
        for (Predicate<ProjectActivity> predicate : predicates) {
            if (!predicate.test(activity)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the span type to filter by.
     * @param spanType the span type to set
     */
	public void setSpanType(final SpanType spanType) {
		this.spanType = spanType;
	}
	
    /**
     * Getter for the span type.
     * @return the span type
     */
	public SpanType getSpanType() {
		return this.spanType;
	}
    
    /**
     * Getter for the timeInterval.
     * @return the timeInterval
     */
    public Interval getTimeInterval() {
    	return this.timeInterval;
    }
 
    /**
     * Sets the timeInterval to filter by.
     * @param timeInterval the timeInterval to set
     */
    public void setTimeInterval(final Interval timeInterval) {
        this.timeInterval = timeInterval;

        if (this.timeIntervalPredicate != null) {
            this.predicates.remove(this.timeIntervalPredicate);
        }

        final Predicate<ProjectActivity> newTimeIntervalPredicate = new TimeIntervalPredicate(timeInterval);
        this.timeIntervalPredicate = newTimeIntervalPredicate;
        this.predicates.add(newTimeIntervalPredicate);
    }
    
	/** Initializes the time interval for the span type of the filter. */
	public void initTimeInterval() {
		DateTime now = DateUtils.getNowAsDateTime().withMillisOfDay(0);

		switch (spanType) {
		case Day:
			setTimeInterval(new Interval(now, now.plusDays(1)));
			break;
		case Week:
			now = now.withDayOfWeek(1);
			setTimeInterval(new Interval(now, now.plusWeeks(1)));
			break;
		case Month:
			now = now.withDayOfMonth(1);
			setTimeInterval(new Interval(now, now.plusMonths(1)));
			break;
        case Quarter:
            now = quarterStartFor(now);
            setTimeInterval(new Interval(now, quarterEndFor(now)));
            break;
		case Year:
			now = now.withDayOfYear(1);
			setTimeInterval(new Interval(now, now.plusYears(1)));
			break;
		}
	}

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        
        if (!(that instanceof Filter)) {
            return false;
        }
        
        final Filter filter = (Filter) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(this.getSpanType(), filter.getSpanType());
        eqBuilder.append(this.getTimeInterval(), filter.getTimeInterval());
        
        return eqBuilder.isEquals();
    }

    public FilterVO toVO() {
	    FilterVO filterVO = new FilterVO();
	    filterVO.setTimeInterval(timeInterval);
        return filterVO;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getSpanType(),
                this.getTimeInterval()
        );
    }
    
    @Override
    public String toString() {
    	final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
    	
    	toStringBuilder.append(this.getSpanType());
    	toStringBuilder.append(this.getTimeInterval()); 
    	
    	return toStringBuilder.toString();
    }

}
