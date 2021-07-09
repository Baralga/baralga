package org.remast.baralga.gui.panels;

import com.google.common.eventbus.Subscribe;
import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Objects;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.events.BaralgaEvent;
import org.remast.baralga.gui.lists.ProjectFilterList;
import org.remast.baralga.gui.model.PresentationModel;
import org.remast.baralga.gui.settings.UserSettings;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.baralga.model.filter.FilterUtils;
import org.remast.baralga.model.filter.SpanType;
import org.remast.swing.util.LabeledItem;
import org.remast.util.TextResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.odell.glazedlists.swing.DefaultEventComboBoxModel;

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
	private static final Logger log = LoggerFactory.getLogger(ReportPanel.class);

	/** The model. */
	private PresentationModel model;

	/** Filter by selected project. */
	private JComboBox<LabeledItem<Project>> projectFilterSelector;

	/** List of projects by which can be filtered. */
	private ProjectFilterList projectFilterList;

	/** The panel that actually displays the filtered activities. */
	private FilteredActivitiesPane filteredActivitiesPane;
	
	/** Filter by selected span type. */
	private JComboBox<LabeledItem<SpanType>> spanTypeSelector;
	
	/** Displays the interval of the current filter. */
	private JXLabel dateField;

	/** Jump to next interval of the filter. */
	private JXButton nextIntervalButton;
	
	/** Jump to the previous interval of the filter. */
	private JXButton previousIntervalButton;
	
	/** All available options for the time span. */
	private static final Vector<LabeledItem<SpanType>> spanSelectorItems = new Vector<>();

	private JXButton homeButton;
	static {
		spanSelectorItems.add(new LabeledItem<>(SpanType.Day, textBundle.textFor("ReportPanel.DayLabel")));
		spanSelectorItems.add(new LabeledItem<>(SpanType.Week, textBundle.textFor("ReportPanel.WeekLabel")));
		spanSelectorItems.add(new LabeledItem<>(SpanType.Month, textBundle.textFor("ReportPanel.MonthLabel")));
		spanSelectorItems.add(new LabeledItem<>(SpanType.Quarter, textBundle.textFor("ReportPanel.QuarterLabel")));
		spanSelectorItems.add(new LabeledItem<>(SpanType.Year, textBundle.textFor("ReportPanel.YearLabel")));
	}

	/**
	 * Creates a new report panel for the given model.
	 * @param model the model to be presented
	 */
	public ReportPanel(final PresentationModel model) {
		this.model = model;
		this.model.getEventBus().register(this);

		// Initialize GUI elements.
		initialize();
	}

	/**
	 * Set up GUI components.
	 */
	private void initialize() {
		// Obtain a reusable constraints object to place components in the grid.
		filteredActivitiesPane = new FilteredActivitiesPane(model);

		final double border = 5;
		final double[][] size = {
				{ border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.PREFERRED, 10, TableLayout.PREFERRED, border, TableLayout.FILL, border}, // Columns
				{ TableLayout.PREFERRED, border, TableLayout.PREFERRED, border, TableLayout.FILL, border }  // Rows
		};
		this.setLayout(new TableLayout(size));

		spanTypeSelector = getSpanTypeSelector();

		nextIntervalButton = new JXButton("");
		nextIntervalButton.addActionListener(event -> ReportPanel.this.actionPerformed(event, FilterInterval.Next));
		nextIntervalButton.setContentAreaFilled(false);
		nextIntervalButton.setRolloverEnabled(true);
		nextIntervalButton.setIcon(new ImageIcon(BaralgaMain.class.getResource("/icons/Play-1-Disabled-icon.png")));
		nextIntervalButton.setRolloverIcon(new ImageIcon(BaralgaMain.class.getResource("/icons/Play-icon.png")));
		
		previousIntervalButton = new JXButton("");
		previousIntervalButton.addActionListener(event -> ReportPanel.this.actionPerformed(event, FilterInterval.Previous));
		previousIntervalButton.setContentAreaFilled(false);
		nextIntervalButton.setRolloverEnabled(true);
		previousIntervalButton.setIcon(new ImageIcon(BaralgaMain.class.getResource("/icons/Play-1-Disabled-icon-left.png")));
		previousIntervalButton.setRolloverIcon(new ImageIcon(BaralgaMain.class.getResource("/icons/Play-icon-left.png")));

		homeButton = new JXButton();
		homeButton.setIcon(new ImageIcon(BaralgaMain.class.getResource("/icons/user-home.png")));
		homeButton.setContentAreaFilled(false);
		homeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				homeButton.setBackgroundPainter(new org.jdesktop.swingx.painter.CompoundPainter<JXButton>(new MattePainter((Color) UIManager.getLookAndFeelDefaults().get("Button.light")), new GlossPainter()));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				homeButton.setBackgroundPainter(null);
			}
		});

		homeButton.setToolTipText(textBundle.textFor("ReportPanel.TodayButton.ToolTipText"));
		homeButton.addActionListener(event -> {
			spanTypeSelector.setSelectedIndex(0);

			// Delegate to parent class
			ReportPanel.this.actionPerformed(event, FilterInterval.Today);
		});

		dateField = new JXLabel();
		final JXHeader dataSeparator = new JXHeader(textBundle.textFor("ReportPanel.DataLabel"), null); //$NON-NLS-1$
		this.add(dataSeparator, "0, 0, 11, 0"); //$NON-NLS-1$

		this.add(spanTypeSelector, "1, 2"); //$NON-NLS-1$

		this.add(previousIntervalButton, "3, 2"); //$NON-NLS-1$

		this.add(homeButton, "5, 2"); //$NON-NLS-1$

		this.add(nextIntervalButton, "7, 2"); //$NON-NLS-1$

		this.add(dateField, "9, 2, 12, 2"); //$NON-NLS-1$

		this.add(filteredActivitiesPane, "1, 4, 11, 4"); //$NON-NLS-1$
		
		updateLabelsAndTooltips();
	}

	/**
	 * @return the selector for the span type
	 */
	private JComboBox<LabeledItem<SpanType>> getSpanTypeSelector() {
		if (spanTypeSelector == null) {
			spanTypeSelector = new JComboBox<>(spanSelectorItems);
			spanTypeSelector.setToolTipText(textBundle.textFor("SpanTypeSelector.ToolTipText")); //$NON-NLS-1$

			// Read from Filter
			for (LabeledItem<SpanType> item : spanSelectorItems) {
				if (Objects.equals(item.getItem(), model.getFilter().getSpanType())) {
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
	private JComboBox<LabeledItem<Project>> getProjectFilterSelector() {
		if (projectFilterSelector == null) {
			projectFilterList = model.getProjectFilterList();
			projectFilterSelector = new JComboBox<LabeledItem<Project>>(
					new DefaultEventComboBoxModel<LabeledItem<Project>>(projectFilterList.getProjectList())
			);
			projectFilterSelector.setToolTipText(textBundle.textFor("ProjectFilterSelector.ToolTipText")); //$NON-NLS-1$

			// Select first entry
			if (!projectFilterList.getProjectList().isEmpty()) {
				projectFilterSelector.setSelectedIndex(0);
			}

			// Read from Settings.
			final String selectedProjectId = UserSettings.instance().getFilterSelectedProjectId();
			if (selectedProjectId != null) {
				for (LabeledItem<Project> item : projectFilterList.getProjectList()) {
					if (Objects.equals(item.getItem().getId(), selectedProjectId)) {
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
			String projectId = project.getId();
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
		// Create filter from selection.
		final Filter filter = this.createFilter(filterInterval);

		// Save selection to settings.
		storeFilterInSettings();

		// Save to model
		model.setFilter(filter, this);

		updateLabelsAndTooltips();
	}

	/** Updates all labels and tooltips depending on filter information. */
	private void updateLabelsAndTooltips() {
		// Date Field
		this.dateField.setText(FilterUtils.makeIntervalString(model.getFilter()));
		this.dateField.setToolTipText(FilterUtils.makeToolTipText(model.getFilter()));

		// Tooltips of next and previous interval buttons
		String spanTypeLabel = null;
		switch (model.getFilter().getSpanType()) {
		case Day:
			spanTypeLabel = textBundle.textFor("ReportPanel.DayLabel");
			break;
		case Week:
			spanTypeLabel = textBundle.textFor("ReportPanel.WeekLabel");
			break;
		case Month:
			spanTypeLabel = textBundle.textFor("ReportPanel.MonthLabel");
			break;
		case Quarter:
			spanTypeLabel = textBundle.textFor("ReportPanel.QuarterLabel");
			break;
		case Year:
			spanTypeLabel = textBundle.textFor("ReportPanel.YearLabel");
			break;
		}
		
		nextIntervalButton.setToolTipText(textBundle.textFor("ReportPanel.NextIntervalButton.ToolTipText", spanTypeLabel));
		previousIntervalButton.setToolTipText(textBundle.textFor("ReportPanel.PreviousIntervalButton.ToolTipText", spanTypeLabel));
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
