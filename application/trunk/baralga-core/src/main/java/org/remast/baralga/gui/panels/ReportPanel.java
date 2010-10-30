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
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.lists.ProjectFilterList;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.filter.FilterUtils;
import org.remast.baralga.model.filter.SpanType;
import org.remast.swing.util.LabeledItem;
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

	/** Filter by selected project. */
	private JComboBox projectFilterSelector;

	/** List of projects by which can be filtered. */
	private ProjectFilterList projectFilterList;

	/** The panel that actually displays the filtered activities. */
	private FilteredActivitiesPane filteredActivitiesPane;
	
	/** Filter by selected span type. */
	private JComboBox spanTypeSelector;
	
	/** Displays the interval of the current filter. */
	private JTextField dateField;

	/** Jump to next interval of the filter. */
	private JButton nextIntervalButton;
	
	/** Jump to the previous interval of the filter. */
	private JButton previousIntervalButton;
	
	/** All available options for the time span. */
	private static final Vector<LabeledItem<SpanType>> spanSelectorItems = new Vector<LabeledItem<SpanType>>();
	static {
		spanSelectorItems.add(new LabeledItem<SpanType>(SpanType.Day, textBundle.textFor("ReportPanel.DayLabel")));
		spanSelectorItems.add(new LabeledItem<SpanType>(SpanType.Week, textBundle.textFor("ReportPanel.WeekLabel")));
		spanSelectorItems.add(new LabeledItem<SpanType>(SpanType.Month, textBundle.textFor("ReportPanel.MonthLabel")));
		spanSelectorItems.add(new LabeledItem<SpanType>(SpanType.Year, textBundle.textFor("ReportPanel.YearLabel")));
	}

	/**
	 * Creates a new report panel for the given model.
	 * @param model the model to be presented
	 */
	public ReportPanel(final PresentationModel model) {
		this.model = model;

		// Initialize GUI elements.
		initialize();
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

		spanTypeSelector = getSpanTypeSelector();

		nextIntervalButton = new JButton(">");
		nextIntervalButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				ReportPanel.this.actionPerformed(event, FilterInterval.Next);
			}

		});

		previousIntervalButton = new JButton("<");
		previousIntervalButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				ReportPanel.this.actionPerformed(event, FilterInterval.Previous);
			}

		});

		JButton homeButton = new JButton(textBundle.textFor("ReportPanel.TodayLabel"));
		homeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				spanTypeSelector.setSelectedIndex(0);

				// Delegate to parent class
				ReportPanel.this.actionPerformed(event, FilterInterval.Today);
			}
		});

		dateField = new JTextField();
		dateField.setEditable(false);
		this.dateField.setText(FilterUtils.makeIntervalString(model.getFilter()));

		final JXTitledSeparator dataSeparator = new JXTitledSeparator(textBundle.textFor("ReportPanel.DataLabel")); //$NON-NLS-1$
		this.add(dataSeparator, "1, 3, 11, 0"); //$NON-NLS-1$

		this.add(spanTypeSelector, "1, 5"); //$NON-NLS-1$

		this.add(previousIntervalButton, "3, 5"); //$NON-NLS-1$

		this.add(homeButton, "5, 5"); //$NON-NLS-1$

		this.add(nextIntervalButton, "7, 5"); //$NON-NLS-1$

		this.add(new JLabel(textBundle.textFor("ReportPanel.DateLabel")), "9, 5"); //$NON-NLS-1$

		this.add(dateField, "11, 5"); //$NON-NLS-1$

		this.add(filteredActivitiesPane, "1, 7, 11, 7"); //$NON-NLS-1$
	}

	/**
	 * @return the selector for the span type
	 */
	private JComboBox getSpanTypeSelector() {
		if (spanTypeSelector == null) {
			spanTypeSelector = new JComboBox(spanSelectorItems);
			spanTypeSelector.setToolTipText(textBundle.textFor("ProjectFilterSelector.ToolTipText")); //$NON-NLS-1$

			// Read from Filter
			for (LabeledItem<SpanType> item : spanSelectorItems) {
				if (ObjectUtils.equals(item.getItem(), model.getFilter().getSpanType())) {
					spanTypeSelector.setSelectedItem(item);
					break;
				}
			}

			spanTypeSelector.addActionListener(this);
		}
		return spanTypeSelector;
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
	 * @param filterInterval 
	 * @return the filter for the selection
	 */
	@SuppressWarnings("unchecked")
	public Filter createFilter(final FilterInterval filterInterval) {
		final Filter filter = new Filter();

		// Restore criteria from previous filter
		filter.setProject(model.getFilter().getProject());
		filter.setSpanType(model.getFilter().getSpanType());
		filter.setTimeInterval(model.getFilter().getTimeInterval());

		switch (filterInterval) {
		case Next:
			FilterUtils.moveToNextInterval(filter);
			break;
		case Previous:
			FilterUtils.moveToPreviousInterval(filter);
			break;
		}

		final LabeledItem<SpanType> seletedItem = (LabeledItem<SpanType>) spanTypeSelector.getSelectedItem();
		final SpanType oldSpanType = filter.getSpanType();
		filter.setSpanType(seletedItem.getItem());

		if (oldSpanType != filter.getSpanType() || FilterInterval.Today == filterInterval) {
			filter.initTimeInterval();
		}

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
		// Store span type
		final LabeledItem<SpanType> seletedItem = (LabeledItem<SpanType>) spanTypeSelector.getSelectedItem();
		UserSettings.instance().setFilterSelectedSpanType(seletedItem.getItem());

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
	 * @param event the event that caused the change of filter criteria
	 * @param filterInterval the new filter interval
	 */
	public final void actionPerformed(final ActionEvent event, final FilterInterval filterInterval) {
		// 1. Create filter from selection.
		final Filter filter = this.createFilter(filterInterval);

		// 2. Save selection to settings.
		storeFilterInSettings();

		// 3. Save to model
		model.setFilter(filter, this);

		ReportPanel.this.dateField.setText(FilterUtils.makeIntervalString(model.getFilter()));
	}

	/**
	 * One of the filter criteria changed. So we create and apply the filter.
	 * @param event the event that caused the change of filter criteria
	 */
	public final void actionPerformed(final ActionEvent event) {
		this.actionPerformed(event, FilterInterval.Same);
	}

	/** The filter intervals. */
	private enum FilterInterval {
		Next, Same, Previous, Today
	}

}
