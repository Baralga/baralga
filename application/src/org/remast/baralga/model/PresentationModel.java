package org.remast.baralga.model;

import java.io.File;
import java.util.Date;
import java.util.Observable;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.gui.events.ProTrackEvent;
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
    
    /** Flag indicating whether selected project is active or not. */
    private boolean active;
    
    /** Start date of activity. */
    private Date start;
    
    /** Stop date of activity. */
    private Date stop;
    
    private ProTrack data;
    
    /** Current activity filter. */
    private Filter<ProjectActivity> filter;

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
    }

    public void addProject(Project project) {
        getData().add(project);
        this.projectList.add(project);
        
        ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_ADDED);
        event.setData(project);
        
        notify(event);
    }

    public void removeProject(final Project project) {
        getData().remove(project);
        this.projectList.remove(project);
        
        ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_REMOVED);
        event.setData(project);
        
        notify(event);
    }

    public void start() throws ProjectStateException {
        if(getSelectedProject() == null)
            throw new ProjectStateException(Messages.getString("PresentationModel.NoActiveProjectSelectedError")); //$NON-NLS-1$
        
        setActive(true);

        // Set start time to now
        setStart(DateUtils.getNow());
        
        ProTrackEvent event = new ProTrackEvent(ProTrackEvent.START);
        notify(event);
    }
    
    private void notify(ProTrackEvent event) {
        setChanged();
        notifyObservers(event);
    }
    
    public void fireProTrackActivityChangedEvent(ProjectActivity changedActivity) {
        ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_ACTIVITY_CHANGED);
        event.setData(changedActivity);
        notify(event);
    }

    public void stop() throws ProjectStateException {
        if(!isActive())
            throw new ProjectStateException(Messages.getString("PresentationModel.NoActiveProjectError")); //$NON-NLS-1$
        
        setStop(DateUtils.getNow());
        setActive(false);
        
        ProjectActivity activity = new ProjectActivity(start, getStop(), getSelectedProject());
        getData().getActivities().add(activity);
        this.activitiesList.add(activity);
        
        // Create Stop Event
        ProTrackEvent event = new ProTrackEvent(ProTrackEvent.STOP);
        notify(event);
        
        // Create Event for Project Activity
        event  = new ProTrackEvent(ProTrackEvent.PROJECT_ACTIVITY_ADDED);
        event.setData(activity);
        notify(event);
    }

    /**
     * @param activeProject the activeProject to set
     */
    public void changeProject(Project activeProject) {
        if(getSelectedProject() != activeProject) {
            this.selectedProject = activeProject;
            this.data.setActiveProject(activeProject);
            
            final Date now = DateUtils.getNow();
            
            // check if project is running
            if(isActive()) {
                // 1. Stop the running project.
                setStop(now);
                
                // 2. Track recorded project activity.
                ProjectActivity activity = new ProjectActivity(start, getStop(), getSelectedProject());
                getData().getActivities().add(activity);
                this.activitiesList.add(activity);
                
                // 3. Broadcast project activity event.
                ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_ACTIVITY_ADDED);
                event.setData(activity);
                notify(event);
            }
            // set start time to now
            // INFO: We need to clone the date so we don't work with the 
            // exact same reference
            setStart((Date) now.clone());
            
            // fire Project changed event
            ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_CHANGED);
            event.setData(activeProject);
            notify(event);
        }
    }

    /**
     * Save the model.
     * @throws Exception
     */
    public void save() throws Exception {
        ProTrackWriter writer = new ProTrackWriter(getData());
        
        ProTrackUtils.checkOrCreateProTrackDir();
        
        File proTrackFile = new File(Settings.getProTrackFileLocation());
        writer.write(proTrackFile);
    }

    public void addActivity(final ProjectActivity activity) {
        getData().getActivities().add(activity);
        this.getActivitiesList().add(activity);
        
        // Fire event
        ProTrackEvent event = new ProTrackEvent(ProTrackEvent.PROJECT_ACTIVITY_ADDED);
        event.setData(activity);
        notify(event);
    }

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
    private void setStart(Date start) {
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
    private void setStop(Date stop) {
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
    public void setActive(boolean active) {
        this.active = active;
        
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
    public void setData(ProTrack data) {
        this.data = data;
        initialize();
    }
    
    /**
     * @return the filter
     */
    public Filter<ProjectActivity> getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(Filter<ProjectActivity> filter) {
        this.filter = filter;
    }
}
