package org.remast.baralga.gui.panels;

import javax.swing.JTabbedPane;

import org.remast.baralga.Messages;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.filter.Filter;

/**
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class FilteredActivitiesPane extends JTabbedPane {

    private PresentationModel pModel;
    
    private Filter filter;

    private AccummulatedActitvitiesPanel accummulatedActitvitiesPanel;

    private AllActitvitiesPanel filteredActitvitiesPanel;
    
    private HoursByWeekPanel hoursByWeekPanel;

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(Filter filter) {
        this.filter = filter;

        // Propagate filter to children
        if(filteredActitvitiesPanel != null)
            filteredActitvitiesPanel.setFilter(filter);
        
        if(accummulatedActitvitiesPanel != null)
            accummulatedActitvitiesPanel.setFilter(filter);
        
        if(hoursByWeekPanel != null)
            hoursByWeekPanel.setFilter(filter);

    }

    public FilteredActivitiesPane(PresentationModel model) {
        super();
        this.pModel = model;
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        accummulatedActitvitiesPanel = new AccummulatedActitvitiesPanel(pModel.getFilteredReport());
        this.addTab(Messages.getString("FilteredActivitiesPane.Tab.AccumulatedActivities"), accummulatedActitvitiesPanel); //$NON-NLS-1$
        
        hoursByWeekPanel = new HoursByWeekPanel(pModel.getHoursByWeekReport());
        this.addTab(Messages.getString("FilteredActivitiesPane.Tab.HoursByWeek"), hoursByWeekPanel); //$NON-NLS-1$
        
        filteredActitvitiesPanel = new AllActitvitiesPanel(pModel);
        this.addTab(Messages.getString("FilteredActivitiesPane.Tab.AllActivities"), filteredActitvitiesPanel); //$NON-NLS-1$
    }
}
