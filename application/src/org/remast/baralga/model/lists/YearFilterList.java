package org.remast.baralga.model.lists;

import java.text.SimpleDateFormat;
import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class YearFilterList implements Observer {

    public static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy"); //$NON-NLS-1$

    private PresentationModel model;

    public static final String ALL_YEARS_DUMMY = "*"; //$NON-NLS-1$
    
    public static final FilterItem<String> ALL_YEARS_FILTER_ITEM = new FilterItem<String>(ALL_YEARS_DUMMY, Messages.getString("YearFilterList.AllYearsLabel")); //$NON-NLS-1$
    
    private EventList<FilterItem<String>> yearList;

    public YearFilterList(PresentationModel model) {
        this.model = model;
        this.yearList = new BasicEventList<FilterItem<String>>();

        this.model.addObserver(this);
        
        initialize();
    }

    private void initialize() {
        this.yearList.clear();
        this.yearList.add(ALL_YEARS_FILTER_ITEM);
        for(ProjectActivity activity : this.model.getData().getActivities()) {
            this.addYear(activity);
        }
    }

    public EventList<FilterItem<String>> getYearList() {
        return this.yearList;
    }

    public void update(Observable source, Object eventObject) {
        if (eventObject instanceof ProTrackEvent) {
            ProTrackEvent event = (ProTrackEvent) eventObject;

            switch (event.getType()) {
            
            case ProTrackEvent.PROJECT_ACTIVITY_ADDED:
                ProjectActivity activity = (ProjectActivity) event.getData();
                this.addYear(activity);
                break;
                
            case ProTrackEvent.PROJECT_ACTIVITY_REMOVED:
                this.initialize();
                break;
            }
        }
    }

    private void addYear(ProjectActivity activity) {
        FilterItem<String> year = new FilterItem<String>(YEAR_FORMAT.format(activity.getStart()));
        if(!this.yearList.contains(year))
            this.yearList.add(year);
    }

}
