package org.remast.baralga.gui.panels;

import javax.swing.ImageIcon;

import org.remast.baralga.Messages;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.model.filter.Filter;

import com.jidesoft.swing.JideTabbedPane;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class FilteredActivitiesPane extends JideTabbedPane {

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
        this.setTabShape(JideTabbedPane.SHAPE_WINDOWS);
      setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER);

        accummulatedActitvitiesPanel = new AccummulatedActitvitiesPanel(model.getFilteredReport());
        this.addTab(
                Messages.getString("FilteredActivitiesPane.Tab.AccumulatedActivities"),  //$NON-NLS-1$
                null,
//                new ImageIcon(getClass().getResource("/icons/gnome-calculator.png")),  //$NON-NLS-1$
                accummulatedActitvitiesPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.AccumulatedActivities.Tooltip") //$NON-NLS-1$
        );

        hoursByWeekPanel = new HoursByWeekPanel(model.getHoursByWeekReport());
        this.addTab(
                Messages.getString("FilteredActivitiesPane.Tab.HoursByWeek"),  //$NON-NLS-1$
                null,
//                new ImageIcon(getClass().getResource("/icons/stock_calendar-view-work-week.png")),  //$NON-NLS-1$
                hoursByWeekPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.HoursByWeek.Tooltip") //$NON-NLS-1$
        );

        hoursByDayPanel = new HoursByDayPanel(model.getHoursByDayReport());
        this.addTab(
                Messages.getString("FilteredActivitiesPane.Tab.HoursByDay"),  //$NON-NLS-1$
                null,
//                new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png")),  //$NON-NLS-1$
                hoursByDayPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.HoursByDay.Tooltip") //$NON-NLS-1$
        );

        filteredActitvitiesPanel = new AllActitvitiesPanel(model);
        this.addTab(
                Messages.getString("FilteredActivitiesPane.Tab.AllActivities"),  //$NON-NLS-1$
                null,
//                new ImageIcon(getClass().getResource("/icons/gtk-dnd-multiple.png")),  //$NON-NLS-1$
                filteredActitvitiesPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.AllActivities.Tooltip") //$NON-NLS-1$
        );

        descriptionPanel = new DescriptionPanel(model);
        this.addTab(
                Messages.getString("FilteredActivitiesPane.Tab.Descriptions"),  //$NON-NLS-1$
                null,
//                new ImageIcon(getClass().getResource("/icons/gnome-mime-text-x-readme.png")), 
                descriptionPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.Descriptions.Tooltip") //$NON-NLS-1$
        );
    }
}
