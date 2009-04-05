package org.remast.baralga.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.remast.baralga.FormatUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * An activity for a project.
 * 
 * Invariants of this class (not enforced, yet):
 * - start time must not be after end time
 * - start and end date of an activity must always be on the same day
 * unless end date is at 0:00h. In that situation end date is on the following date to the start date.
 * 
 * @author remast
 */
@XStreamAlias("projectActivity")//$NON-NLS-1$
public class ProjectActivity implements Serializable, Comparable<ProjectActivity> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** Start date of this activity. */
    private final DateTime start;

    /** End date of this activity. */
    private final DateTime end;

    /** The project associated with this activity. */
    private final Project project;

    /** The description of this activity. */
    private final String description;

    public static final String PROPERTY_START = "org.remast.baralga.model.ProjectActivity.start";

    public static final String PROPERTY_END = "org.remast.baralga.model.ProjectActivity.end";

    /** Artificial property if the day in year of the activity changes. */
    public static final String PROPERTY_DATE = "org.remast.baralga.model.ProjectActivity.date";

    public static final String PROPERTY_PROJECT = "org.remast.baralga.model.ProjectActivity.project";

    public static final String PROPERTY_DESCRIPTION = "org.remast.baralga.model.ProjectActivity.description";

    /**
     * Creates a new {@link ProjectActivity} with an empty description.
     * 
     * @throws IllegalArgumentException if end time is before start time
     */
    public ProjectActivity(final DateTime start, final DateTime end, final Project project) {
        this(start, end, project, null);
    }

    /**
     * Creates a new {@link ProjectActivity}.
     * 
     * @throws IllegalArgumentException if end time is before start time
     */
    public ProjectActivity(final DateTime start, final DateTime end, final Project project,
            final String description) {
        if(start.isAfter(end)) {
            throw new IllegalArgumentException("End time may not be before start time!");
        }
        this.start = start;
        this.end = end;
        this.project = project;
        this.description = description;
    }
    
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public ProjectActivity withDescription(final String description) {
        return new ProjectActivity(getStart(), getEnd(), getProject(), description);
    }

    /**
     * Sets the day of the activity.
     * 
     * @param day the new activity day.
     *   Hours, minutes, seconds and so on in the passed value are ignored.
     */
    public ProjectActivity withDay(DateTime day) {
        DateTime start = getStart();
        start = start.withYear(day.getYear()).withMonthOfYear(day.getMonthOfYear())
            .withDayOfMonth(day.getDayOfMonth());
        
        
        DateTime end = getEnd();
        end = end.withYear(day.getYear()).withMonthOfYear(day.getMonthOfYear())
            .withDayOfMonth(day.getDayOfMonth());
        if(end.getHourOfDay() == 0 && end.getMinuteOfHour() == 0) {
            end = end.plusDays(1);
        }
        
        return new ProjectActivity(start, end, getProject(), getDescription());
    }
    
    /**
     * Returns the day of the activity
     * 
     * Hours, minutes, seconds of the returned value are to be ignored.
     */
    public DateTime getDay() {
        return getStart().withMillisOfDay(0);
    }

    /**
     * @return the end
     */
    public DateTime getEnd() {
        return end;
    }
    
    /**
     * Sets the end hours and minutes while respecting the class invariants.
     * 
     * Note: When setting the end date to 0:00h it is always supposed to mean
     * midnight i.e. 0:00h the next day!
     * 
     * @throws IllegalArgumentException if end time is before start time
     */
    public ProjectActivity withEndTime(final int hours, final int minutes) {
        DateTime endDt = getEnd();
        if( hours == endDt.getHourOfDay() && minutes == endDt.getMinuteOfHour() ) {
            return this;
        }
        
        if(endDt.getHourOfDay() == 0 && endDt.getMinuteOfHour() == 0) { // adjust day if old end was on midnight
            endDt = endDt.minusDays( 1 );
        } else if(hours == 0 && minutes == 0) { // adjust day if new end is on midnight
            endDt = endDt.plusDays(1);
        }
        
        endDt = endDt.withHourOfDay(hours).withMinuteOfHour(minutes);
        
        if( endDt.isBefore( getStart() ) ) {
            throw new IllegalArgumentException("End time may not be before start time!");
        }
        return new ProjectActivity(getStart(), endDt, getProject(), getDescription());
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project
     *            the project to set
     */
    public ProjectActivity withProject(final Project project) {
        return new ProjectActivity(getStart(), getEnd(), project, getDescription());
    }


    /**
     * @return the start
     */
    public DateTime getStart() {
        return start;
    }
    
    /**
     * Sets the start hours and minutes while respecting the class invariants.
     * 
     * @throws IllegalArgumentException if end time is before start time
     */
    public ProjectActivity withStartTime(final int hours, final int minutes) {
        DateTime startDt = getStart();
        if( hours == startDt.getHourOfDay() && minutes == startDt.getMinuteOfHour() ) {
            return this;
        }
        
        startDt = startDt.withHourOfDay(hours).withMinuteOfHour(minutes);
        
        if( startDt.isAfter( getEnd() ) ) {
            throw new IllegalArgumentException("End time may not be before start time!");
        }
        return new ProjectActivity(startDt, getEnd(), getProject(), getDescription());
    }
    
    @Override
    public String toString() {
        return FormatUtils.formatTime(getStart()) + " "
                + FormatUtils.formatTime(getStart()) + " - " + FormatUtils.formatTime(getEnd()) + " ("
                + FormatUtils.durationFormat.format(this.getDuration()) + "h) " + this.project;
    }

    @Override
    public int compareTo(final ProjectActivity activity) {
        if (activity == null) {
            return 0;
        }

        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        return this.getStart().compareTo(activity.getStart()) * -1;
    }
    
    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        
        if (that == null || !(that instanceof ProjectActivity)) {
            return false;
        }
        
        final ProjectActivity activity = (ProjectActivity) that;
        
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        
        eqBuilder.append(this.getStart(), activity.getStart());
        eqBuilder.append(this.getEnd(), activity.getEnd());
        eqBuilder.append(this.getProject(), activity.getProject());
        
        return eqBuilder.isEquals();
    }
    
    @Override
    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        
        hashCodeBuilder.append(this.getStart());
        hashCodeBuilder.append(this.getEnd());
        hashCodeBuilder.append(this.getProject());
        
        return hashCodeBuilder.toHashCode();
    }
    
    /**
     * Calculate the duration of the given activity in decimal hours.
     * @return decimal value of the duration (e.g. for 30 minutes, 0.5 and so on)
     */    
    public final double getDuration() {
        final long timeMilliSec = end.getMillis() - start.getMillis();
        final long timeMin = timeMilliSec / 1000 / 60;
        final long hours = timeMin / 60;

        final long mins = timeMin % 60;
        final double minsD = Math.round(mins * (1 + 2.0 / 3.0)) / 100.0;

        return hours + minsD;
    }
}
