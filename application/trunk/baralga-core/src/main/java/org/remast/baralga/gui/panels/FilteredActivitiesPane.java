package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXPanel;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.report.AccummulatedActitvitiesPanel;
import org.remast.baralga.gui.panels.report.AllActitvitiesPanel;
import org.remast.baralga.gui.panels.report.DescriptionPanel;
import org.remast.baralga.gui.panels.report.HoursByDayPanel;
import org.remast.baralga.gui.panels.report.HoursByProjectChartPanel;
import org.remast.baralga.gui.panels.report.HoursByProjectPanel;
import org.remast.baralga.gui.panels.report.HoursByWeekPanel;
import org.remast.baralga.model.filter.Filter;

import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.swing.JideToggleButton;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class FilteredActivitiesPane extends JXPanel {

    private PresentationModel model;

    private Filter filter;

    private AccummulatedActitvitiesPanel accummulatedActitvitiesPanel;

    CategorizedTab accummulatedActitvitiesTab;
    
    private List<CategorizedTab> categorizedTabs = new ArrayList<CategorizedTab>();
    
    private String shownCategory;

    private JideTabbedPane tabs = new JideTabbedPane();

    
    //------------------------------------------------
    // Tabs with their panels
    //------------------------------------------------

    private HoursByWeekPanel hoursByWeekPanel;
    private CategorizedTab hoursByWeekTab;

    private HoursByDayPanel hoursByDayPanel;
    private CategorizedTab hoursByDayTab;

    private HoursByProjectPanel hoursByProjectPanel;
    private CategorizedTab hoursByProjectTab;
    
    private HoursByProjectChartPanel hoursByProjectChartPanel;
    private CategorizedTab hoursByProjectChartTab;
    
    private AllActitvitiesPanel filteredActitvitiesPanel;
    private CategorizedTab filteredActitvitiesTab;

    private DescriptionPanel descriptionPanel;
    private CategorizedTab descriptionTab;
    
    //------------------------------------------------
    // Toggle buttons for tab categorys
    //------------------------------------------------

    private JXPanel categoryButtonPanel = new JXPanel();
    
    private JideToggleButton generalButton = new JideToggleButton(new AbstractAction(Messages.getString("Category.General"), new ImageIcon(getClass().getResource("/icons/gtk-dnd-multiple.png"))) {

        @Override
        public void actionPerformed(ActionEvent e) {
            FilteredActivitiesPane.this.toggleCategory("General", generalButton);
        }
        
    });
    
    private JideToggleButton timeButton = new JideToggleButton(new AbstractAction(Messages.getString("Category.Time"), new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png"))) {

        @Override
        public void actionPerformed(ActionEvent e) {
            FilteredActivitiesPane.this.toggleCategory("Time", timeButton);
        }
        
    });
    private JideToggleButton projectButton = new JideToggleButton(new AbstractAction(Messages.getString("Category.Project"), new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png"))) {

        @Override
        public void actionPerformed(ActionEvent e) {
            FilteredActivitiesPane.this.toggleCategory("Project", projectButton);
        }
        
    });
    
    private JideToggleButton [] categoryToggleButtons = new JideToggleButton [] {
            generalButton, projectButton, timeButton
    };

    public FilteredActivitiesPane(PresentationModel model) {
        super();
        this.model = model;
        
        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        double size[][] = {
                { TableLayout.FILL}, // Columns
                { TableLayout.PREFERRED, TableLayout.FILL } }; // Rows
        this.setLayout(new TableLayout(size));
        
        int border = 5;
        categoryButtonPanel.setLayout(new TableLayout(new double [][] {{0, TableLayout.FILL, border, TableLayout.FILL, border, TableLayout.FILL},{border, TableLayout.PREFERRED, border-3}}));
        categoryButtonPanel.add(generalButton, "1, 1");
        categoryButtonPanel.add(timeButton, "3, 1");
        categoryButtonPanel.add(projectButton, "5, 1");
        this.add(categoryButtonPanel, "0, 0");

        tabs.setTabShape(JideTabbedPane.SHAPE_WINDOWS);
        tabs.setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER);
        
        shownCategory = Settings.instance().getShownCategory();

        accummulatedActitvitiesTab = new CategorizedTab("General");
        accummulatedActitvitiesPanel = new AccummulatedActitvitiesPanel(model.getFilteredReport());
        accummulatedActitvitiesTab.setComponent(
                Messages.getString("FilteredActivitiesPane.Tab.AccumulatedActivities"),  //$NON-NLS-1$
                null,
//              new ImageIcon(getClass().getResource("/icons/gnome-calculator.png")),  //$NON-NLS-1$
                accummulatedActitvitiesPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.AccumulatedActivities.Tooltip") //$NON-NLS-1$
        );
        categorizedTabs.add(accummulatedActitvitiesTab);

        filteredActitvitiesTab = new CategorizedTab("General");
        filteredActitvitiesPanel = new AllActitvitiesPanel(model);
        filteredActitvitiesTab.setComponent(
                Messages.getString("FilteredActivitiesPane.Tab.AllActivities"),  //$NON-NLS-1$
                null,
//              new ImageIcon(getClass().getResource("/icons/gtk-dnd-multiple.png")),  //$NON-NLS-1$
                filteredActitvitiesPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.AllActivities.Tooltip") //$NON-NLS-1$
        );
        categorizedTabs.add(filteredActitvitiesTab);
        
        descriptionTab = new CategorizedTab("General");
        descriptionPanel = new DescriptionPanel(model);
        descriptionTab.setComponent(
                Messages.getString("FilteredActivitiesPane.Tab.Descriptions"),  //$NON-NLS-1$
                null,
//              new ImageIcon(getClass().getResource("/icons/gnome-mime-text-x-readme.png")), 
                descriptionPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.Descriptions.Tooltip") //$NON-NLS-1$
        );
        categorizedTabs.add(descriptionTab);

        hoursByWeekTab = new CategorizedTab("Time");
        hoursByWeekPanel = new HoursByWeekPanel(model.getHoursByWeekReport());
        hoursByWeekTab.setComponent(
                Messages.getString("FilteredActivitiesPane.Tab.HoursByWeek"),  //$NON-NLS-1$
                null,
//              new ImageIcon(getClass().getResource("/icons/stock_calendar-view-work-week.png")),  //$NON-NLS-1$
                hoursByWeekPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.HoursByWeek.Tooltip") //$NON-NLS-1$
        );
        categorizedTabs.add(hoursByWeekTab);

        hoursByDayTab = new CategorizedTab("Time");
        hoursByDayPanel = new HoursByDayPanel(model.getHoursByDayReport());
        hoursByDayTab.setComponent(
                Messages.getString("FilteredActivitiesPane.Tab.HoursByDay"),  //$NON-NLS-1$
                null,
//              new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png")),  //$NON-NLS-1$
                hoursByDayPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.HoursByDay.Tooltip") //$NON-NLS-1$
        );
        categorizedTabs.add(hoursByDayTab);

        hoursByProjectTab = new CategorizedTab("Project");
        hoursByProjectPanel = new HoursByProjectPanel(model.getHoursByProjectReport());
        hoursByProjectTab.setComponent(
                Messages.getString("FilteredActivitiesPane.Tab.HoursByProject"),  //$NON-NLS-1$
                null,
//              new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png")),  //$NON-NLS-1$
                hoursByProjectPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.HoursByProject.Tooltip") //$NON-NLS-1$
        );
        categorizedTabs.add(hoursByProjectTab);
        
        hoursByProjectChartTab = new CategorizedTab("Project");
        hoursByProjectChartPanel = new HoursByProjectChartPanel(model.getHoursByProjectReport());
        hoursByProjectChartTab.setComponent(
                Messages.getString("FilteredActivitiesPane.Tab.HoursByProjectChart"),  //$NON-NLS-1$
                null,
//              new ImageIcon(getClass().getResource("/icons/stock_calendar-view-day.png")),  //$NON-NLS-1$
                hoursByProjectChartPanel, 
                Messages.getString("FilteredActivitiesPane.Tab.HoursByProjectChart.Tooltip") //$NON-NLS-1$
        );
        categorizedTabs.add(hoursByProjectChartTab);
        

        
        this.initCategorizedTabs();
        this.add(tabs, "0, 1");
    }

    private void initCategorizedTabs() {
        tabs.removeAll();
        
        for (CategorizedTab tab : categorizedTabs) {
            if (StringUtils.equals(shownCategory, tab.getCategory())) {
                this.addCategorizedTab(tab);
            }
        }
        
    }

    public void toggleCategory(String cat, JideToggleButton toggledCategoryButton) {
        shownCategory = cat;
        
        Settings.instance().setShownCategory(cat);
        
        initCategorizedTabs();
        
        for (JideToggleButton categoryButton : categoryToggleButtons) {
            if (categoryButton != toggledCategoryButton) {
                categoryButton.setSelected(false);
            }
        }
        
    }

    private void addCategorizedTab(CategorizedTab tab) {
        tabs.addTab(tab.getTitle(), tab.getIcon(), tab.getComponent(), tab.getTip());   
    }


    /**
     * @param filter
     *            the filter to set
     * @deprecated replace by oberserver
     */
    public void setFilter(final Filter filter) {
        this.filter = filter;
    }
}
