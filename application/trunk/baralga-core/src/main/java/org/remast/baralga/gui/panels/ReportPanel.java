package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;

import javax.swing.JComboBox;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.remast.baralga.Messages;
import org.remast.baralga.gui.Settings;
import org.remast.baralga.gui.lists.FilterItem;
import org.remast.baralga.gui.lists.MonthFilterList;
import org.remast.baralga.gui.lists.ProjectFilterList;
import org.remast.baralga.gui.lists.YearFilterList;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.panels.report.FilteredActivitiesPane;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.filter.Filter;

import ca.odell.glazedlists.swing.EventComboBoxModel;

/**
 * @author remast
 */
@SuppressWarnings("serial")//$NON-NLS-1$
public class ReportPanel extends JXPanel implements ActionListener {

    /** The logger. */
    private static final Log log = LogFactory.getLog(ReportPanel.class);

    /** The model. */
    private PresentationModel model;

    /** Filter by selected project. */
    private JComboBox projectFilterSelector;

    /** Filter by selected year. */
    private JComboBox yearFilterSelector;

    /** Filter by selected month. */
    private JComboBox monthFilterSelector;

    private FilteredActivitiesPane filteredActivitiesPane;

    /** List of months by which can be filtered. */
    private MonthFilterList monthFilterList;

    /** List of years by which can be filtered. */
    private YearFilterList yearFilterList;

    /** List of projects by which can be filtered. */
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
        filteredActivitiesPane = new FilteredActivitiesPane(model);

