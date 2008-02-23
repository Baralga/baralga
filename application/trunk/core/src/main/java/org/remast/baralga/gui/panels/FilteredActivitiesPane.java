package org.remast.baralga.gui.panels;

import javax.swing.JTabbedPane;

import org.remast.baralga.gui.Messages;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.filter.Filter;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class FilteredActivitiesPane extends JTabbedPane {
    
    private PresentationModel model;
    
    private Filter filter;

    private AccummulatedActitvitiesPanel accummulatedActitvitiesPanel;

    private AllActitvitiesPanel filteredActitvitiesPanel;

    private HoursByWeekPanel hoursByWeekPanel;

    private HoursByDayPanel hoursByDayPanel;

    private DescriptionPanel descriptionPanel;

    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @param filter
     *            the filter to set
     */
    public void setFilter(Filter filter) {
        this.filter = filter;

        // Propagate filter to children
        if (filteredActitvitiesPanel != null) {
            filteredActitvitiesPanel.setFilter(filter);
        }

        if (accummulatedActitvitiesPanel != null) {
            accummulatedActitvitiesPanel.setFilter(filter);
        }

        if (hoursByWeekPanel != null) {
            hoursByWeekPanel.setFilter(filter);
        }

        if (hoursByDayPanel != null) {
            hoursByDayPanel.setFilter(filter);
        }

        if (descriptionPanel != null) {
            descriptionPanel.setFilter(filter);
        }
    }

    public FilteredActivitiesPane(PresentationModel model) {
        super();
        this.model = model;

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        accummulatedActitvitiesPanel = new AccummulatedActitvitiesPanel(model.getFilteredReport());
        this.addTab(
                Messages.getString("FilteredActivitiesPane.Tab.AccumulatedActivities"), accummulatedActitvitiesPanel); //$NON-NLS-1$

        hoursByWeekPanel = new HoursByWeekPanel(model.getHoursByWeekReport());
        this.addTab(Messages.getString("FilteredActivitiesPane.Tab.HoursByWeek"), hoursByWeekPanel); //$NON-NLS-1$

        hoursByDayPanel = new HoursByDayPanel(model.getHoursByDayReport());
        this.addTab(Messages.getString("FilteredActivitiesPane.Tab.HoursByDay"), hoursByDayPanel); //$NON-NLS-1$
        
        filteredActitvitiesPanel = new AllActitvitiesPanel(model);
        this.addTab(Messages.getString("FilteredActivitiesPane.Tab.AllActivities"), filteredActitvitiesPanel); //$NON-NLS-1$

        descriptionPanel = new DescriptionPanel(model);
        this.addTab(Messages.getString("FilteredActivitiesPane.Tab.Descriptions"), descriptionPanel); //$NON-NLS-1$
    }
}
