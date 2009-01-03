package org.remast.baralga.gui.model;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Date;
import java.util.Observable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.lists.MonthFilterList;
import org.remast.baralga.gui.lists.ProjectFilterList;
import org.remast.baralga.gui.lists.WeekOfYearFilterList;
import org.remast.baralga.gui.lists.YearFilterList;
import org.remast.baralga.gui.model.edit.EditStack;
import org.remast.baralga.gui.model.io.DataBackup;
import org.remast.baralga.gui.model.report.HoursByDayReport;
import org.remast.baralga.gui.model.report.HoursByProjectReport;
import org.remast.baralga.gui.model.report.HoursByWeekReport;
import org.remast.baralga.gui.model.report.ObservingAccumulatedActivitiesReport;
import org.remast.baralga.model.ProTrack;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.io.ProTrackWriter;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/**
 * The model of the Baralga application. This is the model capturing both the state
 * of the application as well as the application logic.
 * For further information on the pattern see <a href="http://www.martinfowler.com/eaaDev/PresentationModel.html">presentation model</a>.
 * @author remast
 */
public class PresentationModel extends Observable {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

    /** The list of projects. */
    private EventList<Project> projectList;

    /** The list of project activities. */
    private EventList<ProjectActivity> activitiesList;

    /** The currently selected project. */
    private Project selectedProject;

    /** The description of the activity. */
    private String description;

    /** Flag indicating whether selected project is active or not. */
    private boolean active;

    /** Flag indicating whether data has been saved after last change. */
    private boolean dirty = false;

    /** Start date of activity. */
    private Date start;

    /** Stop date of activity. */
    private Date stop;

    /** The data file that is presented by this model. */
    private ProTrack data;

    /** Current activity filter. */
    private Filter filter;

    /** The stack of edit actions (for undo and redo). */
    private EditStack editStack;

    /**
     * Creates a new model.
     */
    public PresentationModel() {
        this.data = new ProTrack();
        this.projectList = new BasicEventList<Project>();
        this.activitiesList = new BasicEventList<ProjectActivity>();
        
        initialize();
    }

    /**
     * Initializes the model.
     */
    private void initialize() {
        this.active = this.data.isActive();
        this.start = this.data.getStartTime();
        this.selectedProject = this.data.getActiveProject();

        this.projectList.addAll(this.data.getProjects());
        this.activitiesList.addAll(this.data.getActivities());

        // Restore filter from settings
        // a) restore week of year, month and year
        this.filter = Settings.instance().restoreFromSettings();

        // b) restore project (can be done here only as we need to search all projects)
        final Long selectedProjectId = Settings.instance().getFilterSelectedProjectId();
        if (selectedProjectId != null) {
            filter.setProject(
                    this.data.findProjectById(selectedProjectId.longValue())
            );
        }

        this.description = Settings.instance().getLastDescription();
        
        // If there is a active project that has been started on another day,
        // we end it here.
        if (active && !org.apache.commons.lang.time.DateUtils.isSameDay(start, DateUtils.getNow())) {
            try {
                stop();
            } catch (ProjectActivityStateException e) {
                // Ignore
            }
        }

        // Edit stack
        if (editStack == null) {
            editStack = new EditStack(this);
            this.addObserver(editStack);
        }
    }

    /**
     * Add the given project.
     * @param project the project to add
     * @param source the source of the edit activity
     */
    public final void addProject(final Project project, final Object source) {
        getData().add(project);
        this.projectList.add(project);

        // Mark data as dirty
        this.dirty = true;

        final BaralgaEvent event = new BaralgaEvent(BaralgaEvent.PROJECT_ADDED, source);
        event.setData(project);

        notify(event);
    }

    /**
     * Remove the given project.
     * @param project the project to remove
     * @param source the source of the edit activity
     */
    public final void removeProject(final Project project, final Object source) {
        getData().remove(project);
        this.projectList.remove(project);

        // Mark data as dirty
        this.dirty = true;

        final BaralgaEvent event = new BaralgaEvent(BaralgaEvent.PROJECT_REMOVED, source);
        event.setData(project);

        notify(event);
    }

    /**
     * Start a project activity.
     * @throws ProjectActivityStateException if there is already a running project
     */
    public final void start() throws ProjectActivityStateException {
        if (getSelectedProject() == null) {
            throw new ProjectActivityStateException(textBundle.textFor("PresentationModel.NoActiveProjectSelectedError")); //$NON-NLS-1$
        }

        // Mark as active
        setActive(true);

        // Mark data as dirty
        this.dirty = true;

        // Set start time to now
        setStart(DateUtils.getNow());

        // Fire start event
        final BaralgaEvent event = new BaralgaEvent(BaralgaEvent.PROJECT_ACTIVITY_STARTED);
        notify(event);
    }