        double border = 5;
        double size[][] = {
                { border, TableLayout.PREFERRED, border, TableLayout.FILL, border, TableLayout.PREFERRED, border,
                        TableLayout.FILL, border, TableLayout.PREFERRED, border, TableLayout.FILL, border }, // Columns
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border,
                        TableLayout.FILL, border } }; // Rows
        this.setLayout(new TableLayout(size));

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
     * @return the monthFilterSelector
     */
    public JComboBox getMonthFilterSelector() {
        if (monthFilterSelector == null) {
            monthFilterList = model.getMonthFilterList();
            monthFilterSelector = new JComboBox(new EventComboBoxModel<FilterItem<String>>(monthFilterList
                    .getMonthList()));
            monthFilterSelector.setToolTipText(Messages.getString("MonthFilterSelector.ToolTipText"));
            
            // Select first entry
            if (!monthFilterList.getMonthList().isEmpty()) {
                monthFilterSelector.setSelectedIndex(0);
            }

            // Read from Settings.
            String selectedMonth = Settings.instance().getFilterSelectedMonth();
            if (selectedMonth != null) {
                if (MonthFilterList.ALL_MONTHS_DUMMY.equals(selectedMonth)) {
                    monthFilterSelector.setSelectedItem(MonthFilterList.ALL_MONTHS_FILTER_ITEM);
                } else {
                    for (FilterItem<String> item : monthFilterList.getMonthList()) {
                        if (StringUtils.equals(selectedMonth, item.getItem())) {
                            monthFilterSelector.setSelectedItem(item);
                            break;
                        }
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
        if (projectFilterSelector == null) {
            projectFilterList = model.getProjectFilterList();
            projectFilterSelector = new JComboBox(new EventComboBoxModel<FilterItem<Project>>(projectFilterList
                    .getProjectList()));
            projectFilterSelector.setToolTipText(Messages.getString("ProjectFilterSelector.ToolTipText"));
            
            // Select first entry
            if (!projectFilterList.getProjectList().isEmpty()) {
                projectFilterSelector.setSelectedIndex(0);
            }

            // Read from Settings.
            Long selectedProjectId = Settings.instance().getFilterSelectedProjectId();
            if (selectedProjectId != null) {
                if (selectedProjectId.longValue() == -1) {
                    projectFilterSelector.setSelectedItem(ProjectFilterList.ALL_PROJECTS_FILTER_ITEM);
                } else {
                    for (FilterItem<Project> item : projectFilterList.getProjectList()) {
                        if (ObjectUtils.equals(item.getItem().getId(), selectedProjectId.longValue())) {
                            projectFilterSelector.setSelectedItem(item);
                            break;
                        }
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
        if (yearFilterSelector == null) {
            yearFilterList = model.getYearFilterList();
            yearFilterSelector = new JComboBox(new EventComboBoxModel<FilterItem<String>>(yearFilterList.getYearList()));
            yearFilterSelector.setToolTipText(Messages.getString("YearFilterSelector.ToolTipText"));

            // Select first entry
            if (!CollectionUtils.isEmpty(yearFilterList.getYearList())) {
                yearFilterSelector.setSelectedIndex(0);
            }

            // Read from Settings.
            String selectedYear = Settings.instance().getFilterSelectedYear();
            if (selectedYear != null) {
                if (YearFilterList.ALL_YEARS_DUMMY.equals(selectedYear)) {
                    yearFilterSelector.setSelectedItem(YearFilterList.ALL_YEARS_FILTER_ITEM);
                } else {
                    for (FilterItem<String> item : yearFilterList.getYearList()) {
                        if (StringUtils.equals(selectedYear, item.getItem())) {
                            yearFilterSelector.setSelectedItem(item);
                            break;
                        }
                    }
                }
            }

            yearFilterSelector.addActionListener(this);
        }
        return yearFilterSelector;
    }

    /**
     * Create filter from selection in this panel.
     * @return the filter for the selection
     */
    public Filter createFilter() {
        final Filter filter = new Filter();

        FilterItem<String> filterItem = (FilterItem<String>) getMonthFilterSelector().getSelectedItem();
        final String selectedMonth = filterItem.getItem();
        if (!MonthFilterList.ALL_MONTHS_DUMMY.equals(selectedMonth)) {
            try {
                Date month = MonthFilterList.MONTH_FORMAT.parse(selectedMonth);
                filter.setMonth(month);
            } catch (ParseException e) {
                log.error(e, e);
            }
        }

        filterItem = (FilterItem<String>) getYearFilterSelector().getSelectedItem();
        final String selectedYear = filterItem.getItem();
        if (!YearFilterList.ALL_YEARS_DUMMY.equals(selectedYear)) {
            try {
                Date year = YearFilterList.YEAR_FORMAT.parse(selectedYear);
                filter.setYear(year);
            } catch (ParseException e) {
                log.error(e, e);
            }
        }

        final FilterItem<Project> projectFilterItem = (FilterItem<Project>) getProjectFilterSelector().getSelectedItem();
        final Project project = projectFilterItem.getItem();
        if (!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            filter.setProject(project);
        }
        return filter;
    }

    /**
     * Stores the filter in the user settings.
     */
    private void saveToPreferences() {
        // Store filter by month
        FilterItem<String> filterItem = (FilterItem<String>) getMonthFilterSelector().getSelectedItem();
        final String selectedMonth = filterItem.getItem();
        if (!MonthFilterList.ALL_MONTHS_DUMMY.equals(selectedMonth)) {
            Settings.instance().setFilterSelectedMonth(selectedMonth);
        } else {
            Settings.instance().setFilterSelectedMonth(MonthFilterList.ALL_MONTHS_DUMMY);
        }

        // Store filter by year
        filterItem = (FilterItem<String>) getYearFilterSelector().getSelectedItem();
        final String selectedYear = filterItem.getItem();
        if (!YearFilterList.ALL_YEARS_DUMMY.equals(selectedYear)) {
            Settings.instance().setFilterSelectedYear(selectedYear);
        } else {
            Settings.instance().setFilterSelectedYear(YearFilterList.ALL_YEARS_DUMMY);
        }

        // Store filter by project
        final FilterItem<Project> projectFilterItem = (FilterItem<Project>) getProjectFilterSelector()
                .getSelectedItem();
        final Project project = projectFilterItem.getItem();
        if (!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            long projectId = project.getId();
            Settings.instance().setFilterSelectedProjectId(projectId);
        } else {
            Settings.instance().setFilterSelectedProjectId(-1);
        }
    }

    /**
     * One of the filter criteria changed. So we create and apply the filter.
     */
    public void actionPerformed(ActionEvent e) {
        // 1. Create filter from selection.
        Filter filter = this.createFilter();

        // 2. Save selection to preferences.
        saveToPreferences();

        // 3. Save to model
        model.setFilter(filter, this);

        // 4. Propagate to children
        if (filteredActivitiesPane != null) {
            filteredActivitiesPane.setFilter(filter);
        }
    }
}
