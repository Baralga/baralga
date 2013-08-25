package org.remast.baralga.gui.model.report;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Report for the working hours by project.
 * @author remast
 */
public class HoursByProjectReport {

    /** The model. */
    private PresentationModel model;

    /** The bus to publish changes of the report. */
    private EventBus eventBus = new EventBus();

    private SortedList<HoursByProject> hoursByProjectList;

    public HoursByProjectReport(final PresentationModel model) {
        this.model = model;
        this.model.getEventBus().register(this);
        this.hoursByProjectList = new SortedList<HoursByProject>(new BasicEventList<HoursByProject>());

        calculateHours();
    }
    
    /**
     * Getter for the event bus.
     * @return the event bus
     */
    public EventBus getEventBus() {
    	return eventBus;
    }
    
    public void calculateHours() {
        this.hoursByProjectList.clear();

        for (ProjectActivity activity : this.model.getActivitiesList()) {
            this.addHours(activity);
        }
    }

    public void addHours(final ProjectActivity activity) {
        final HoursByProject newHoursByProject = new HoursByProject(activity.getProject(), activity.getDuration());

        if (this.hoursByProjectList.contains(newHoursByProject)) {
            HoursByProject HoursByProject = this.hoursByProjectList.get(hoursByProjectList.indexOf(newHoursByProject));
            HoursByProject.addHours(newHoursByProject.getHours());
        } else {
            this.hoursByProjectList.add(newHoursByProject);
        }

    }

    public SortedList<HoursByProject> getHoursByProject() {
        return hoursByProjectList;
    }

    /**
     * {@inheritDoc}
     */
    @Subscribe 
    public void update(final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;
        switch (event.getType()) {

            case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
            case BaralgaEvent.DATA_CHANGED:
            case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
            case BaralgaEvent.FILTER_CHANGED:
                calculateHours();
                break;
        }
        eventBus.post(this);
    }

}