    /**
     * Helper method to notify all observers of an event.
     * @param event the event to forward to the observers
     */
    private void notify(final BaralgaEvent event) {
        setChanged();
        notifyObservers(event);
    }

    public void fireProjectActivityChangedEvent(final ProjectActivity changedActivity, final PropertyChangeEvent propertyChangeEvent) {
        final BaralgaEvent event = new BaralgaEvent(BaralgaEvent.PROJECT_ACTIVITY_CHANGED);
        event.setData(changedActivity);
        event.setPropertyChangeEvent(propertyChangeEvent);
        notify(event);
    }
    
    public final void ping() {
        if (isActive()) {
            Settings.instance().setPingTime(DateUtils.getNow());
        }
    }

    /**
     * Stop a project activity.
     * @throws ProjectActivityStateException if there is no running project
     * @see #stop(boolean)
     */
    public final void stop() throws ProjectActivityStateException {
        // Stop with notifying observers.
        stop(true);
    }

    /**
     * Stop a project activity.
     * @throws ProjectActivityStateException if there is no running project
     */
    public final void stop(final boolean notifyObservers) throws ProjectActivityStateException {
        if (!isActive()) {
            throw new ProjectActivityStateException(textBundle.textFor("PresentationModel.NoActiveProjectError")); //$NON-NLS-1$
        }

        final Date now = DateUtils.getNow();

        BaralgaEvent eventOnEndDay = null;
        Date stop2 = null;

        // If start is on a different day from now end the activity at 0:00 one day after start.
        // Also make a new activity from 0:00 the next day until the stop time of the next day.
        if (!org.apache.commons.lang.time.DateUtils.isSameDay(start, now)) {
            DateTime dt = new DateTime(start);
            dt = dt.plusDays(1);

            stop = dt.toDateMidnight().toDate();

            stop2 = dt.toDate();
            final Date start2 = stop;

            final ProjectActivity activityOnEndDay = new ProjectActivity(start2, stop2, getSelectedProject());
            activityOnEndDay.setDescription(this.description);
            getData().getActivities().add(activityOnEndDay);
            this.activitiesList.add(activityOnEndDay);

            // Create Event for Project Activity
            eventOnEndDay  = new BaralgaEvent(BaralgaEvent.PROJECT_ACTIVITY_ADDED);
            eventOnEndDay.setData(activityOnEndDay);
        } else {
            stop = now;
        }

        final ProjectActivity activityOnStartDay = new ProjectActivity(start, stop, getSelectedProject());
        activityOnStartDay.setDescription(this.description);
        getData().getActivities().add(activityOnStartDay);
        this.activitiesList.add(activityOnStartDay);

        // Clear old activity
        clearProjectActivityAttributes();

        // Mark data as dirty
        this.dirty = true;

        if (notifyObservers) {
            // Create Event for Project Activity
            BaralgaEvent event  = new BaralgaEvent(BaralgaEvent.PROJECT_ACTIVITY_ADDED);
            event.setData(activityOnStartDay);
            notify(event);

            if (eventOnEndDay != null)  {
                notify(eventOnEndDay);
                stop = stop2;
            }

            // Create Stop Event
            event = new BaralgaEvent(BaralgaEvent.PROJECT_ACTIVITY_STOPPED);
            notify(event);
        }
    }

    /**
     * 
     */
    private void clearProjectActivityAttributes() {
        description = StringUtils.EMPTY;
        Settings.instance().setLastDescription(StringUtils.EMPTY);
        setActive(false);
        Settings.instance().setPingTime(null);
        start = null;
    }

    /**
     * Changes to the given project.
     * @param activeProject the new active project
     */
    public final void changeProject(final Project activeProject) {
        // If there's no change we're done.
        if (ObjectUtils.equals(getSelectedProject(), activeProject)) {
            return;
        }

        // Store previous project
        final Project previousProject = getSelectedProject();

        // Set selected project to new project
        this.selectedProject = activeProject;

        // Mark data as dirty
        this.dirty = true;

        // Set active project to new project
        this.data.setActiveProject(activeProject);

        final Date now = DateUtils.getNow();

        // If a project is currently running we create a new project activity.
        if (isActive()) {
            // 1. Stop the running project.
            setStop(now);

            // 2. Track recorded project activity.
            final ProjectActivity activity = new ProjectActivity(start, stop, previousProject);
            activity.setDescription(description);

            getData().getActivities().add(activity);
            this.activitiesList.add(activity);

            // Clear description
            description = StringUtils.EMPTY;
            Settings.instance().setLastDescription(StringUtils.EMPTY);

            // 3. Broadcast project activity event.
            final BaralgaEvent event = new BaralgaEvent(BaralgaEvent.PROJECT_ACTIVITY_ADDED);
            event.setData(activity);
            notify(event);
        }

        // Set start time to now.
        // :INFO: We need to clone the date so we don't work with the 
        // exact same reference
        setStart((Date) now.clone());

        // Fire project changed event
        final BaralgaEvent event = new BaralgaEvent(BaralgaEvent.PROJECT_CHANGED);
        event.setData(activeProject);
        notify(event);
    }

