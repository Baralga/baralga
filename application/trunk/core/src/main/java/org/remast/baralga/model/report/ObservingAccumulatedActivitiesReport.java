package org.remast.baralga.model.report;

import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.events.ProTrackEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;

public class ObservingAccumulatedActivitiesReport extends AccumulatedActivitiesReport implements Observer {

    /** The model. */
    private PresentationModel model;

    public ObservingAccumulatedActivitiesReport(final PresentationModel model) {
        super(model.getData());

        this.filter = model.getFilter();
        this.model = model;
        this.model.addObserver(this);

        this.accumulate();
    }

    public void update(Observable source, Object eventObject) {
        if (eventObject != null && eventObject instanceof ProTrackEvent) {
            final ProTrackEvent event = (ProTrackEvent) eventObject;
            switch (event.getType()) {

                case ProTrackEvent.PROJECT_ACTIVITY_ADDED:
                    ProjectActivity activity = (ProjectActivity) event.getData();
                    this.acummulateActivity(activity);
                    break;

                case ProTrackEvent.PROJECT_ACTIVITY_REMOVED:
                    this.accumulate();
                    break;

                case ProTrackEvent.PROJECT_ACTIVITY_CHANGED:
                    this.accumulate();
                    break;
            }
            setChanged();
            notifyObservers();
        }
    }

}
