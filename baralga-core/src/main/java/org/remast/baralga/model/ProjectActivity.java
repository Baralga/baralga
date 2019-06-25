package org.remast.baralga.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.remast.baralga.FormatUtils;
import org.remast.text.DurationFormat;

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
public class ProjectActivity implements Serializable, Comparable<ProjectActivity> {

    private static final long serialVersionUID = 1L;

    /** The unique identifier of the project. */
    private long id;

    /** Start date of this activity. */
    private LocalDateTime start;

    /** End date of this activity. */
    private LocalDateTime end;

    /** The project associated with this activity. */
    private Project project;

    /** The description of this activity. */
    private String description;

    public static final String PROPERTY_START = "org.remast.baralga.model.ProjectActivity.start";

    public static final String PROPERTY_END = "org.remast.baralga.model.ProjectActivity.end";

    /** Artificial property if the day in year of the activity changes. */
    public static final String PROPERTY_DATE = "org.remast.baralga.model.ProjectActivity.date";

    public static final String PROPERTY_PROJECT = "org.remast.baralga.model.ProjectActivity.project";

    public static final String PROPERTY_DESCRIPTION = "org.remast.baralga.model.ProjectActivity.description";
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
    	this.id = id;
    }

    /**
     * Creates a new {@link ProjectActivity} with an empty description.
     * @throws IllegalArgumentException if end time is before start time
     */
    public ProjectActivity(final LocalDateTime start, final LocalDateTime end, final Project project) {
        this(start, end, project, null);
    }

    /**
     * Creates a new {@link ProjectActivity}.     *
     * @throws IllegalArgumentException if end time is before start time
     */
    public ProjectActivity(final LocalDateTime start, final LocalDateTime end, final Project project,
            final String description) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("End time may not be before start time!");
        }
        this.start = start;
        this.end = end;
        this.project = project;
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        if (StringUtils.equals(this.description, description)) {
            return;
        }
        
        this.description = description;
    }

    /**
     * Sets the day of the activity.
     * @param day the new activity day.
     *   Hours, minutes, seconds and so on in the passed value are ignored.
     */
    public void setDay(final LocalDateTime day) {
        LocalDateTime newStartDay = getStart();
        this.start = newStartDay.withYear(day.getYear()).withMonth(day.getMonthValue())
            .withDayOfMonth(day.getDayOfMonth());

        LocalDateTime newEndDay = getEnd();
        newEndDay = newEndDay.withYear(day.getYear()).withMonth(day.getMonthValue())
            .withDayOfMonth(day.getDayOfMonth());
        if (newEndDay.getHour() == 0 && newEndDay.getHour() == 0) {
            newEndDay = newEndDay.plusDays(1);
        }
        
        this.end = newEndDay;
    }
    
    /**
     * Returns the day of the activity. 
     * Hours, minutes, seconds of the returned value are to be ignored.
     */
    public LocalDateTime getDay() {
        return getStart().withNano(0).withSecond(0);
    }

    public LocalDateTime getEnd() {
        return end;
    }
    
    /**
     * Sets the end hours and minutes while respecting the class invariants.
     * 
     * Note: When setting the end date to 0:00h it is always supposed to mean
     * midnight i.e. 0:00h the next day!
     * @throws IllegalArgumentException if end time is before start time
     */
    public void setEndTime(final int hours, final int minutes) {
        LocalDateTime endDate = getEnd();
        if (hours == endDate.getHour() && minutes == endDate.getMinute()) {
            return;
        }
        
        if (endDate.getHour() == 0 && endDate.getMinute() == 0) { // adjust day if old end was on midnight
            endDate = endDate.minusDays(1);
        } else if (hours == 0 && minutes == 0) { // adjust day if new end is on midnight
            endDate = endDate.plusDays(1);
        }
        
        endDate = endDate.withHour(hours).withMinute(minutes);
        
        if (endDate.isBefore(getStart())) {
            throw new IllegalArgumentException("End time may not be before start time!");
        }
        
        this.end = endDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(final Project project) {
        if (Objects.equals(this.project, project)) {
            return;
        }
        
        this.project = project;
    }

    public LocalDateTime getStart() {
        return start;
    }
    
    /**
     * Sets the start hours and minutes while respecting the class invariants.
     * @param hours the hours
     * @param minutes the minutes
     * @throws IllegalArgumentException if end time is before start time
     */
    public void setStartTime(final int hours, final int minutes) {
        LocalDateTime startTime = getStart();
        if (hours == startTime.getHour() && minutes == startTime.getMinute()) {
            return;
        }
        
        startTime = startTime.withHour(hours).withMinute(minutes);
        
        if (startTime.isAfter(getEnd())) {
            throw new IllegalArgumentException("End time may not be before start time!");
        }
        
        this.start = startTime;
    }
    
    @Override
    public String toString() {
        return FormatUtils.formatDay(getStart()) + "   "
                + FormatUtils.formatTime(getStart()) + " - " + FormatUtils.formatTime(getEnd()) + " ("
                + new DurationFormat().format(this.getDuration()) + " h) " + this.project;
    }

    @Override
    public int compareTo(final ProjectActivity activity) {
        if (activity == null) {
            return 0;
        }

        // Sort by start date but the other way round. That way the latest
        // activity is always on top.
        final LocalDateTime startDateTime =  this.getDay().withHour(getStart().getHour()).withMinute(getStart().getMinute());
        final LocalDateTime startDateTimeOther =  activity.getDay().withHour(activity.getStart().getHour()).withMinute(activity.getStart().getMinute());

        return startDateTime.compareTo(startDateTimeOther) * -1;
    }
    
    /**
     * Calculate the duration of the given activity in decimal hours.
     * @return decimal value of the duration (e.g. for 30 minutes, 0.5 and so on)
     */    
    public final double getDuration() {
        final long timeMilliSec = end.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli() - start.toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
        final double timeMin = timeMilliSec / 1000.0 / 60.0;
        final double hours = timeMin / 60.0;
        return hours;
    }

}
