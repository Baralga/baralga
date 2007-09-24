package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;

import javax.swing.JComboBox;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.model.PresentationModel;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.filter.MonthPredicate;
import org.remast.baralga.model.filter.ProjectPredicate;
import org.remast.baralga.model.filter.YearPredicate;
import org.remast.baralga.model.lists.FilterItem;
import org.remast.baralga.model.lists.MonthFilterList;
import org.remast.baralga.model.lists.ProjectFilterList;
import org.remast.baralga.model.lists.YearFilterList;

import ca.odell.glazedlists.swing.EventComboBoxModel;

/**
 * @author Jan Stamer
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ReportPanel extends JXPanel implements ActionListener {

    /** The model. */
    private PresentationModel model = null;
    
    /** Filter by selected project. */
    private JComboBox projectFilterSelector;
    
    /** Filter by selected year. */
    private JComboBox yearFilterSelector;

    /** Filter by selected month. */
    private JComboBox monthFilterSelector;

    private FilteredActivitiesPane filteredActivitiesPane;

    private MonthFilterList monthFilterList;

    private YearFilterList yearFilterList;

    private ProjectFilterList projectFilterList;
    
    public ReportPanel(final PresentationModel model) {
        this.model = model;

        initialize();
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        // Obtain a reusable constraints object to place components in the grid.
        filteredActivitiesPane = new FilteredActivitiesPane(getModel());

        double border = 5;
        double size[][] =
        {{border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.PREFERRED, border, TableLayout.FILL, border},  // Columns
         {border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.FILL, border}}; // Rows
        this.setLayout (new TableLayout(size));
        
        JXTitledSeparator filterSep = new JXTitledSeparator(Messages.getString("ReportPanel.FiltersLabel"));
        this.add(filterSep, "1, 1, 11, 1"); //$NON-NLS-1$
        
        this.add(new JXLabel(Messages.getString("ReportPanel.ProjectLabel")), "1, 3");
        this.add(getProjectFilterSelector(), "3, 3");
        
        this.add(new JXLabel(Messages.getString("ReportPanel.YearLabel")), "5, 3");
        this.add(getYearFilterSelector(), "7, 3");

        this.add(new JXLabel(Messages.getString("ReportPanel.MonthLabel")), "9, 3");
        this.add(getMonthFilterSelector(), "11, 3");

        JXTitledSeparator sep = new JXTitledSeparator(Messages.getString("ReportPanel.DataLabel"));
        this.add(sep, "1, 5, 11, 1"); //$NON-NLS-1$
        
        this.add(filteredActivitiesPane, "1, 7, 11, 7");
    }

    /**
     * This method initializes model
     * 
     * @return org.remast.baralga.model.PresentationModel
     */
    private PresentationModel getModel() {
        return this.model;
    }

    /**
     * @return the monthFilterSelector
     */
    public JComboBox getMonthFilterSelector() {
        if(monthFilterSelector == null) {
            monthFilterList = getModel().getMonthFilterList();
            monthFilterSelector = new JComboBox(new EventComboBoxModel<FilterItem<String>>(monthFilterList.getMonthList()));

            // Select first entry
            if(!monthFilterList.getMonthList().isEmpty()) {
                monthFilterSelector.setSelectedIndex(0);
            }
            
            // Read from Settings.
            String selectedMonth = Settings.instance().getSelectedMonth();
            if(selectedMonth != null) {
                for(FilterItem<String> item : monthFilterList.getMonthList()) {
                    if(StringUtils.equals(selectedMonth, item.getItem())) {
                        monthFilterSelector.setSelectedItem(item);
                        break;
                    }
                }
            }

            monthFilterSelector.addActionListener(this);
        }
        return monthFilterSelector;
    }

    /**
     * @return the projectFilterSelector
     */
    public JComboBox getProjectFilterSelector() {
        if(projectFilterSelector == null) {
            projectFilterList = getModel().getProjectFilterList();
            projectFilterSelector= new JComboBox(new EventComboBoxModel<FilterItem<Project>>(projectFilterList.getProjectList()));

            // Select first entry
            if(!projectFilterList.getProjectList().isEmpty()) {
                projectFilterSelector.setSelectedIndex(0);
            }
            
            // Read from Settings.
            Long selectedProjectId = Settings.instance().getSelectedProjectId();
            if(selectedProjectId != null)  {
                for(FilterItem<Project> item : projectFilterList.getProjectList()) {
                    if (ObjectUtils.equals(item.getItem().getId(), selectedProjectId.longValue())) {
                        projectFilterSelector.setSelectedItem(item);
                        break;
                    }
                }
            }

            
            projectFilterSelector.addActionListener(this);
        }
        return projectFilterSelector;
    }

    /**
     * @return the yearFilterSelector
     */
    public JComboBox getYearFilterSelector() {
        if(yearFilterSelector == null) {
            yearFilterList = getModel().getYearFilterList();
            yearFilterSelector= new JComboBox(new EventComboBoxModel<FilterItem<String>>(yearFilterList.getYearList()));

            // Select first entry
            if(!yearFilterList.getYearList().isEmpty()) {
                yearFilterSelector.setSelectedIndex(0);
            }
            
            // Read from Settings.
            String selectedYear = Settings.instance().getSelectedYear();
            if(selectedYear != null) {
                for(FilterItem<String> item : yearFilterList.getYearList()) {
                    if(StringUtils.equals(selectedYear, item.getItem())) {
                        yearFilterSelector.setSelectedItem(item);
                        break;
                    }
                }
            }
            
            yearFilterSelector.addActionListener(this);
        }
        return yearFilterSelector;
    }

    /**
     * Create filter from selection.
     * @return
     */
    public Filter<ProjectActivity> createFilter() {
        Filter<ProjectActivity> filter = new Filter<ProjectActivity>();
        
        FilterItem<String> filterItem = (FilterItem<String>) getMonthFilterSelector().getSelectedItem();
        String selectedMonth = filterItem.getItem();
        if(!MonthFilterList.ALL_MONTHS_DUMMY.equals(selectedMonth)) {
            try {
                Date month = MonthFilterList.MONTH_FORMAT.parse(selectedMonth);
                filter.addPredicate(new MonthPredicate(month));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        filterItem = (FilterItem<String>) getYearFilterSelector().getSelectedItem();
        String selectedYear = filterItem.getItem();
        if(!YearFilterList.ALL_YEARS_DUMMY.equals(selectedYear)) {
            try {
                Date year = YearFilterList.YEAR_FORMAT.parse(selectedYear);
                filter.addPredicate(new YearPredicate(year));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        FilterItem<Project> projectFilterItem = (FilterItem<Project>) getProjectFilterSelector().getSelectedItem();
        Project project = projectFilterItem.getItem();
        if(!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            filter.addPredicate(new ProjectPredicate(project));
        }
        return filter;
    }
    
    private void saveToPreferences() {
        FilterItem<String> filterItem = (FilterItem<String>) getMonthFilterSelector().getSelectedItem();
        String selectedMonth = filterItem.getItem();
        if(!MonthFilterList.ALL_MONTHS_DUMMY.equals(selectedMonth)) {
            Settings.instance().setSelectedMonth(selectedMonth);
        }

        filterItem = (FilterItem<String>) getYearFilterSelector().getSelectedItem();
        String selectedYear = filterItem.getItem();
        if(!YearFilterList.ALL_YEARS_DUMMY.equals(selectedYear)) {
            Settings.instance().setSelectedYear(selectedYear);
        }

        FilterItem<Project> projectFilterItem = (FilterItem<Project>) getProjectFilterSelector().getSelectedItem();
        Project project = projectFilterItem.getItem();
        if(!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            long projectId = project.getId();
            Settings.instance().setSelectedProjectId(projectId);
        }
    }

    public void actionPerformed(ActionEvent e) {
        // 1. Create filter from selection.
        Filter<ProjectActivity> filter = this.createFilter();
        
        // 2. Save selection to preferences.
        saveToPreferences();
        
        if(filteredActivitiesPane != null) {
            filteredActivitiesPane.setFilter(filter);
        }
        
        getModel().setFilter(filter);
    }
}
