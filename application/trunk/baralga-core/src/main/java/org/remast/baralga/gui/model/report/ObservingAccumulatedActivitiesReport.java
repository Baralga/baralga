package org.remast.baralga.gui.model.report;

import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.report.AccumulatedActivitiesReport;

public class ObservingAccumulatedActivitiesReport extends AccumulatedActivitiesReport implements Observer {

    /** The model. */
    private final PresentationModel model;

    public ObservingAccumulatedActivitiesReport(final PresentationModel model) {
        super(model.getData());

        this.filter = model.getFilter();
        this.model = model;
        this.model.addObserver(this);

        this.accumulate();
    }

    public void update(final Observable source, final Object eventObject) {
        if (eventObject == null || !(eventObject instanceof BaralgaEvent)) {
            return;
        }

        final BaralgaEvent event = (BaralgaEvent) eventObject;
        switch (event.getType()) {

        case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
            ProjectActivity activity = (ProjectActivity) event.getData();
            this.acummulateActivity(activity);
            break;

        case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
            this.accumulate();
            break;

        case BaralgaEvent.FILTER_CHANGED:
            final Filter newFilter = (Filter) event.getData();
            setFilter(newFilter);
            break;

        case BaralgaEvent.DATA_CHANGED:
            setData(model.getData());
            break;
        }
        
        setChanged();
        notifyObservers();
    }

}
