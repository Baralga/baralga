package org.remast.baralga.gui.model.report;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.report.AccumulatedActivitiesReport;

import com.google.common.eventbus.Subscribe;

public class ObservingAccumulatedActivitiesReport extends AccumulatedActivitiesReport {

    /** The model. */
    private final PresentationModel model;

    public ObservingAccumulatedActivitiesReport(final PresentationModel model) {
        super(model.getActivitiesList(), model.getFilter());

        this.model = model;
        this.model.getEventBus().register(this);

        this.accumulate();
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
        case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
        case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
            this.accumulate();
            break;
            
        case BaralgaEvent.FILTER_CHANGED:
            final Filter newFilter = (Filter) event.getData();
            setData(model.getActivitiesList());
            setFilter(newFilter);
            break;

        case BaralgaEvent.DATA_CHANGED:
            setData(model.getActivitiesList());
            break;
        }
        
        setChanged();
        notifyObservers();
    }

}
