package org.remast.baralga.gui.model.report;

import java.util.Observable;
import java.util.Observer;

import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;

public class ObservingFilteredReport extends FilteredReport implements Observer {

    private final PresentationModel model;

    public ObservingFilteredReport(final PresentationModel model) {
        super(model.getData());

        this.filter = model.getFilter();
        this.model = model;
        this.model.addObserver(this);

        this.accumulate();
    }

    public void update(Observable source, Object eventObject) {
        BaralgaEvent event = (BaralgaEvent) eventObject;
        switch (event.getType()) {

            case BaralgaEvent.PROJECT_ACTIVITY_ADDED:
                ProjectActivity activity = (ProjectActivity) event.getData();
                this.acummulateActivity(activity);
                break;

            case BaralgaEvent.PROJECT_ACTIVITY_REMOVED:
                this.accumulate();
                break;

            case BaralgaEvent.PROJECT_ACTIVITY_CHANGED:
                this.accumulate();
                break;
                
            case BaralgaEvent.FILTER_CHANGED:
                final Filter newFilter = (Filter) event.getData();
                setFilter(newFilter);
                break;
        }
        setChanged();
        notifyObservers();
    }

}
