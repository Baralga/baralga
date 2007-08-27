package org.remast.baralga.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXPanel;
import org.remast.baralga.Messages;
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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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
     * This method initializes this
     */
    private void initialize() {
        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, right:pref, 3dlu, right:pref, 3dlu, right:pref, 3dlu, right:pref, 3dlu, pref:grow", // columns //$NON-NLS-1$
                "pref, 3dlu, pref, 6dlu, pref, 3dlu, pref:grow"); // rows //$NON-NLS-1$
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        // Obtain a reusable constraints object to place components in the grid.
        CellConstraints cc = new CellConstraints();
        builder.addSeparator(Messages.getString("ReportPanel.FiltersLabel"), cc.xyw(1, 1, 11)); //$NON-NLS-1$

        builder.addLabel(Messages.getString("ReportPanel.ProjectLabel"), cc.xyw(1, 3, 1)); //$NON-NLS-1$
        builder.add(getProjectFilterSelector(), cc.xyw(3, 3, 1));

        builder.addLabel(Messages.getString("ReportPanel.YearLabel"), cc.xyw(5, 3, 1)); //$NON-NLS-1$
        builder.add(getYearFilterSelector(), cc.xyw(7, 3, 1));

        builder.addLabel(Messages.getString("ReportPanel.MonthLabel"), cc.xyw(9, 3, 1)); //$NON-NLS-1$
        builder.add(getMonthFilterSelector(), cc.xyw(11, 3, 1));

        builder.addSeparator(Messages.getString("ReportPanel.DataLabel"), cc.xyw(1, 5, 11)); //$NON-NLS-1$
        filteredActivitiesPane = new FilteredActivitiesPane(getModel());
        builder.add(filteredActivitiesPane, cc.xyw(1, 7, 11, "fill, fill"));

        JPanel p = builder.getPanel();
        this.setLayout(new BorderLayout());
        this.add(p, BorderLayout.CENTER);

        /*
        JXCollapsiblePane cp = new JXCollapsiblePane();
        cp.setLayout(new BorderLayout());
        cp.add("Center", p);

        this.add("Center", cp);

        // get the built-in toggle action
        Action toggleAction = cp.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION);

        // use the collapse/expand icons from the JTree UI
        toggleAction.putValue(JXCollapsiblePane.COLLAPSE_ICON, UIManager.getIcon("Tree.expandedIcon"));
        toggleAction.putValue(JXCollapsiblePane.EXPAND_ICON, UIManager.getIcon("Tree.collapsedIcon"));
        toggleAction.putValue(Action.NAME, "Show / hide Report");
        JToggleButton toggle = new JToggleButton(toggleAction);
        this.add("North", toggle);
        */
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
            
            yearFilterSelector.addActionListener(this);
        }
        return yearFilterSelector;
    }

    /**
     * Create filter from selection.
     * @return
     */
    public Filter<ProjectActivity>  createFilter() {
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
        if(!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project))
            filter.addPredicate(new ProjectPredicate(project));
        
        return filter;
    }

    public void actionPerformed(ActionEvent e) {
        Filter<ProjectActivity> filter = this.createFilter();
        
        if(filteredActivitiesPane != null) {
            filteredActivitiesPane.setFilter(filter);
        }
        
        getModel().setFilter(filter);
    }
}
