package org.remast.baralga.model;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.model.edit.EditStack;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.io.ProTrackWriter;
import org.remast.baralga.model.lists.MonthFilterList;
import org.remast.baralga.model.lists.ProjectFilterList;
import org.remast.baralga.model.lists.YearFilterList;
import org.remast.baralga.model.report.HoursByWeekReport;
import org.remast.baralga.model.report.ObservingFilteredReport;
import org.remast.baralga.model.utils.ProTrackUtils;
import org.remast.util.DateUtils;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class PresentationModel extends Observable {
    
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
    
    /** Start date of activity. */
    private Date start;
    
    /** Stop date of activity. */
    private Date stop;
    
    private ProTrack data;
    
    /** Current activity filter. */
    private Filter filter;

    private EditStack editStack;

    public PresentationModel() {
        this.data = new ProTrack();
        this.projectList = new BasicEventList<Project>();
        this.activitiesList = new BasicEventList<ProjectActivity>();
        
        initialize();
    }
    
    private void initialize() {
        this.active = this.data.isActive();
        this.start = this.data.getStartTime();
        this.selectedProject = this.data.getActiveProject();
        
        this.projectList.addAll(this.data.getProjects());
        this.activitiesList.addAll(this.data.getActivities());
        
        this.filter = FilterUtils.restoreFromSettings();
        this.description = Settings.instance().getLastDescription();

        final Long selectedProjectId = Settings.instance().getFilterSelectedProjectId();
        if (selectedProjectId != null) {
            filter.setProject(this.data.findProjectById(selectedProjectId.longValue()));
        }
        
        // If there is a active project that has been started on another day,
        // we end it here.
        if (active && !org.apache.commons.lang.time.DateUtils.isSameDay(start, DateUtils.getNow())) {
            try {
                stop();
            } catch (ProjectStateException e) {
            }
        }

        // Edit stack
        if (editStack == null) {
            editStack = new EditStack();
            this.addObserver(editStack);
        }
    }

    public void addProject(Project project) {
        getData().add(project);
        this.projectList.add(project);
        
        final ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_ADDED);
        event.setData(project);
        
        notify(event);
    }

    public void removeProject(final Project project) {
        getData().remove(project);
        this.projectList.remove(project);
        
        final ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_REMOVED);
        event.setData(project);
        
        notify(event);
    }

    public void start() throws ProjectStateException {
        if(getSelectedProject() == null) {
            throw new ProjectStateException(Messages.getString("PresentationModel.NoActiveProjectSelectedError")); //$NON-NLS-1$
        }
        
        setActive(true);

        // Set start time to now
        setStart(DateUtils.getNow());
        
        final ProTrackEvent event = new ProTrackEvent(ProTrackEvent.START);
        notify(event);
    }
    
    private void notify(final ProTrackEvent event) {
        setChanged();
        notifyObservers(event);
    }
    
    public void fireProjectActivityChangedEvent(final ProjectActivity changedActivity, final String propertyIdentifier) {
        final ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_ACTIVITY_CHANGED);
        event.setData(changedActivity);
        event.setPropertyHint(propertyIdentifier);
        notify(event);
    }

    public void stop() throws ProjectStateException {
        if (!isActive()) {
            throw new ProjectStateException(Messages.getString("PresentationModel.NoActiveProjectError")); //$NON-NLS-1$
        }

        // If start is on a different day from now end the activity at 0:00 one day after start.
        Date now = DateUtils.getNow();
        if (!org.apache.commons.lang.time.DateUtils.isSameDay(start, now)) {
            DateTime dt = new DateTime(start);
            dt = dt.plusDays(1);
            stop = dt.toDateMidnight().toDate();
        } else {
            stop = now;
        }
        
        
        final ProjectActivity activity = new ProjectActivity(start, stop, getSelectedProject());
        activity.setDescription(this.description);
        getData().getActivities().add(activity);
        this.activitiesList.add(activity);
        
        // Clear old activity
        description = StringUtils.EMPTY;
        setActive(false);
        start = null;

        // Create Stop Event
        ProTrackEvent event = new ProTrackEvent(ProTrackEvent.STOP);
        notify(event);
        
        // Create Event for Project Activity
        event  = new ProTrackEvent(ProTrackEvent.PROJECT_ACTIVITY_ADDED);
        event.setData(activity);
        notify(event);
    }

    /**
     * Changes to the given project.
     * @param activeProject the new active project
     */
    public void changeProject(final Project activeProject) {
        // If there's no change we're done.
        if (ObjectUtils.equals(getSelectedProject(), activeProject)) {
            return;
        }

        // Store previous project
        final Project previousProject = getSelectedProject();

        // Set selected project to new project
        this.selectedProject = activeProject;

        // Set active project to new project
        this.data.setActiveProject(activeProject);

        final Date now = DateUtils.getNow();

        // If a project is currently running we create a new project activity.
        if (isActive()) {
            // 1. Stop the running project.
            setStop(now);

            // 2. Track recorded project activity.
            final ProjectActivity activity = new ProjectActivity(start, getStop(), previousProject);
            activity.setDescription(description);
            
            getData().getActivities().add(activity);
            this.activitiesList.add(activity);
            
            // Clear description
            description = StringUtils.EMPTY;
            Settings.instance().setLastDescription(StringUtils.EMPTY);

            // 3. Broadcast project activity event.
            final ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_ACTIVITY_ADDED);
            event.setData(activity);
            notify(event);
        }
        
        // Set start time to now.
        // :INFO: We need to clone the date so we don't work with the 
        // exact same reference
        setStart((Date) now.clone());

        // Fire Project changed event
        final ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_CHANGED);
        event.setData(activeProject);
        notify(event);
    }

    /**
     * Save the model.
     * @throws Exception
     */
    public void save() throws Exception {
        final ProTrackWriter writer = new ProTrackWriter(getData());
        
        ProTrackUtils.checkOrCreateProTrackDir();
        
        final File proTrackFile = new File(Settings.getProTrackFileLocation());
        writer.write(proTrackFile);
    }

    /**
     * Add a new activity to the model.
     * @param activity the activity to add
     */
    public void addActivity(final ProjectActivity activity) {
        getData().getActivities().add(activity);
        this.getActivitiesList().add(activity);
        
        // Fire event
        ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_ACTIVITY_ADDED);
        event.setData(activity);
        notify(event);
    }

    /**
     * Remove an activity from the model.
     * @param activity the activity to remove
     */
    public void removeActivity(final ProjectActivity activity) {
        getData().getActivities().remove(activity);
        this.getActivitiesList().remove(activity);
        
        // Fire event
        ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_ACTIVITY_REMOVED);
        event.setData(activity);
        notify(event);
    }


    public EventList<Project> getProjectList() {
        return projectList;
    }

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
    
    public ObservingFilteredReport getFilteredReport() {
        return new ObservingFilteredReport(this);
    }
    
    public HoursByWeekReport getHoursByWeekReport() {
        return new HoursByWeekReport(this);
    }

    /**
     * @return the start
     */
    public Date getStart() {
        return start;
    }

    /**
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
    private void setStop(final Date stop) {
        this.stop = stop;
    }
    
    /**
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
     */
    public void setFilter(final Filter filter) {
        this.filter = filter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