    /**
     * Save the model.
     * @throws Exception on error during saving
     */
    public final void save() throws Exception {
        // If there are no changes there's nothing to do.
        if (!dirty)  {
            return;
        }

        // Save data to disk.
        final ProTrackWriter writer = new ProTrackWriter(data);

        final File proTrackFile = new File(Settings.getProTrackFileLocation());
        DataBackup.createBackup(proTrackFile);

        writer.write(proTrackFile);        
    }

    /**
     * Add a new activity to the model.
     * @param activity the activity to add
     */
    public final void addActivity(final ProjectActivity activity, final Object source) {
        getData().getActivities().add(activity);
        this.getActivitiesList().add(activity);

        // Mark data as dirty
        this.dirty = true;

        // Fire event
        final BaralgaEvent event = new BaralgaEvent(BaralgaEvent.PROJECT_ACTIVITY_ADDED, source);
        event.setData(activity);
        notify(event);
    }

    /**
     * Remove an activity from the model.
     * @param activity the activity to remove
     */
    public final void removeActivity(final ProjectActivity activity, final Object source) {
        getData().getActivities().remove(activity);
        this.getActivitiesList().remove(activity);

        // Mark data as dirty
        this.dirty = true;

        // Fire event
        final BaralgaEvent event = new BaralgaEvent(BaralgaEvent.PROJECT_ACTIVITY_REMOVED, source);
        event.setData(activity);
        notify(event);
    }

    /**
     * Getter for the list of projects.
     * @return the list with all projects
     */
    public EventList<Project> getProjectList() {
        return projectList;
    }

    /**
     * Getter for the list of project activities.
     * @return the list with all project activities
     */
    public EventList<ProjectActivity> getActivitiesList() {
        return activitiesList;
    }

    public ProjectFilterList getProjectFilterList() {
        return new ProjectFilterList(this);
    }

    /**
     * Get all years in which there are project activities.
     * @return List of years with activities as String.
     */
    public YearFilterList getYearFilterList() {
        return new YearFilterList(this);
    }

    /**
     * Get all months in which there are project activities.
     * @return List of months with activities as String.
     */
    public MonthFilterList getMonthFilterList() {
        return new MonthFilterList(this);
    }

    /**
     * Get all weeks in which there are project activities.
     * @return List of weeks with activities as String.
     */
    public WeekOfYearFilterList getWeekFilterList() {
        return new WeekOfYearFilterList(this);
    }

    public ObservingAccumulatedActivitiesReport getFilteredReport() {
        return new ObservingAccumulatedActivitiesReport(this);
    }

    public HoursByWeekReport getHoursByWeekReport() {
        return new HoursByWeekReport(this);
    }

    public HoursByDayReport getHoursByDayReport() {
        return new HoursByDayReport(this);
    }

    public HoursByProjectReport getHoursByProjectReport() {
        return new HoursByProjectReport(this);
    }

    /**
     * Gets the start of the current activity.
     * @return the start
     */
    public Date getStart() {
        return start;
    }

    /**
     * Sets the start of a new activity.
     * @param start the start to set
     */
    private void setStart(final Date start) {
        this.start = start;
        this.data.setStartTime(start);
    }

    
    /**
     * @return the stop
     */
    public Date getStop() {
        return stop;
    }

    /**
     * @param stop the stop to set
     */
    public void setStop(final Date stop) {
        this.stop = stop;
    }

    /**
     * Checks whether a project activity is currently running.
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(final boolean active) {
        this.active = active;
        // Propagate to data.
        this.data.setActive(active);
    }

    /**
     * @return the activeProject
     */
    public Project getSelectedProject() {
        return selectedProject;
    }

    /**
     * @return the data
     */
    public ProTrack getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(final ProTrack data) {
        this.data = data;
        initialize();
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     * @param source the source of the new filter
     */
    public void setFilter(final Filter filter, final Object source) {
        this.filter = filter;

        // Fire event
        final BaralgaEvent event = new BaralgaEvent(BaralgaEvent.FILTER_CHANGED, source);
        event.setData(filter);
        notify(event);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the editStack
     */
    public EditStack getEditStack() {
        return editStack;
    }

    /**
     * @return the dirty
     */
    public final boolean isDirty() {
        return dirty;
    }

    /**
     * @param dirty the dirty to set
     */
    public final void setDirty(final boolean dirty) {
        this.dirty = dirty;
    }

}
