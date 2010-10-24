package org.remast.baralga.gui.panels;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.remast.baralga.FormatUtils;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.lists.ProjectFilterList;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.filter.Filter;
import org.remast.swing.util.LabeledItem;
import org.remast.util.DateUtils;
import org.remast.util.TextResourceBundle;

import ca.odell.glazedlists.swing.EventComboBoxModel;

/**
 * Displays the reports generated from the project activities.
 * @author remast
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ReportPanel extends JXPanel implements ActionListener {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

    /** The logger. */
    @SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ReportPanel.class);

    /** The model. */
    private PresentationModel model;
    
    private Interval timeInterval;
    
    private SpanType spanType = SpanType.Day;

    /** Filter by selected project. */
    private JComboBox projectFilterSelector;

    /** List of projects by which can be filtered. */
    private ProjectFilterList projectFilterList;

    /** The panel that actually displays the filtered activities. */
    private FilteredActivitiesPane filteredActivitiesPane;

    public ReportPanel(final PresentationModel model) {
        this.model = model;

        initialize();
        
        setTimeInterval(new Interval(org.remast.util.DateUtils.getNowAsDateTime(), org.remast.util.DateUtils.getNowAsDateTime().plusDays(1)));
    }
    
    private static final Vector<LabeledItem<SpanType>> spanSelectorItems = new Vector<LabeledItem<SpanType>>();

	private JComboBox spanSelector;

	private JTextField dateField;

	private JButton nextButton;

	private JButton previousButton;
    static {
    	spanSelectorItems.add(new LabeledItem<SpanType>(SpanType.Day, textBundle.textFor("ReportPanel.DayLabel")));
    	spanSelectorItems.add(new LabeledItem<SpanType>(SpanType.Week, textBundle.textFor("ReportPanel.WeekLabel")));
    	spanSelectorItems.add(new LabeledItem<SpanType>(SpanType.Month, textBundle.textFor("ReportPanel.MonthLabel")));
    	spanSelectorItems.add(new LabeledItem<SpanType>(SpanType.Year, textBundle.textFor("ReportPanel.YearLabel")));
    }
    
    private static enum SpanType {
    	Day, Week, Month, Year
    }

    /**
     * Set up GUI components.
     */
    private void initialize() {
        // Obtain a reusable constraints object to place components in the grid.
        filteredActivitiesPane = new FilteredActivitiesPane(model);

        final double borderBig = 10;
        final double border = 5;
        final double[][] size = {
                { border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, 10, TableLayout.PREFERRED, border, TableLayout.FILL, border}, // Columns
                { border, TableLayout.PREFERRED, borderBig, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.FILL, border }  // Rows
        };
        this.setLayout(new TableLayout(size));
        
        spanSelector = new JComboBox(spanSelectorItems);
        spanSelector.addActionListener(new ActionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				final LabeledItem<SpanType> seletedItem = (LabeledItem<SpanType>) spanSelector.getSelectedItem();
				setSpanType(seletedItem.getItem());
			}

		});
        
        nextButton = new JButton(">");
        nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Interval newTimeInterval = null;
				switch (ReportPanel.this.spanType) {
				case Day:
					newTimeInterval = new Interval(timeInterval.getStart().plusDays(1), timeInterval.getEnd().plusDays(1));
					break;
				case Week:
					newTimeInterval = new Interval(timeInterval.getStart().plusWeeks(1), timeInterval.getEnd().plusWeeks(1));
					break;
				case Month:
					newTimeInterval = new Interval(timeInterval.getStart().plusMonths(1), timeInterval.getEnd().plusMonths(1));
					break;
				case Year:
					newTimeInterval = new Interval(timeInterval.getStart().plusYears(1), timeInterval.getEnd().plusYears(1));
					break;
				}
				
				if (newTimeInterval != null) {
					setTimeInterval(newTimeInterval);
				}
			}
		});
        
        previousButton = new JButton("<");
        previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Interval newTimeInterval = null;
				switch (ReportPanel.this.spanType) {
				case Day:
					newTimeInterval = new Interval(timeInterval.getStart().minusDays(1), timeInterval.getEnd().minusDays(1));
					break;
				case Week:
					newTimeInterval = new Interval(timeInterval.getStart().minusWeeks(1), timeInterval.getEnd().minusWeeks(1));
					break;
				case Month:
					newTimeInterval = new Interval(timeInterval.getStart().minusMonths(1), timeInterval.getEnd().minusMonths(1));
					break;
				case Year:
					newTimeInterval = new Interval(timeInterval.getStart().minusYears(1), timeInterval.getEnd().minusYears(1));
					break;
				}
				
				if (newTimeInterval != null) {
					setTimeInterval(newTimeInterval);
				}
			}
		});
        JButton homeButton = new JButton(textBundle.textFor("ReportPanel.TodayLabel"));
        homeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setSpanType(SpanType.Day);
				spanSelector.setSelectedIndex(0);
			}
		});
        
        dateField = new JTextField();
        dateField.setEditable(false);
        
        final JXTitledSeparator dataSeparator = new JXTitledSeparator(textBundle.textFor("ReportPanel.DataLabel")); //$NON-NLS-1$
        this.add(dataSeparator, "1, 3, 11, 0"); //$NON-NLS-1$
        
        this.add(spanSelector, "1, 5"); //$NON-NLS-1$
        
        this.add(previousButton, "3, 5"); //$NON-NLS-1$

        this.add(homeButton, "5, 5"); //$NON-NLS-1$
        
        this.add(nextButton, "7, 5"); //$NON-NLS-1$

        this.add(new JLabel(textBundle.textFor("ReportPanel.DateLabel")), "9, 5"); //$NON-NLS-1$

        this.add(dateField, "11, 5"); //$NON-NLS-1$
        
        this.add(filteredActivitiesPane, "1, 7, 11, 7"); //$NON-NLS-1$
    }

    /**
     * @return the projectFilterSelector
     */
    private JComboBox getProjectFilterSelector() {
        if (projectFilterSelector == null) {
            projectFilterList = model.getProjectFilterList();
            projectFilterSelector = new JComboBox(
                    new EventComboBoxModel<LabeledItem<Project>>(projectFilterList.getProjectList())
            );
            projectFilterSelector.setToolTipText(textBundle.textFor("ProjectFilterSelector.ToolTipText")); //$NON-NLS-1$

            // Select first entry
            if (!projectFilterList.getProjectList().isEmpty()) {
                projectFilterSelector.setSelectedIndex(0);
            }

            // Read from Settings.
            final Long selectedProjectId = UserSettings.instance().getFilterSelectedProjectId();
            if (selectedProjectId != null) {
                for (LabeledItem<Project> item : projectFilterList.getProjectList()) {
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
     * Create filter from selection in this panel.
     * @return the filter for the selection
     */
    @SuppressWarnings("unchecked")
    public Filter createFilter() {
        final Filter filter = new Filter();
        filter.setTimeInterval(timeInterval);

        // Filter for project
        final LabeledItem<Project> projectFilterItem = (LabeledItem<Project>) getProjectFilterSelector().getSelectedItem();
        final Project project = projectFilterItem.getItem();
        if (!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            filter.setProject(project);
        }
        return filter;
    }

    /**
     * Stores the filter in the user settings.
     */
    @SuppressWarnings("unchecked")
    private void storeFilterInSettings() {
    	// TODO:

        // Store filter by project
        final LabeledItem<Project> projectFilterItem = (LabeledItem<Project>) getProjectFilterSelector().getSelectedItem();
        final Project project = projectFilterItem.getItem();
        if (!ProjectFilterList.ALL_PROJECTS_DUMMY.equals(project)) {
            long projectId = project.getId();
            UserSettings.instance().setFilterSelectedProjectId(projectId);
        } else {
            UserSettings.instance().setFilterSelectedProjectId(
                    ProjectFilterList.ALL_PROJECTS_DUMMY_VALUE
            );
        }
    }

    /**
     * One of the filter criteria changed. So we create and apply the filter.
     */
    public final void actionPerformed(final ActionEvent event) {
        // 1. Create filter from selection.
        final Filter filter = this.createFilter();

        // 2. Save selection to settings.
        storeFilterInSettings();
        
        // 3. Save to model
        model.setFilter(filter, this);
    }
    
	private void setSpanType(final SpanType spanType) {
		this.spanType = spanType;

		DateTime now = DateUtils.getNowAsDateTime();

		switch (spanType) {
		case Day:
			setTimeInterval(new Interval(now, now.plusDays(1)));
			break;
		case Week:
			now = now.withDayOfWeek(1);
			setTimeInterval(new Interval(now, now.plusWeeks(1)));
			break;
		case Month:
			now = now.withDayOfMonth(1);
			setTimeInterval(new Interval(now, now.plusMonths(1)));
			break;
		case Year:
			now = now.withDayOfYear(1);
			setTimeInterval(new Interval(now, now.plusYears(1)));
			break;
		}
	}
	
	private static final DateTimeFormatter weekOfYearFormatter = DateTimeFormat.forPattern("ww");
    private static final DateTimeFormatter monthFormatter = DateTimeFormat.forPattern("MM/yyyy");
    private static final DateTimeFormatter yearFormatter = DateTimeFormat.forPattern("yyyy");


	private void setTimeInterval(Interval interval) {
		this.timeInterval = interval;
		
		String intervalString = this.timeInterval.toString();
		switch (spanType) {
		case Day:
			intervalString = FormatUtils.formatDate(timeInterval.getStart());
			break;
		case Week:
			intervalString = "(" + textBundle.textFor("ReportPanel.CWLabel") + " " + weekOfYearFormatter.print(timeInterval.getStart()) + ") " + FormatUtils.formatDate(timeInterval.getStart()) + " - " + FormatUtils.formatDate(timeInterval.getEnd().minusDays(1));
			break;
		case Month:
			intervalString = monthFormatter.print(timeInterval.getStart());
			break;
		case Year:
			intervalString = yearFormatter.print(timeInterval.getStart());
			break;
		}
		this.dateField.setText(intervalString);

		
		this.actionPerformed(null);
	}

}
